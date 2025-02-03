package cn.muzisheng.pear.core.admin.impl;

import cn.muzisheng.pear.core.admin.AdminService;
import cn.muzisheng.pear.dao.AdminDAO;
import cn.muzisheng.pear.exception.GeneralException;
import cn.muzisheng.pear.model.AdminObject;
import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.params.AdminQueryResult;
import cn.muzisheng.pear.params.QueryForm;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminServiceImpl<T> implements AdminService {

    private final AdminDAO<T> adminDAO;
    private static final Logger LOG = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    public AdminServiceImpl(AdminDAO<T> adminDAO) {
        this.adminDAO = adminDAO;
    }

    @Override
    public ResponseEntity<Result<AdminQueryResult>> handleQueryOrGetOne(HttpServletRequest request, String model, AdminObject adminObject, QueryForm queryForm) {
        if (request.getContentLength() <= 0) {
            return handleGetOne(request, model, adminObject, queryForm);
        }
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
     * 客户端通过url的参数进行数据查询 (不通过请求体传入参数)
     **/
    private ResponseEntity<Result<Map<String, Object>>> handleGetOne(HttpServletRequest request, String model, AdminObject adminObject, QueryForm queryForm) {
        Map<String, Object> queryMap = getPrimaryValues(request, model, adminObject, queryForm);
        if (queryMap.isEmpty()) {
            LOG.error("invalid primary key");
            throw new GeneralException("invalid primary key");
        }
        T result = adminDAO.getFirst(queryMap);
        if (result == null) {
            LOG.error("Data cannot be found.");
            throw new GeneralException("Data cannot be found.");
        }
        if (adminObject.getBeforeRender() != null) {
            Object res = adminObject.getBeforeRender().execute(request, result);
            if (res != null) {
                result = (T) res;
            }

        }

    }

    /**
     * 根据query值遍历并获取其映射数据库表的主键或外键键值对
     **/
    private Map<String, Object> getPrimaryValues(HttpServletRequest request, String model, AdminObject adminObject, QueryForm queryForm) {
        Map<String, Object> queryMap = new HashMap<>();
        boolean keysExist = false;
        for (String field : adminObject.getPrimaryKeys()) {
            String param = request.getParameter(field);
            if (!param.isEmpty()) {
                queryMap.put(field, param);
                keysExist = true;
            }
        }
        if (keysExist) {
            return queryMap;
        }
        for (String field : adminObject.getUniqueKeys()) {
            String param = request.getParameter(field);
            if (!param.isEmpty()) {
                queryMap.put(field, param);
            }
        }
        return queryMap;
    }

}
