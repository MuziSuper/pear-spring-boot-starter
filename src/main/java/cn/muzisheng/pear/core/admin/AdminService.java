package cn.muzisheng.pear.core.admin;

import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.model.AdminObject;
import cn.muzisheng.pear.model.Response;
import cn.muzisheng.pear.model.Result;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AdminService {
    ResponseEntity<Result<Map<String,Object>>> handleQueryOrGetOne(AdminObject adminObject);
    ResponseEntity<Result<Map<String,Object>>> handleCreate(AdminObject adminObject);
    ResponseEntity<Result<Map<String,Object>>> handleUpdate(AdminObject adminObject);
    ResponseEntity<Result<Map<String,Object>>> handleDelete(AdminObject adminObject);
    ResponseEntity<Result<Map<String,Object>>> handleAction(AdminObject adminObject);
}
