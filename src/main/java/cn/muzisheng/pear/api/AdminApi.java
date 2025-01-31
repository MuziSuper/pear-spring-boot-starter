package cn.muzisheng.pear.api;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.core.admin.AdminService;
import cn.muzisheng.pear.initialize.ApplicationInitialization;
import cn.muzisheng.pear.model.AdminObject;
import cn.muzisheng.pear.model.Response;
import cn.muzisheng.pear.params.RegisterUserForm;
import cn.muzisheng.pear.model.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminApi {
    @Autowired
    private AdminService adminService;
    @PostMapping("{model}")
    public ResponseEntity<Result<Map<String, Object>>> query(HttpServletRequest request,@PathVariable("model") String model) {
        for(AdminObject adminObject : ApplicationInitialization.adminObjects){
            if(adminObject.getTableName().equals(model)){
                return adminService.handleQueryOrGetOne(adminObject);
            }
        }
        Response<Map<String, Object>> response = new Response<>();
        response.setError("Model not found");
        response.setStatus(Constant.GENERAL_EXCEPTION);
        return response.value();
    }
    @PutMapping("{model}")
    public ResponseEntity<Result<Map<String, Object>>> handleCreate(HttpServletRequest request, @PathVariable("model") String model) {
        for(AdminObject adminObject : ApplicationInitialization.adminObjects){
            if(adminObject.getTableName().equals(model)){
                return adminService.handleQueryOrGetOne(adminObject);
            }
        }
        Response<Map<String, Object>> response = new Response<>();
        response.setError("Model not found");
        response.setStatus(Constant.GENERAL_EXCEPTION);
        return response.value();
    }
    @PatchMapping("{model}")
    public ResponseEntity<Result<Map<String, Object>>> handleUpdate(HttpServletRequest request, @PathVariable("model") String model) {
        for(AdminObject adminObject : ApplicationInitialization.adminObjects){
            if(adminObject.getTableName().equals(model)){
                return adminService.handleQueryOrGetOne(adminObject);
            }
        }
        Response<Map<String, Object>> response = new Response<>();
        response.setError("Model not found");
        response.setStatus(Constant.GENERAL_EXCEPTION);
        return response.value();
    }
    @DeleteMapping("{model}")
    public ResponseEntity<Result<Map<String, Object>>> handleDelete(HttpServletRequest request, @PathVariable("model") String model) {
        for(AdminObject adminObject : ApplicationInitialization.adminObjects){
            if(adminObject.getTableName().equals(model)){
                return adminService.handleQueryOrGetOne(adminObject);
            }
        }
        Response<Map<String, Object>> response = new Response<>();
        response.setError("Model not found");
        response.setStatus(Constant.GENERAL_EXCEPTION);
        return response.value();
    }

    @PostMapping("{model}/{name}")
    public ResponseEntity<Result<Map<String, Object>>> adminAction(HttpServletRequest request, @PathVariable("model") String model,@PathVariable("name") String name) {
        for(AdminObject adminObject : ApplicationInitialization.adminObjects){
            if(adminObject.getTableName().equals(model)){
                return adminService.handleQueryOrGetOne(adminObject);
            }
        }
        Response<Map<String, Object>> response = new Response<>();
        response.setError("Model not found");
        response.setStatus(Constant.GENERAL_EXCEPTION);
        return response.value();
    }
}
