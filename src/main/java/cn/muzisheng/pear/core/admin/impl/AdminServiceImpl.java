package cn.muzisheng.pear.core.admin.impl;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.core.admin.AdminService;
import cn.muzisheng.pear.dao.AdminDAO;
import cn.muzisheng.pear.exception.GeneralException;
import cn.muzisheng.pear.mapper.AdminMapper;
import cn.muzisheng.pear.model.*;
import cn.muzisheng.pear.params.AdminQueryResult;
import cn.muzisheng.pear.params.Filter;
import cn.muzisheng.pear.params.QueryForm;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private static final Logger LOG = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    public AdminServiceImpl(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

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
        if (queryForm.isForeignMode()) {
            queryForm.setLimit(0);
        }
        AdminQueryResult result = queryObjects(request, model, adminObject, queryForm);
        response.setData(result);
        return response.value();
    }


    @Override
    public ResponseEntity<Result<Map<String, Object>>> handleCreate(AdminObject adminObject) {
        return null;
    }

    @Override
    public ResponseEntity<Result<Map<String, Object>>> handleUpdate(AdminObject adminObject) {
        return null;
    }

    @Override
    public ResponseEntity<Result<Map<String, Object>>> handleDelete(AdminObject adminObject) {
        return null;
    }

    @Override
    public ResponseEntity<Result<Map<String, Object>>> handleAction(AdminObject adminObject) {
        return null;
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
        for (Filter filter : queryForm.getFilters()) {
            String queryClause = filter.getQueryClause();
            if (queryClause != null) {
                // 如果是like操作，则构造LIKE语句whereClause，先不带WHERE,下文还有限定字筛选
                if (Constant.FILTER_OP_LIKE.equals(filter.getOp())) {
                    if (filter.getValue() instanceof Object[] values) {
                        List<String> conditions = new ArrayList<>();
                        for (Object value : values) {
                            if (value instanceof String strValue) {
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
                            String escapedValue = strValue.replace("\"", "\\\"");
                            whereClause = String.format("`%s`.`%s` LIKE '%%%s%%'", adminObject.getTableName(), filter.getName(), escapedValue);
                        }
                    }
                // 如果是between操作，则构造BETWEEN语句,也不带WHERE
                } else if (Constant.FILTER_OP_BETWEEN.equals(filter.getOp())) {
                    if (filter.getValue() instanceof List values) {
                        if (values.size() == 2) {
                            // `user`.name BETWEEN 'John' AND 'Doe'
                            if((values.get(0) instanceof Integer && values.get(1) instanceof Integer)||(values.get(0) instanceof Float && values.get(1) instanceof Float)) {
                                whereClause = String.format("`%s`.%s %s %s AND %s", adminObject.getTableName(), filter.getName(), queryClause, values.get(0), values.get(1));
                            }else{
                                whereClause = String.format("`%s`.%s %s '%s' AND '%s'", adminObject.getTableName(), filter.getName(), queryClause, values.get(0), values.get(1));
                            }
                        } else {
                            throw new GeneralException("Expected an Object[] value with length 2 for between operation but got " + values.size());
                        }
                    } else {
                        throw new GeneralException("Expected an Object[] value for between operation but got " + filter.getValue().getClass().getName());
                    }
                }else{
                    whereClause = String.format("`%s`.%s'%s'", adminObject.getTableName(),queryClause, filter.getValue());
                }
            }
        }
        // 排序子句，不加ORDER BY
        Order[] orders;
        StringBuilder orderClause = new StringBuilder();
        if (queryForm.getOrders().length > 0) {
            orders = queryForm.getOrders();
        } else {
            orders = adminObject.getOrders();
        }
        for (Order order : orders) {
            if(!orderClause.isEmpty()){
                orderClause.append(",");
            }
            orderClause.append(String.format("`%s`.`%s` %s", adminObject.getTableName(), order.getName(), order.getOp()));
        }

        StringBuilder whereClauseBuilder = new StringBuilder();
        if (queryForm.getKeyword() != null) {
            for(String searchField : adminObject.getSearches()){
                if(!whereClauseBuilder.isEmpty()){
                    whereClauseBuilder.append(" OR ");
                }
                whereClauseBuilder.append(String.format("`%s`.`%s` LIKE '%%%s%%'", adminObject.getTableName(), searchField, queryForm.getKeyword()));
            }
        }
        StringBuilder showClause = new StringBuilder();
        if(adminObject.getShows()!=null) {
            for (String viewField : adminObject.getShows()) {
                if (!showClause.isEmpty()) {
                    showClause.append(" ,");
                }
                showClause.append(String.format("`%s`.`%s`", adminObject.getTableName(), viewField));
            }
        }else{
            showClause.append("*");
        }
        List<Map<String, Object>> result =adminMapper.query(adminObject.getTableName(), showClause.toString(),whereClause, orderClause.toString(),whereClauseBuilder.toString(), queryForm.getLimit());
        if(adminObject.getBeforeRender()!=null){
            try {
                Object res = adminObject.getBeforeRender().execute(request, result);
                if(res instanceof List){
                    result = (List<Map<String, Object>>) res;
                }
            }catch (Exception e){
                LOG.warn("BeforeRender error: {}", e.getMessage());
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
        Map<String, Object> queryMap = getPrimaryValues(request, model, adminObject, queryForm);
        if (queryMap.isEmpty()) {
            LOG.error("invalid primary key");
            throw new GeneralException("invalid primary key");
        }
        // 使用预加载，获取对象及其外键链接的数据
        Object result = adminMapper.selectFirst(adminObject.getTableName(), queryMap);
        if (result == null) {
            LOG.error("Data cannot be found.");
            throw new GeneralException("Data cannot be found.");
        }
        if (adminObject.getBeforeRender() != null) {
            Object res;
            try {
                res = adminObject.getBeforeRender().execute(request, result);
            } catch (Exception e) {
                LOG.error("BeforeRender error");
                throw new GeneralException("BeforeRender error");
            }
            if (res != null) {
                result = res;
            }
        }
        // 序列化对象，将result转为map[string]any
        Map<String, Object> data = marshalOne(request, adminObject, result);
        response.setData(data);
        return response.value();
    }

    /**
     * 序列化对象，将result转为map[string]any
     **/
    private Map<String, Object> marshalOne(HttpServletRequest request, AdminObject adminObject, Object result) {
        Map<String, Object> data = new HashMap<>();
        for (AdminField field : adminObject.getFields()) {
            if (field.getForeign() != null) {
                AdminValue foreignValue = new AdminValue();
                foreignValue.setValue(field.getForeign().getForeignKey());
                if (field.getForeign().getFieldName() != null) {
                    foreignValue.setLabel(field.getForeign().getFieldName());
                } else {
                    foreignValue.setLabel(foreignValue.getValue().toString());
                }
                data.put(field.getName(), foreignValue);
            } else {
                String filedName = field.getName();
                data.put(field.getName(), filedName);
            }
        }
        if (adminObject.getAdminViewOnSite() != null) {
            try {
                data.put("_adminExtra", adminObject.getAdminViewOnSite().execute(request, result));
            } catch (Exception e) {
                LOG.error("AdminViewOnSite error");
            }
        }
        return data;
    }

    /**
     * 根据query值遍历并获取其映射数据库表的主键或外键键值对
     **/
    private Map<String, Object> getPrimaryValues(HttpServletRequest request, String model, AdminObject adminObject, QueryForm queryForm) {
        Map<String, Object> queryMap = new HashMap<>();
        boolean keysExist = false;
        if(adminObject.getPrimaryKeys()!=null){
            for (String field : adminObject.getPrimaryKeys()) {
                String param = request.getParameter(field);
                if (!param.isEmpty()) {
                    queryMap.put(field, param);
                    keysExist = true;
                }
            }
        }
        if (keysExist) {
            return queryMap;
        }
        if(adminObject.getUniqueKeys()!=null){
            for (String field : adminObject.getUniqueKeys()) {
                String param = request.getParameter(field);
                if (!param.isEmpty()) {
                    queryMap.put(field, param);
                }
            }
        }
        return queryMap;
    }

}
