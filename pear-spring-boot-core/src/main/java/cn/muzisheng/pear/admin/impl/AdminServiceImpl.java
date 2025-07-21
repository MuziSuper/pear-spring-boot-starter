package cn.muzisheng.pear.admin.impl;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.TimeTransitionUtil;
import cn.muzisheng.pear.admin.AdminService;
import cn.muzisheng.pear.annotation.Verification;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.*;
import cn.muzisheng.pear.handler.BuildContext;
import cn.muzisheng.pear.initialize.AdminContainer;
import cn.muzisheng.pear.mapper.AdminMapper;
import cn.muzisheng.pear.model.*;
import cn.muzisheng.pear.params.AdminQueryResult;
import cn.muzisheng.pear.params.Filter;
import cn.muzisheng.pear.params.QueryForm;
import cn.muzisheng.pear.service.ConfigService;
import cn.muzisheng.pear.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {
    private final AdminMapper adminMapper;
    private final ObjectMapper objectMapper=new ObjectMapper();
    private final UserService userService;
    private final ConfigService configService;
    private final ResourcePatternResolver resourceLoader;
    private static final Logger LOG = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    public AdminServiceImpl(AdminMapper adminMapper, UserService userService, ConfigService configService, ResourcePatternResolver resourceLoader) {
        this.configService = configService;
        this.adminMapper = adminMapper;
        this.userService = userService;
        this.resourceLoader = resourceLoader;
    }

    @Verification(UserVerify = true)
    @Override
    public ResponseEntity<Result<Object>> handleQueryOrGetOne(HttpServletRequest request, String model, AdminObject adminObject, QueryForm queryForm) {
        Response<Object> response = new Response<>();
        // 如果请求体中无数据，就获取单个主键排序第一位的数据，
        // 根据query传参，来获取数据库中存储的某表的数据，
        // 若自定义了beforeRender就调用， 将实例进行序列化传给客户端
        if (request.getContentLength() <= 0) {
            return handleGetOne(request, model, adminObject, queryForm);
        }
        // 处理请求体中的数据
        queryForm.defaultPrepareQuery();
        AdminQueryResult result = queryObjects(request, model, adminObject, queryForm);
        response.setData(result);
        return response.value();
    }

    /**
     * 根据请求头与请求体的参数创建admin
     *
     * @param adminObject 通用类对象
     * @param data        请求体
     * @return 创建的admin数据
     **/
//    @Verification(UserVerify = true)
    @Override
    public ResponseEntity<Result<Map<String, Object>>> handleCreate(HttpServletRequest request, String model, AdminObject adminObject, Map<String, Object> data) {
        Response<Map<String, Object>> response = new Response<>();
        // 根据query值遍历并获取其映射数据库表的主键或唯一键键值对
        Map<String, Object> params = getPrimaryValues(request, adminObject);
        if (params.isEmpty() && data == null) {
            throw new IllegalException("Both the query parameter and the request body are null.");
        }
        // 获取整合好的数据
        Map<String, Object> res = unmarshalFrom(adminObject, data, params, true);

        if (adminObject.getBeforeCreate() != null) {
            try {
                adminObject.getBeforeCreate().execute(request, adminObject, res);
            } catch (Exception e) {
                LOG.error("beforeCreate error", e);
                throw new HookException("beforeCreate error: " + e.getMessage());
            }
        }
        try {
            if (adminMapper.create(model, res) < 1) {
                LOG.error("admin creation failed. {}", res.get("email") != null ? "email: " + res.get("email") : "");
                throw new AdminErrorException("admin creation failed. " + (res.get("email") != null ? "email: " + res.get("email") : ""));
            }
        }catch (Exception e){
            throw new SqlStatementException(e.getMessage());
        }
        if (adminObject.getBeforeRender() != null) {
            try {
                adminObject.getBeforeRender().execute(request, adminObject, res);
            } catch (Exception e) {
                LOG.error("beforeRender error", e);
                throw new HookException("beforeRender error");
            }
        }
        response.setData(res);
        return response.value();
    }
    /**
     * 根据请求头与请求体的参数更新admin，请求头中要有主键或唯一键信息
     *
     * @param adminObject 通用类对象
     * @param data        请求体
     * @return 更新的admin数据
     **/
//    @Verification(UserVerify = true)
    @Override
    public ResponseEntity<Result<Map<String, Object>>> handleUpdate(HttpServletRequest request, String model, AdminObject adminObject, Map<String, Object> data) {
        Map<String, Object> keys = getPrimaryValues(request, adminObject);
        if (keys.isEmpty() || data.isEmpty()) {
            throw new IllegalException("The parameter or primary key is empty.");
        }
        List<Map<String, Object>> list;
        try {
            list = adminMapper.selectFirst(model, keys);
        } catch (Exception e){
            throw new SqlStatementException(e.getMessage());
        }
        if (list.isEmpty()) {
            throw new IllegalException("The primary key does not exist.");
        }
        Map<String, Object> res = unmarshalFrom(adminObject, data, keys, false);
        if (adminObject.getBeforeUpdate() != null) {
            try {
                adminObject.getBeforeUpdate().execute(request, adminObject, res);
            } catch (Exception e) {
                LOG.error("beforeUpdate error", e);
                throw new HookException("beforeUpdate error");
            }
        }
        try {
            if (adminMapper.update(model, keys, res) < 1) {
                LOG.error("admin update failed. {}", res.get("email") != null ? "email: " + res.get("email") : "");
                throw new AdminErrorException("admin update failed. " + (res.get("email") != null ? "email: " + res.get("email") : ""));
            }
        }catch (Exception e){
            throw new SqlStatementException(e.getMessage());
        }
        Response<Map<String, Object>> response = new Response<>();
        response.setData(res);
        return response.value();
    }

    @Override
    public ResponseEntity<Result<Map<String, Object>>> handleDelete(HttpServletRequest request, String model, AdminObject adminObject) {
        Response<Map<String, Object>> response = new Response<>();
        Map<String, Object> keys = getPrimaryValues(request, adminObject);
        if(keys.isEmpty()){
            throw new IllegalException("The primary key is empty.");
        }
        List<Map<String, Object>> list;
        try{
            list = adminMapper.selectFirst(model, keys);
        }catch (Exception e){
            throw new SqlStatementException(e.getMessage());
        }
        if (list.isEmpty()) {
            throw new IllegalException("The primary key does not exist.");
        }
        if(adminObject.getBeforeDelete()!= null){
            try {
                adminObject.getBeforeDelete().execute(request, adminObject, list.get(0));
            } catch (Exception e) {
                LOG.error("beforeDelete error", e);
                throw new HookException("beforeDelete error");
            }
        }
        try{
         if (adminMapper.delete(model, keys) < 1) {
            LOG.error("admin delete failed. The key is {}", keys);
        }
        }catch (Exception e){
            throw new SqlStatementException(e.getMessage());
        }
        response.setData(list.get(0));
        return response.value();
    }


    @Override
    public ResponseEntity<Result<Map<String, Object>>> handleAction(AdminObject adminObject) {
        return null;
    }

    @Verification
    @Override
    public ResponseEntity<Result<Map<String, Object>>> adminJson(HttpServletRequest request) {
        Response<Map<String, Object>> response = new Response<>();
        Map<String, Object> map = handleAdminJson(request, AdminContainer.getAllAdminObjects(), (req, data) -> {
            data.put("dashboard", configService.getValue(Constant.KEY_ADMIN_DASHBOARD));
            return data;
        });
        response.setData(map);
        return response.value();
    }
    /**
     *  获取后台文件路径
     **/
//    @Verification
    @Override
    public ResponseEntity<Result<Map<String, Object>>> adminFilepath(HttpServletRequest request) {
        List<String> cssFiles = new ArrayList<>();
        List<String> jsFiles = new ArrayList<>();
        try {
            Resource[] resources = resourceLoader.getResources("classpath:static/**/*");
            for (Resource resource : resources) {
                if (resource.getFilename() != null && !resource.getFilename().isEmpty() && resource.getFilename().endsWith(".css")) {
                    cssFiles.add(resource.getFilename());
                }
                if (resource.getFilename() != null && !resource.getFilename().isEmpty() && resource.getFilename().endsWith(".js")) {
                    jsFiles.add(resource.getFilename());
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new GeneralException();
        }
        Response<Map<String, Object>> response = new Response<>();
        Map<String, Object> map = new HashMap<>();
        map.put("Scripts", jsFiles);
        map.put("Styles", cssFiles);
        map.put("Dashboard", configService.getValue(Constant.KEY_ADMIN_DASHBOARD));
        map.put("Objects", AdminContainer.getAllAdminObjects());
        response.setData(map);
        return response.value();
    }

    /**
     * 添加主键值到请求体中,并将请求体数据经过处理后返回,过滤了不可编辑字段
     *
     * @param adminObject 通用类对象
     * @param body        请求体
     * @param params      query中参数集合
     * @param initial     是否为创建对象
     * @return 处理后的请求体
     * @throws IllegalException 请求体数据为空
     **/
    public Map<String, Object> unmarshalFrom(AdminObject adminObject, Map<String, Object> body, Map<String, Object> params, boolean initial) {
        if (body == null) {
            body = new HashMap<>();
        }
        // 删除不可编辑字段
//        if (initial) {
            if (adminObject.getEdits() != null) {
                body.keySet().removeIf(field -> !adminObject.getEdits().contains(field));
            }
//        }
        // 添加body中没有的query参数，整合数据到body中
        for (String field : params.keySet()) {
            if (!body.containsKey(field)) {
                body.put(field, params.get(field));
            }
        }
        if (body.isEmpty()) {
            throw new IllegalException("The processed dataset is empty.");
        }
        // 将body中的键值转换为目标类型
        for (AdminField adminField : adminObject.getFields()) {
            // 获取body中的此字段数据
            Object val = body.get(adminField.getFieldName());
            // 不存在则跳过
            if (val == null) {
                continue;
            }
            // 获取目标类型
            Class<?> targetClass = adminField.getType();
            // 如果某一数据为Map,则尝试获取其value的键值
            if (val instanceof Map<?, ?> valMap) {
                for (Map.Entry<?, ?> entry2 : valMap.entrySet()) {
                    if (entry2.getKey() instanceof String) {
                        if (entry2.getKey().equals("value")) {
                            val = entry2.getValue();
                        }
                    } else {
                        LOG.warn("The parameter type passed in is Map, but the data of the value key is not obtained or the data types do not match.");
                    }
                }
            }

            body.put(adminField.getFieldName(), convertValue(targetClass, val));
        }
        flushTime(adminObject, body, initial);
        if (adminObject.getIgnores() != null) {
            for (String key : adminObject.getIgnores().keySet()) {
                body.remove(key);
            }
        }
        return body;
    }

    private Object convertValue(Class<?> targetClass, Object val) {
        if (val.getClass() == targetClass) {
            if (targetClass.equals(Boolean.class)) {
                if ((Boolean) val) {
                    val = 1;
                } else {
                    val = 0;
                }
            }
            return val;
        }
        try {
            if (targetClass.equals(Integer.class)) {
                val = formatAsInt(val);
            } else if (targetClass.equals(Long.class)) {
                val = formatAsLong(val);
            } else if (targetClass.equals(Double.class)) {
                val = formatAsDouble(val);
            } else if (targetClass.equals(Float.class)) {
                val = formatAsFloat(val);
            } else if (targetClass.equals(Boolean.class)) {
                String valString = (String) val;
                if (valString.equalsIgnoreCase("true") || valString.equalsIgnoreCase("on") || valString.equalsIgnoreCase("yes")|| valString.equals("1")) {
                    val = 1;
                } else if (valString.equalsIgnoreCase("false") || valString.equalsIgnoreCase("off") || valString.equalsIgnoreCase("no")|| valString.equals("0")) {
                    val = 0;
                } else {
                    val = 0;
                }
            } else if (targetClass.equals(LocalDateTime.class)) {
                val = TimeTransitionUtil.stringToLocalDateTime((String) val);
            } else if (targetClass.equals(LocalDate.class)) {
                val = TimeTransitionUtil.stringToLocalDateTime((String) val);
            } else if (targetClass.equals(Date.class)) {
                val = TimeTransitionUtil.stringToDate((String) val);
            } else if (targetClass.equals(LocalTime.class)) {
                val = TimeTransitionUtil.stringToLocalTime((String) val);
            } else {
                try {
                    val = objectMapper.convertValue((String) val, targetClass);
                } catch (IllegalArgumentException e) {
                    val = null;
                }
            }
        } catch (TimeException e) {
            LOG.info("target type:{}, but cannot parse date: {}", targetClass, val);
            val = null;
        }
        return val;
    }

    private Object formatAsFloat(Object val) {
        if (val instanceof Float) {
            return val;
        } else if (val instanceof Double) {
            val = ((Double) val).floatValue();
        } else if (val instanceof Integer) {
            val = ((Integer) val).floatValue();
        } else if (val instanceof Long) {
            val = ((Long) val).floatValue();
        } else if (val instanceof String) {
            if (val.equals("")) {
                val = 0;
                return val;
            }
            val = Float.parseFloat((String) val);
        } else {
            val = 0;
        }
        return val;
    }

    private Object formatAsDouble(Object val) {
        if (val instanceof Double) {
            return val;
        } else if (val instanceof Float) {
            val = ((Float) val).doubleValue();
        } else if (val instanceof Integer) {
            val = ((Integer) val).doubleValue();
        } else if (val instanceof Long) {
            val = ((Long) val).doubleValue();
        } else if (val instanceof String) {
            if (val.equals("")) {
                val = 0;
                return val;
            }
            val = Double.parseDouble((String) val);
        } else {
            val = 0;
        }
        return val;
    }

    private Object formatAsLong(Object val) {
        if (val instanceof Long) {
            return val;
        } else if (val instanceof Double) {
            val = ((Double) val).longValue();
        } else if (val instanceof Float) {
            val = ((Float) val).longValue();
        } else if (val instanceof Integer) {
            val = ((Integer) val).longValue();
        } else if (val instanceof String) {
            if (val.equals("")) {
                val = 0;
                return val;
            }
            val = Long.parseLong((String) val);
        } else {
            val = 0;
        }
        return val;
    }

    private Object formatAsInt(Object val) {
        if (val instanceof Integer) {
            return val;
        } else if (val instanceof Double) {
            val = ((Double) val).intValue();
        } else if (val instanceof Float) {
            val = ((Float) val).intValue();
        } else if (val instanceof Long) {
            val = ((Long) val).intValue();
        } else if (val instanceof String) {
            if (val.equals("")) {
                val = 0;
                return val;
            }
            val = Integer.parseInt((String) val);
        } else {
            val = 0;
        }
        return val;
    }

    /**
     * 获取站点信息与pear数据集
     **/
    private Map<String, Object> handleAdminJson(HttpServletRequest request, List<AdminObject> AdminObjects, BuildContext buildContext) {
        Map<String, Object> res = new HashMap<>();
        List<AdminObject> viewObjects = new ArrayList<>();
        for (AdminObject adminObject : AdminObjects) {
            // adminObject逐个进行钩子权限检查（用户级）,目前未约定返回结果，只根据抛出的异常判断是否通过检查
            if (adminObject.getAccessCheck() != null) {
                try {
                    adminObject.getAccessCheck().execute(request, adminObject);
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                    continue;
                }
            }
            User user=userService.currentUser(request);
            if(user==null){
                throw new AuthorizationException("未登录");
            }
            adminObject.buildPermissions(user);
            viewObjects.add(adminObject);
        }
        // 获取渲染页面的所有站点信息
        Map<String, Object> siteCtx = getRenderPageContext(request);
        //
        if (buildContext != null) {
            buildContext.execute(request, siteCtx);
        }
        res.put("objects", viewObjects);
        res.put("user", userService.currentUser(request));
        res.put("site", siteCtx);
        return res;
    }

    /**
     * 获取渲染页面的所有站点信息
     **/
    public Map<String, Object> getRenderPageContext(HttpServletRequest request) {
        configService.loadAutoLoads();
        String loginNext = request.getParameter("next");
        if (loginNext == null || loginNext.isEmpty()) {
            loginNext = configService.getValue(Constant.KEY_SITE_LOGIN_NEXT);
        }
        HashMap<String, Object> res = new HashMap<>();
        res.put("loginNext", loginNext);
        HashMap<String, Object> site = new HashMap<>();
        site.put("Url", configService.getValue(Constant.KEY_SITE_URL));
        site.put("Name", configService.getValue(Constant.KEY_SITE_NAME));
        site.put("Admin", configService.getValue(Constant.KEY_SITE_ADMIN));
        site.put("Keywords", configService.getValue(Constant.KEY_SITE_KEYWORDS));
        site.put("Description", configService.getValue(Constant.KEY_SITE_DESCRIPTION));
        site.put("GA", configService.getValue(Constant.KEY_SITE_GA));
        site.put("LogoUrl", configService.getValue(Constant.KEY_SITE_LOGO_URL));
        site.put("FaviconUrl", configService.getValue(Constant.KEY_SITE_FAVICON_URL));
        site.put("TermsUrl", configService.getValue(Constant.KEY_SITE_TERMS_URL));
        site.put("PrivacyUrl", configService.getValue(Constant.KEY_SITE_PRIVACY_URL));
        site.put("SigninUrl", configService.getValue(Constant.KEY_SITE_SIGNIN_URL));
        site.put("SignupUrl", configService.getValue(Constant.KEY_SITE_SIGNUP_URL));
        site.put("LogoutUrl", configService.getValue(Constant.KEY_SITE_LOGOUT_URL));
        site.put("ResetPasswordUrl", configService.getValue(Constant.KEY_SITE_RESET_PASSWORD_URL));
        site.put("SigninApi", configService.getValue(Constant.KEY_SITE_SIGNIN_API));
        site.put("SignupApi", configService.getValue(Constant.KEY_SITE_SIGNUP_API));
        site.put("ResetPasswordDoneApi", configService.getValue(Constant.KEY_SITE_RESET_PASSWORD_DONE_API));
        site.put("ChangeEmailDoneApi", configService.getValue(Constant.KEY_SITE_CHANGE_EMAIL_DONE_API));
        site.put("UserIdType", configService.getValue(Constant.KEY_SITE_USER_ID_TYPE));
        res.put("site", site);
        return res;
    }

    /**
     * 拼接数据库db的查询子句,筛选子句，排序子句，获取查询总数，
     * 携带外键连接的表其所有数据，将多条总数据放入映射集合与列表中，
     * 封装在r AdminQueryResult中
     **/
    private AdminQueryResult queryObjects(HttpServletRequest request, String model, AdminObject adminObject, QueryForm queryForm) {
        AdminQueryResult r = new AdminQueryResult();
        if (queryForm.isForeignMode()) {
            queryForm.setLimit(0);
        }
        String whereClause = "";
        if(queryForm.getFilters() != null) {
            for (Filter filter : queryForm.getFilters()) {
                String queryClause = filter.getQueryClause();
                if (queryClause != null) {
                    // 如果是like操作，则构造LIKE语句whereClause，先不带WHERE,下文还有限定字筛选
                    if (Constant.FILTER_OP_LIKE.equals(filter.getOp())) {
                        if (filter.getValue() instanceof Object[] values) {
                            List<String> conditions = new ArrayList<>();
                            for (Object value : values) {
                                if (value instanceof String strValue) {
                                    if (strValue.isEmpty()) {
                                        continue;
                                    }
                                    // 转义双引号，并构造 LIKE 语句
                                    String escapedValue = strValue.replace("\"", "\\\"");
                                    String condition = String.format("`%s`.`%s` LIKE '%%%s%%'", adminObject.getTableName(), filter.getName(), escapedValue);
                                    conditions.add(condition);
                                }
                            }
                            if (!conditions.isEmpty()) {
                                // 因为多个筛选条件则拼接OR语句
                                whereClause = String.join(" OR ", conditions);
                            }
                        } else {
                            Object value = filter.getValue();
                            if (value instanceof String strValue) {
                                if (strValue.isEmpty()) {
                                    continue;
                                }
                                String escapedValue = strValue.replace("\"", "\\\"");
                                whereClause = String.format("`%s`.`%s` LIKE '%%%s%%'", adminObject.getTableName(), filter.getName(), escapedValue);
                            }
                        }
                        // 如果是between操作，则构造BETWEEN语句,也不带WHERE
                    } else if (Constant.FILTER_OP_BETWEEN.equals(filter.getOp())) {
                        if (filter.getValue() instanceof List<?> values) {
                            if (values.size() == 2) {
                                // `user`.name BETWEEN 'John' AND 'Doe'
                                if ((values.get(0) instanceof Integer && values.get(1) instanceof Integer) || (values.get(0) instanceof Float && values.get(1) instanceof Float)) {
                                    whereClause = String.format("`%s`.%s %s %s AND %s", adminObject.getTableName(), filter.getName(), queryClause, values.get(0), values.get(1));
                                } else {
                                    whereClause = String.format("`%s`.%s %s '%s' AND '%s'", adminObject.getTableName(), filter.getName(), queryClause, values.get(0), values.get(1));
                                }
                            } else {
                                throw new GeneralException("Expected an Object[] value with length 2 for between operation but got " + values.size());
                            }
                        } else {
                            throw new GeneralException("Expected an Object[] value for between operation but got " + filter.getValue().getClass().getName());
                        }
                    } else {
                        whereClause = String.format("`%s`.%s'%s'", adminObject.getTableName(), queryClause, filter.getValue());
                    }
                }
            }
        }
        // 排序子句，不加ORDER BY
        List<Order> orders;
        StringBuilder orderClause = new StringBuilder();
        if (queryForm.getOrders() != null && !queryForm.getOrders().isEmpty()) {
            orders = queryForm.getOrders();
        } else {
            orders = adminObject.getOrders();
        }
        if (orders != null) {
            for (Order order : orders) {
                if (!orderClause.isEmpty()) {
                    orderClause.append(",");
                }
                orderClause.append(String.format("`%s`.`%s` %s", adminObject.getTableName(), order.getName(), order.getOp().toUpperCase()));
            }
        }
        StringBuilder whereClauseBuilder = new StringBuilder();
        if (queryForm.getKeyword() != null) {
            for (String searchField : adminObject.getSearches()) {
                if (!whereClauseBuilder.isEmpty()) {
                    whereClauseBuilder.append(" OR ");
                }
                whereClauseBuilder.append(String.format("`%s`.`%s` LIKE '%%%s%%'", adminObject.getTableName(), searchField, queryForm.getKeyword()));
            }
        }
        StringBuilder showClause = new StringBuilder();
        if (adminObject.getShows() != null) {
            for (String viewField : adminObject.getShows()) {
                if (!showClause.isEmpty()) {
                    showClause.append(" ,");
                }
                showClause.append(String.format("`%s`.`%s`", adminObject.getTableName(), viewField));
            }
        } else {
            showClause.append("*");
        }
        List<Map<String, Object>> result = adminMapper.query(adminObject.getTableName(), showClause.toString(), whereClause, orderClause.toString(), whereClauseBuilder.toString(), queryForm.getLimit());
        if (adminObject.getBeforeRender() != null) {
            try {
                adminObject.getBeforeRender().execute(request, adminObject, result);
            } catch (Exception e) {
                LOG.warn("BeforeRender error: {}", e.getMessage());
                throw new GeneralException("BeforeRender error: " + e.getMessage());

            }
        }
        r.setPos(queryForm.getPos());
        r.setLimit(queryForm.getLimit());
        r.setKeyword(queryForm.getKeyword());
        r.setItems(result);
        r.setTotalCount(result.size());
        return r;
    }


    /**
     * 客户端通过url的参数进行数据查询 (不通过请求体传入参数)
     **/
    private ResponseEntity<Result<Object>> handleGetOne(HttpServletRequest request, String model, AdminObject adminObject, QueryForm queryForm) {
        Response<Object> response = new Response<>();
        // 根据query值遍历并获取其映射数据库表的主键或外键键值对
        Map<String, Object> queryMap = getPrimaryValues(request, adminObject);
        if (queryMap.isEmpty()) {
            LOG.error("invalid primary key");
            throw new IllegalException("invalid primary key");
        }
        // 使用预加载，获取对象及其外键链接的数据
        Object result = adminMapper.selectFirst(adminObject.getTableName(), queryMap);
        if (result == null) {
            LOG.error("Data cannot be found.");
            throw new GeneralException("Data cannot be found.");
        }
        if (adminObject.getBeforeRender() != null) {
            try {
                adminObject.getBeforeRender().execute(request, adminObject, result);
            } catch (Exception e) {
                LOG.warn("BeforeRender error: {}", e.getMessage());
                throw new GeneralException("BeforeRender error: " + e.getMessage());
            }
        }
        // 序列化对象，将result转为map[string]any
        Map<String, Object> data = marshalOne(request, adminObject, result);
        response.setData(data);
        return response.value();
    }

    /**
     * 序列化对象，将result转为map[string]any,展示可shows字段
     **/
    private Map<String, Object> marshalOne(HttpServletRequest request, AdminObject adminObject, Object result) {
        Map<String, Object> data = new HashMap<>();
        if (result instanceof List<?> list) {
            for (Object obj : list) {
                if (obj instanceof Map<?, ?> map) {
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        if (entry.getKey() instanceof String) {
                            if (adminObject.getShows().contains((String) entry.getKey())) {
                                data.put((String) entry.getKey(), entry.getValue());
                            }
                        }
                    }
                }
            }
            if (data.isEmpty()) {
                LOG.warn("The dataset rendered by BeforeRender is not of type List<Map<String,Object>>.");
            }

            if (adminObject.getAdminViewOnSite() != null) {
                try {
                    data.put("_adminExtra", adminObject.getAdminViewOnSite().execute(request, adminObject, result));
                } catch (Exception e) {
                    LOG.error("AdminViewOnSite error");
                }
            }
        }
        return data;
    }

    /**
     * 根据query值遍历并获取其映射数据库表的主键或唯一键键值对
     **/
    private Map<String, Object> getPrimaryValues(HttpServletRequest request, AdminObject adminObject) {
        Map<String, Object> queryMap = new HashMap<>();
        boolean keysExist = false;
        if (adminObject.getPrimaryKeys() != null) {
            for (String field : adminObject.getPrimaryKeys()) {
                String param = request.getParameter(field);
                if (param != null) {
                    queryMap.put(field, param);
                    keysExist = true;
                }
            }
        }
        if (keysExist) {
            return queryMap;
        }
        if (adminObject.getUniqueKeys() != null) {
            for (String field : adminObject.getUniqueKeys()) {
                String param = request.getParameter(field);
                if (param != null) {
                    queryMap.put(field, param);
                }
            }
        }
        return queryMap;
    }

    /**
     * 在res中添加插入更新时间
     *
     * @param adminObject 通用类对象
     * @param res        处理体
     * @param isCreate   新创建
     **/
    private void flushTime(AdminObject adminObject, Map<String, Object> res, boolean isCreate) {
        adminObject.getFields().forEach(field -> {
            if (isCreate) {
                if (field.getIsAutoInsertTime()) {
                    res.put(field.getFieldName(), LocalDateTime.now());
                }
            }
            if (field.getIsAutoUpdateTime()) {
                res.put(field.getFieldName(), LocalDateTime.now());
            }
        });
    }
}
