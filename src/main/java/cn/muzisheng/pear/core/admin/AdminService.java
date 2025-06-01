package cn.muzisheng.pear.core.admin;

import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.model.AdminObject;
import cn.muzisheng.pear.params.QueryForm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AdminService {


    ResponseEntity<Result<Object>> handleQueryOrGetOne(HttpServletRequest request, String model, AdminObject adminObject, QueryForm queryForm);
    ResponseEntity<Result<Map<String,Object>>> handleCreate(HttpServletRequest request, String model, AdminObject adminObject, Map<String,Object> data);
    ResponseEntity<Result<Map<String,Object>>> handleUpdate(HttpServletRequest request, String model, AdminObject adminObject, Map<String,Object> data);
    ResponseEntity<Result<Map<String,Object>>> handleDelete(HttpServletRequest request, String model, AdminObject adminObject);
    ResponseEntity<Result<Map<String,Object>>> handleAction(AdminObject adminObject);
    ResponseEntity<Result<Map<String,Object>>> adminJson(HttpServletRequest request);
    ResponseEntity<Result<Map<String, Object>>> adminFilepath(HttpServletRequest request);

}
