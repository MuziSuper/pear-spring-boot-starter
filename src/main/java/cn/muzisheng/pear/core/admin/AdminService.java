package cn.muzisheng.pear.core.admin;

import cn.muzisheng.pear.model.AdminObject;
import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.params.AdminQueryResult;
import cn.muzisheng.pear.params.QueryForm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AdminService {

    ResponseEntity<Result<AdminQueryResult>> handleQueryOrGetOne(HttpServletRequest request, String model, AdminObject adminObject,QueryForm queryForm);

    ResponseEntity<Result<Map<String,Object>>> handleCreate(AdminObject adminObject);
    ResponseEntity<Result<Map<String,Object>>> handleUpdate(AdminObject adminObject);
    ResponseEntity<Result<Map<String,Object>>> handleDelete(AdminObject adminObject);
    ResponseEntity<Result<Map<String,Object>>> handleAction(AdminObject adminObject);
}
