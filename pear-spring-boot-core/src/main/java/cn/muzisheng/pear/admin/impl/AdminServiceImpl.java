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
import cn.muzisheng.pear.params.SqlQuery;
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
    private final ObjectMapper objectMapper = new ObjectMapper();
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
    @Verification(Permission = Constant.ADMIN_SELECT)
    @Override
    public ResponseEntity<Result<Object>> handleQueryOrGetOne(HttpServletRequest request, String model, AdminObject adminObject, QueryForm queryForm) {
        Response<Object> response = new Response<>();
        // 如果请求体中无数据，就获取单个主键排序第一位的数据，
        // 根据query传参，来获取数据库中存储的某表的数据，
        // 若自定义了beforeRender就调用， 将实例进行序列化传给客户端
        if (request.getContentLength() <= 0) {
            if (!getPrimaryValues(request, adminObject).isEmpty()) {
                return handleGetOne(request, model, adminObject, queryForm);
            }
            queryForm = new QueryForm();
            AdminQueryResult result = queryObjects(request, model, adminObject, queryForm);
            response.setData(result);
            return response.value();
        }
        // 处理请求体中的数据
        if (queryForm == null) {
            queryForm = new QueryForm();
        }
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
    @Verification(Permission = Constant.ADMIN_CREATE)
    @Override
    public ResponseEntity<Result<Map<String, Object>>> handleCreate(HttpServletRequest request, String model, AdminObject adminObject, Map<String, Object> data) {
        Response<Map<String, Object>> response = new Response<>();
        // 根据query值遍历并获取其映射数据库表的主键或唯一键键值对
        Map<String, Object> params = getPrimaryValues(request, adminObject);
        if (data == null || data.isEmpty()) {
            throw new IllegalException("The request body cannot be empty.");
        }
        // 获取整合好的数据
        Map<String, Object> res = unmarshalFrom(adminObject, data, params, true);
        validateRequiredFields(adminObject, res);

        if (adminObject.getBeforeCreate() != null) {
            try {
                adminObject.getBeforeCreate().execute(request, adminObject, res);
            } catch (Exception e) {
                LOG.error("beforeCreate error", e);
                throw new HookException("beforeCreate error: " + e.getMessage());
            }
        }
        validateDataFields(adminObject, res);
        try {
            if (adminMapper.create(adminObject.getTableName(), res) < 1) {
                LOG.error("admin creation failed. {}", res.get("email") != null ? "email: " + res.get("email") : "");
                throw new AdminErrorException("admin creation failed. " + (res.get("email") != null ? "email: " + res.get("email") : ""));
            }
        } catch (Exception e) {
            throw new SqlStatementException(e.getMessage());
        }
        if (adminObject.getAfterCreate() != null) {
            try {
                adminObject.getAfterCreate().execute(request, adminObject, res);
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
    @Verification(Permission = Constant.ADMIN_UPDATE)
    @Override
    public ResponseEntity<Result<Map<String, Object>>> handleUpdate(HttpServletRequest request, String model, AdminObject adminObject, Map<String, Object> data) {
        Map<String, Object> keys = getPrimaryValues(request, adminObject);
        if (keys.isEmpty() || data == null || data.isEmpty()) {
            throw new IllegalException("The parameter or primary key is empty.");
        }
        List<Map<String, Object>> list;
        try {
            list = adminMapper.selectFirst(adminObject.getTableName(), keys);
        } catch (Exception e) {
            throw new SqlStatementException(e.getMessage());
        }
        if (list.isEmpty()) {
            throw new IllegalException("The primary key does not exist.");
        }
        Map<String, Object> res = unmarshalFrom(adminObject, data, keys, false);
        // 运行beforeUpdate钩子
        if (adminObject.getBeforeUpdate() != null) {
            try {
                adminObject.getBeforeUpdate().execute(request, adminObject, res);
            } catch (Exception e) {
                LOG.error("beforeUpdate error", e);
                throw new HookException("beforeUpdate error");
            }
        }
        validateDataFields(adminObject, res);
        try {
            if (adminMapper.update(adminObject.getTableName(), keys, res) < 1) {
                LOG.error("admin update failed. {}", res.get("email") != null ? "email: " + res.get("email") : "");
                throw new AdminErrorException("admin update failed. " + (res.get("email") != null ? "email: " + res.get("email") : ""));
            }
        } catch (Exception e) {
            throw new SqlStatementException(e.getMessage());
        }
        Response<Map<String, Object>> response = new Response<>();
        response.setData(res);
        return response.value();
    }

    @Verification(Permission = Constant.ADMIN_DELETE)
    @Override
    public ResponseEntity<Result<Map<String, Object>>> handleDelete(HttpServletRequest request, String model, AdminObject adminObject) {
        Response<Map<String, Object>> response = new Response<>();
        Map<String, Object> keys = getPrimaryValues(request, adminObject);
        if (keys.isEmpty()) {
            throw new IllegalException("The primary key is empty.");
        }
        List<Map<String, Object>> list;
        try {
            list = adminMapper.selectFirst(adminObject.getTableName(), keys);
        } catch (Exception e) {
            throw new SqlStatementException(e.getMessage());
        }
        if (list.isEmpty()) {
            throw new IllegalException("The primary key does not exist.");
        }
        if (adminObject.getBeforeDelete() != null) {
            try {
                adminObject.getBeforeDelete().execute(request, adminObject, list.get(0));
            } catch (Exception e) {
                LOG.error("beforeDelete error", e);
                throw new HookException("beforeDelete error");
            }
        }
        try {
            if (adminMapper.delete(adminObject.getTableName(), keys) < 1) {
                LOG.error("admin delete failed. The key is {}", keys);
            }
        } catch (Exception e) {
            throw new SqlStatementException(e.getMessage());
        }
        response.setData(list.get(0));
        return response.value();
    }

    @Override
    public ResponseEntity<Result<Map<String, Object>>> handleAction(AdminObject adminObject) {
        return null;
    }

    @Verification(Permission = Constant.ADMIN_SELECT)
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
     * 获取后台文件路径
     **/
    @Verification(Permission = Constant.ADMIN_SELECT)
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

            body.put(adminField.getFieldName(), convertValue(adminField.getFieldName(), targetClass, val));
        }
        flushTime(adminObject, body, initial);
        if (adminObject.getIgnores() != null) {
            for (String key : adminObject.getIgnores().keySet()) {
                body.remove(key);
            }
        }
        return body;
    }

    /**
     * 将客户端传入值转换为字段类型，转换失败时返回明确的 400 参数错误。
     **/
    private Object convertValue(String fieldName, Class<?> targetClass, Object val) {
        targetClass = wrapPrimitive(targetClass);
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
                val = formatAsInt(fieldName, val);
            } else if (targetClass.equals(Long.class)) {
                val = formatAsLong(fieldName, val);
            } else if (targetClass.equals(Double.class)) {
                val = formatAsDouble(fieldName, val);
            } else if (targetClass.equals(Float.class)) {
                val = formatAsFloat(fieldName, val);
            } else if (targetClass.equals(Boolean.class)) {
                if (!(val instanceof String)) {
                    throw invalidFieldValue(fieldName);
                }
                String valString = (String) val;
                if (valString.equalsIgnoreCase("true") || valString.equalsIgnoreCase("on") || valString.equalsIgnoreCase("yes") || valString.equals("1")) {
                    val = 1;
                } else if (valString.equalsIgnoreCase("false") || valString.equalsIgnoreCase("off") || valString.equalsIgnoreCase("no") || valString.equals("0")) {
                    val = 0;
                } else {
                    throw invalidFieldValue(fieldName);
                }
            } else if (targetClass.equals(LocalDateTime.class)) {
                val = TimeTransitionUtil.stringToLocalDateTime((String) val);
            } else if (targetClass.equals(LocalDate.class)) {
                val = TimeTransitionUtil.stringToLocalDate((String) val);
            } else if (targetClass.equals(Date.class)) {
                val = TimeTransitionUtil.stringToDate((String) val);
            } else if (targetClass.equals(LocalTime.class)) {
                val = TimeTransitionUtil.stringToLocalTime((String) val);
            } else {
                val = objectMapper.convertValue(val, targetClass);
            }
        } catch (RuntimeException e) {
            throw invalidFieldValue(fieldName);
        }
        return val;
    }

    private Object formatAsFloat(String fieldName, Object val) {
        if (val instanceof Float) {
            return val;
        } else if (val instanceof Double) {
            val = ((Double) val).floatValue();
        } else if (val instanceof Integer) {
            val = ((Integer) val).floatValue();
        } else if (val instanceof Long) {
            val = ((Long) val).floatValue();
        } else if (val instanceof String) {
            if (((String) val).isBlank()) {
                throw invalidFieldValue(fieldName);
            }
            try {
                val = Float.parseFloat((String) val);
            } catch (NumberFormatException e) {
                throw invalidFieldValue(fieldName);
            }
        } else {
            throw invalidFieldValue(fieldName);
        }
        return val;
    }

    private Object formatAsDouble(String fieldName, Object val) {
        if (val instanceof Double) {
            return val;
        } else if (val instanceof Float) {
            val = ((Float) val).doubleValue();
        } else if (val instanceof Integer) {
            val = ((Integer) val).doubleValue();
        } else if (val instanceof Long) {
            val = ((Long) val).doubleValue();
        } else if (val instanceof String) {
            if (((String) val).isBlank()) {
                throw invalidFieldValue(fieldName);
            }
            try {
                val = Double.parseDouble((String) val);
            } catch (NumberFormatException e) {
                throw invalidFieldValue(fieldName);
            }
        } else {
            throw invalidFieldValue(fieldName);
        }
        return val;
    }

    private Object formatAsLong(String fieldName, Object val) {
        if (val instanceof Long) {
            return val;
        } else if (val instanceof Double) {
            val = ((Double) val).longValue();
        } else if (val instanceof Float) {
            val = ((Float) val).longValue();
        } else if (val instanceof Integer) {
            val = ((Integer) val).longValue();
        } else if (val instanceof String) {
            if (((String) val).isBlank()) {
                throw invalidFieldValue(fieldName);
            }
            try {
                val = Long.parseLong((String) val);
            } catch (NumberFormatException e) {
                throw invalidFieldValue(fieldName);
            }
        } else {
            throw invalidFieldValue(fieldName);
        }
        return val;
    }

    private Object formatAsInt(String fieldName, Object val) {
        if (val instanceof Integer) {
            return val;
        } else if (val instanceof Double) {
            val = ((Double) val).intValue();
        } else if (val instanceof Float) {
            val = ((Float) val).intValue();
        } else if (val instanceof Long) {
            val = ((Long) val).intValue();
        } else if (val instanceof String) {
            if (((String) val).isBlank()) {
                throw invalidFieldValue(fieldName);
            }
            try {
                val = Integer.parseInt((String) val);
            } catch (NumberFormatException e) {
                throw invalidFieldValue(fieldName);
            }
        } else {
            throw invalidFieldValue(fieldName);
        }
        return val;
    }

    private IllegalException invalidFieldValue(String fieldName) {
        return new IllegalException("Invalid value for field " + fieldName + ".");
    }

    private Class<?> wrapPrimitive(Class<?> targetClass) {
        if (!targetClass.isPrimitive()) {
            return targetClass;
        }
        if (targetClass == int.class) return Integer.class;
        if (targetClass == long.class) return Long.class;
        if (targetClass == double.class) return Double.class;
        if (targetClass == float.class) return Float.class;
        if (targetClass == boolean.class) return Boolean.class;
        return targetClass;
    }

    /**
     * 获取站点信息与pear数据集
     **/
    @Verification
    private Map<String, Object> handleAdminJson(HttpServletRequest request, List<AdminObject> adminObjects, BuildContext buildContext) {
        Map<String, Object> res = new HashMap<>();
        User user = userService.currentUser(request);
        if (user == null) {
            throw new AuthorizationException("未登录");
        }
        // 获取渲染页面的所有站点信息
        Map<String, Object> siteCtx = getRenderPageContext(request);

        if (buildContext != null) {
            buildContext.execute(request, siteCtx);
        }
        res.put("objects", adminObjects);
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
     * 查询后台对象列表，按 AdminObject 配置校验字段白名单并生成参数绑定 SQL。
     **/
    private AdminQueryResult queryObjects(HttpServletRequest request, String model, AdminObject adminObject, QueryForm queryForm) {
        AdminQueryResult r = new AdminQueryResult();
        queryForm.validate();
        if (queryForm.isForeignMode()) {
            queryForm.setLimit(0);
        }
        SqlQuery sqlQuery = buildSqlQuery(adminObject, queryForm);
        List<Map<String, Object>> result = adminMapper.query(sqlQuery);
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
     * 构建安全查询对象：字段、表名、排序方向使用白名单，所有动态值使用 MyBatis 绑定参数。
     **/
    private SqlQuery buildSqlQuery(AdminObject adminObject, QueryForm queryForm) {
        SqlQuery sqlQuery = new SqlQuery();
        String tableName = adminObject.getTableName();
        validateIdentifier(tableName, "table name");
        sqlQuery.setTableName(tableName);
        sqlQuery.setShowClause(buildShowClause(adminObject, queryForm));
        sqlQuery.setOrderClause(buildOrderClause(adminObject, queryForm));
        sqlQuery.setLimit(queryForm.getLimit());

        List<String> conditions = new ArrayList<>();
        if (queryForm.getFilters() != null) {
            for (Filter filter : queryForm.getFilters()) {
                String condition = buildFilterCondition(adminObject, filter, sqlQuery);
                if (!condition.isEmpty()) {
                    conditions.add(condition);
                }
            }
        }
        String keywordCondition = buildKeywordCondition(adminObject, queryForm, sqlQuery);
        if (!keywordCondition.isEmpty()) {
            conditions.add(keywordCondition);
        }
        sqlQuery.setWhereClause(String.join(" AND ", conditions));
        return sqlQuery;
    }

    /**
     * 生成 SELECT 字段片段；请求 viewFields 只能来自 shows 白名单。
     **/
    private String buildShowClause(AdminObject adminObject, QueryForm queryForm) {
        List<String> viewFields = queryForm.getViewFields();
        if (viewFields == null || viewFields.isEmpty()) {
            viewFields = adminObject.getShows();
        } else {
            validateConfiguredFields(viewFields, adminObject.getShows(), "viewFields");
        }
        if (viewFields == null || viewFields.isEmpty()) {
            return "*";
        }
        List<String> columns = new ArrayList<>();
        Set<String> allColumns = getColumnFields(adminObject);
        for (String viewField : viewFields) {
            validateField(viewField, allColumns, "viewFields");
            columns.add(qualifiedColumn(adminObject.getTableName(), viewField));
        }
        return String.join(" ,", columns);
    }

    /**
     * 生成 ORDER BY 片段；字段来自 orderables 或数据库字段白名单，方向只允许 asc/desc。
     **/
    private String buildOrderClause(AdminObject adminObject, QueryForm queryForm) {
        List<Order> orders;
        StringBuilder orderClause = new StringBuilder();
        if (queryForm.getOrders() != null && !queryForm.getOrders().isEmpty()) {
            orders = queryForm.getOrders();
        } else {
            orders = adminObject.getOrders();
        }
        Set<String> allowedOrderFields = configuredFieldSet(adminObject.getOrderables());
        if (allowedOrderFields.isEmpty()) {
            allowedOrderFields = getColumnFields(adminObject);
        }
        if (orders != null) {
            for (Order order : orders) {
                String field = order.getName();
                String direction = order.getOp();
                validateField(field, allowedOrderFields, "orders");
                validateOrderDirection(direction);
                if (!orderClause.isEmpty()) {
                    orderClause.append(",");
                }
                orderClause.append(qualifiedColumn(adminObject.getTableName(), field)).append(" ").append(direction.toUpperCase(Locale.ROOT));
            }
        }
        return orderClause.toString();
    }

    /**
     * 生成单个 filter 条件；多个 LIKE 值会在一个 filter 内部以 OR 连接。
     **/
    private String buildFilterCondition(AdminObject adminObject, Filter filter, SqlQuery sqlQuery) {
        if (filter == null) {
            return "";
        }
        Set<String> allowedFilterFields = configuredFieldSet(adminObject.getFilterables());
        if (allowedFilterFields.isEmpty()) {
            allowedFilterFields = getColumnFields(adminObject);
        }
        validateField(filter.getName(), allowedFilterFields, "filters");
        String column = qualifiedColumn(adminObject.getTableName(), filter.getName());
        String op = normalizedFilterOperator(filter.getOp());
        if (Constant.FILTER_OP_LIKE.equalsIgnoreCase(filter.getOp())) {
            List<Object> values = normalizeValues(filter.getValue());
            List<String> likes = new ArrayList<>();
            for (Object value : values) {
                if (value == null || value.toString().isEmpty()) {
                    continue;
                }
                String bindName = addBinding(sqlQuery, "%" + value + "%");
                likes.add(column + " LIKE #{bindings." + bindName + "}");
            }
            return likes.isEmpty() ? "" : "(" + String.join(" OR ", likes) + ")";
        }

        if (Constant.FILTER_OP_BETWEEN.equalsIgnoreCase(filter.getOp())) {
            List<Object> values = normalizeValues(filter.getValue());
            if (values.size() != 2) {
                throw new IllegalException("Expected two values for between operation.");
            }
            String start = addBinding(sqlQuery, values.get(0));
            String end = addBinding(sqlQuery, values.get(1));
            return column + " BETWEEN #{bindings." + start + "} AND #{bindings." + end + "}";
        }

        if (Constant.FILTER_OP_IN.equalsIgnoreCase(filter.getOp()) || Constant.FILTER_OP_NOT_IN.equalsIgnoreCase(filter.getOp())) {
            List<Object> values = normalizeValues(filter.getValue());
            if (values.isEmpty()) {
                throw new IllegalException("Expected at least one value for in operation.");
            }
            List<String> placeholders = new ArrayList<>();
            for (Object value : values) {
                String bindName = addBinding(sqlQuery, value);
                placeholders.add("#{bindings." + bindName + "}");
            }
            return column + " " + op + " (" + String.join(", ", placeholders) + ")";
        }

        if (filter.getValue() == null && Constant.FILTER_OP_IS_NOT.equalsIgnoreCase(filter.getOp())) {
            return column + " IS NOT NULL";
        }
        String bindName = addBinding(sqlQuery, filter.getValue());
        return column + " " + op + " #{bindings." + bindName + "}";
    }

    /**
     * 生成 keyword 查询条件；searchFields 参数只能来自 searches 白名单。
     **/
    private String buildKeywordCondition(AdminObject adminObject, QueryForm queryForm, SqlQuery sqlQuery) {
        if (queryForm.getKeyword() == null) {
            return "";
        }
        List<String> searchFields = queryForm.getSearchFields();
        if (searchFields == null || searchFields.isEmpty()) {
            searchFields = adminObject.getSearches();
        } else {
            validateConfiguredFields(searchFields, adminObject.getSearches(), "searchFields");
        }
        if (searchFields == null || searchFields.isEmpty()) {
            return "";
        }
        String bindName = addBinding(sqlQuery, "%" + queryForm.getKeyword() + "%");
        List<String> conditions = new ArrayList<>();
        Set<String> allColumns = getColumnFields(adminObject);
        for (String searchField : searchFields) {
            validateField(searchField, allColumns, "searchFields");
            conditions.add(qualifiedColumn(adminObject.getTableName(), searchField) + " LIKE #{bindings." + bindName + "}");
        }
        return "(" + String.join(" OR ", conditions) + ")";
    }

    /**
     * 新增或更新前校验写入字段必须是模型中的真实数据库列。
     **/
    private void validateDataFields(AdminObject adminObject, Map<String, Object> data) {
        Set<String> columns = getColumnFields(adminObject);
        for (String field : data.keySet()) {
            validateField(field, columns, "fields");
        }
    }

    /**
     * 创建时校验必填字段，忽略非数据库列、忽略字段、自动生成字段和自动时间字段。
     **/
    private void validateRequiredFields(AdminObject adminObject, Map<String, Object> data) {
        if (adminObject.getRequires() == null || adminObject.getRequires().isEmpty()) {
            return;
        }
        Set<String> requiredFields = new LinkedHashSet<>(adminObject.getRequires());
        if (adminObject.getIgnores() != null) {
            requiredFields.removeAll(adminObject.getIgnores().keySet());
        }
        if (adminObject.getFields() != null) {
            for (AdminField field : adminObject.getFields()) {
                if (Boolean.TRUE.equals(field.getNotColumn()) || Boolean.TRUE.equals(field.getIsAutoId())
                        || Boolean.TRUE.equals(field.getIsAutoInsertTime()) || Boolean.TRUE.equals(field.getIsAutoUpdateTime())) {
                    requiredFields.remove(field.getFieldName());
                }
            }
        }
        for (String field : requiredFields) {
            Object value = data.get(field);
            if (value == null || (value instanceof String str && str.isBlank())) {
                throw new IllegalException("Required field is missing: " + field);
            }
        }
    }

    /**
     * 将合法 filter 操作符映射为 SQL 操作符。
     **/
    private String normalizedFilterOperator(String op) {
        if (op == null) {
            throw new IllegalException("Invalid filter operator.");
        }
        return switch (op.toLowerCase(Locale.ROOT)) {
            case Constant.FILTER_OP_IS_NOT -> "IS NOT";
            case Constant.FILTER_OP_EQUAL -> "=";
            case Constant.FILTER_OP_NOT_EQUAL -> "<>";
            case Constant.FILTER_OP_IN -> "IN";
            case Constant.FILTER_OP_NOT_IN -> "NOT IN";
            case Constant.FILTER_OP_GREATER -> ">";
            case Constant.FILTER_OP_GREATER_OR_EQUAL -> ">=";
            case Constant.FILTER_OP_LESS -> "<";
            case Constant.FILTER_OP_LESS_OR_EQUAL -> "<=";
            case Constant.FILTER_OP_LIKE -> "LIKE";
            case Constant.FILTER_OP_BETWEEN -> "BETWEEN";
            default -> throw new IllegalException("Invalid filter operator: " + op);
        };
    }

    /**
     * 校验排序方向只允许 asc 或 desc。
     **/
    private void validateOrderDirection(String direction) {
        if (direction == null || (!Constant.ORDER_OP_ASC.equalsIgnoreCase(direction) && !Constant.ORDER_OP_DESC.equalsIgnoreCase(direction))) {
            throw new IllegalException("Invalid order direction: " + direction);
        }
    }

    /**
     * 添加一个 MyBatis 绑定参数并返回参数名。
     **/
    private String addBinding(SqlQuery sqlQuery, Object value) {
        String name = "p" + sqlQuery.getBindings().size();
        sqlQuery.getBindings().put(name, value);
        return name;
    }

    /**
     * 将数组、集合或单值统一为列表。
     **/
    private List<Object> normalizeValues(Object value) {
        if (value == null) {
            return Collections.emptyList();
        }
        if (value instanceof Collection<?> collection) {
            return new ArrayList<>(collection);
        }
        if (value instanceof Object[] values) {
            return Arrays.asList(values);
        }
        return Collections.singletonList(value);
    }

    /**
     * 返回模型中的真实数据库字段。
     **/
    private Set<String> getColumnFields(AdminObject adminObject) {
        Set<String> columns = new LinkedHashSet<>();
        if (adminObject.getFields() != null) {
            for (AdminField field : adminObject.getFields()) {
                if (Boolean.TRUE.equals(field.getNotColumn())) {
                    continue;
                }
                if (field.getFieldName() != null && !field.getFieldName().isEmpty()) {
                    validateIdentifier(field.getFieldName(), "field name");
                    columns.add(field.getFieldName());
                }
            }
        }
        addConfiguredFields(columns, adminObject.getShows());
        addConfiguredFields(columns, adminObject.getEdits());
        addConfiguredFields(columns, adminObject.getFilterables());
        addConfiguredFields(columns, adminObject.getOrderables());
        addConfiguredFields(columns, adminObject.getSearches());
        addConfiguredFields(columns, adminObject.getPrimaryKeys());
        addConfiguredFields(columns, adminObject.getUniqueKeys());
        return columns;
    }

    /**
     * 将配置字段加入白名单，并做 SQL 标识符格式校验。
     **/
    private void addConfiguredFields(Set<String> target, List<String> fields) {
        if (fields == null) {
            return;
        }
        for (String field : fields) {
            validateIdentifier(field, "field name");
            target.add(field);
        }
    }

    /**
     * 将字段配置转换为 Set。
     **/
    private Set<String> configuredFieldSet(List<String> fields) {
        Set<String> set = new LinkedHashSet<>();
        addConfiguredFields(set, fields);
        return set;
    }

    /**
     * 校验请求字段必须属于指定配置白名单。
     **/
    private void validateConfiguredFields(List<String> fields, List<String> allowedFields, String label) {
        Set<String> allowed = configuredFieldSet(allowedFields);
        for (String field : fields) {
            validateField(field, allowed, label);
        }
    }

    /**
     * 校验字段属于白名单。
     **/
    private void validateField(String field, Set<String> allowedFields, String label) {
        validateIdentifier(field, "field name");
        if (allowedFields == null || !allowedFields.contains(field)) {
            throw new IllegalException("Invalid " + label + " field: " + field);
        }
    }

    /**
     * 校验 SQL 标识符格式。
     **/
    private void validateIdentifier(String identifier, String label) {
        if (identifier == null || !identifier.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalException("Invalid " + label + ": " + identifier);
        }
    }

    /**
     * 生成带表名前缀的安全列名。
     **/
    private String qualifiedColumn(String tableName, String fieldName) {
        validateIdentifier(tableName, "table name");
        validateIdentifier(fieldName, "field name");
        return "`" + tableName + "`.`" + fieldName + "`";
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
        // 使用预加载，获取对象及其外键链接的数据 Limit 1
        List<Map<String, Object>> result = adminMapper.selectFirst(adminObject.getTableName(), queryMap);
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
        // 序列化对象
        Map<String, Object> data = marshalOne(adminObject, result);
        response.setData(data);
        return response.value();
    }

    /**
     * 序列化对象，将result中唯一的Map数据,展示可shows字段
     **/
    private Map<String, Object> marshalOne(AdminObject adminObject, List<Map<String, Object>> result) {
        Map<String, Object> data = new HashMap<>();
        for (Map<String, Object> map : result) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (adminObject.getShows().contains( entry.getKey())) {
                    data.put(entry.getKey(), entry.getValue());
                }
            }
            if (data.isEmpty()) {
                LOG.warn("The dataset rendered by BeforeRender is not of type List<Map<String,Object>>.");
            }
        }
        return data;
    }

    /**
     * 根据query值遍历并获取其映射数据库表的主键或唯一键键值对
     **/
    private Map<String, Object> getPrimaryValues(HttpServletRequest request, AdminObject adminObject) {
        Map<String, Object> queryMap = new HashMap<>();
        Set<String> columns = getColumnFields(adminObject);
        boolean keysExist = false;
        if (adminObject.getPrimaryKeys() != null) {
            for (String field : adminObject.getPrimaryKeys()) {
                validateField(field, columns, "primaryKeys");
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
                validateField(field, columns, "uniqueKeys");
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
     * @param res         处理体
     * @param isCreate    新创建
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
