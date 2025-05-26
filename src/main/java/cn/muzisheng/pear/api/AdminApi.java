package cn.muzisheng.pear.api;

import cn.muzisheng.pear.core.admin.AdminService;
import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.initialize.AdminContainer;
import cn.muzisheng.pear.model.AdminObject;
import cn.muzisheng.pear.model.Response;
import cn.muzisheng.pear.params.QueryForm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminApi {
    @Autowired
    private AdminService adminService;

    /**
     * 查询
     *
     * @param model     模型名称
     * @param queryForm 查询条件类
     **/
    @PostMapping("{model}")
    public ResponseEntity<Result<Object>> query(HttpServletRequest request, @PathVariable("model") String model, @RequestBody(required = false) QueryForm queryForm) {
        if(AdminContainer.existsAdminObject(model)) {
            return adminService.handleQueryOrGetOne(request, model, AdminContainer.getAdminObject(model), queryForm);
        }
        Response<Object> response = new Response<>();
        response.setError("Model not found");
        response.setStatus(Constant.ILLEGAL_EXCEPTION);
        return response.value();
    }

    /**
     * 创建
     *
     * @param model 模型名称
     * @param data  创建数据
     **/
    @PutMapping("{model}")
    public ResponseEntity<Result<Map<String, Object>>> handleCreate(HttpServletRequest request, @PathVariable("model") String model, @RequestBody(required = false) Map<String, Object> data) {
        if(AdminContainer.existsAdminObject(model)) {
            return adminService.handleCreate(request, model, AdminContainer.getAdminObject(model), data);
        }
        Response<Map<String, Object>> response = new Response<>();
        response.setError("Model not found");
        response.setStatus(Constant.ILLEGAL_EXCEPTION);
        return response.value();
    }
    /**
     * 获取pear当前权限的数据集、当前用户信息与所有站点信息的JSON文件，需要管理员权限（系统级）
     **/
    @PostMapping("/json")
    public ResponseEntity<Result<Map<String, Object>>> AdminJson(HttpServletRequest request) {
        return adminService.adminJson(request);
    }
    /**
     * 获取所有静态文件信息
     **/
    @GetMapping("/")
    public ResponseEntity<Result<Map<String, Object>>> AdminFilepath(HttpServletRequest request) {
        return adminService.adminFilepath(request);
    }
    @PatchMapping("{model}")
    public ResponseEntity<Result<Map<String, Object>>> handleUpdate(HttpServletRequest request, @PathVariable("model") String model, @RequestBody(required = false) Map<String, Object> data) {
        if(AdminContainer.existsAdminObject(model)) {
            return adminService.handleUpdate(request,model, AdminContainer.getAdminObject(model), data);
        }
        Response<Map<String, Object>> response = new Response<>();
        response.setError("Model not found");
        response.setStatus(Constant.ILLEGAL_EXCEPTION);
        return response.value();
    }
//    @DeleteMapping("{model}")
//    public ResponseEntity<Result<Map<String, Object>>> handleDelete(HttpServletRequest request, @PathVariable("model") String model,@RequestParam("filed") String filed) {
//        for(PearObject adminObject : PearApplicationInitialization.adminObjects){
//            if(adminObject.getTableName().equals(model)){
//                return adminService.handleQueryOrGetOne(adminObject);
//            }
//        }
//        Response<Map<String, Object>> response = new Response<>();
//        response.setError("Model not found");
//        response.setStatus(Constant.GENERAL_EXCEPTION);
//        return response.value();
//    }
//
//    @PostMapping("{model}/{name}")
//    public ResponseEntity<Result<Map<String, Object>>> adminAction(HttpServletRequest request, @PathVariable("model") String model,@PathVariable("name") String name,@RequestParam("filed") String filed,@RequestParam("keys") String jsonDataMap) {
//        for(PearObject adminObject : PearApplicationInitialization.adminObjects){
//            if(adminObject.getTableName().equals(model)){
//                return adminService.handleQueryOrGetOne(adminObject);
//            }
//        }
//        Response<Map<String, Object>> response = new Response<>();
//        response.setError("Model not found");
//        response.setStatus(Constant.GENERAL_EXCEPTION);
//        return response.value();
//    }
}
