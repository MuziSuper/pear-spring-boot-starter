package cn.muzisheng.pear.core.admin.impl;

import cn.muzisheng.pear.core.admin.AdminService;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.model.AdminObject;
import cn.muzisheng.pear.model.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {


    @Override
    public ResponseEntity<Result<Map<String, Object>>> handleQueryOrGetOne(AdminObject adminObject) {
        return null;
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
}
