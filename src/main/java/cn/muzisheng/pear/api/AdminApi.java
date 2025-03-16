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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminApi {
    @Autowired
    private AdminService adminService;
    @PostMapping("{model}")
    public ResponseEntity<Result<Object>> query(HttpServletRequest request, @PathVariable("model") String model, @RequestBody(required = false) QueryForm queryForm) {
        for(AdminObject adminObject : AdminContainer.getAllAdminObjects()){
            if(adminObject.getTableName().equals(model)){
                return adminService.handleQueryOrGetOne(request,model,adminObject,queryForm);
            }
        }
        Response<Object> response = new Response<>();
        response.setError("Model not found");
        response.setStatus(Constant.GENERAL_EXCEPTION);
        return response.value();
    }
    @PutMapping("{model}")
    public ResponseEntity<Result<Map<String, Object>>> handleCreate(HttpServletRequest request, @PathVariable("model") String model, @RequestBody(required = false)Map<String, Object> data) {
        for(AdminObject adminObject : AdminContainer.getAllAdminObjects()){
            if(adminObject.getTableName().equals(model)){
                return adminService.handleCreate(request,model,adminObject,data);
            }
        }
        Response<Map<String,Object>> response = new Response<>();
        response.setError("Model not found");
        response.setStatus(Constant.GENERAL_EXCEPTION);
        return response.value();
    }
    @PostMapping("/admin/admin.json")
    public ResponseEntity<Result<Map<String, Object>>> RegisterAdmins(HttpServletRequest request){
        Response<Map<String,Object>> response = new Response<>();
        adminService.registerAdmins(request);
        return response.value();
    }
//    @PatchMapping("{model}")
//    public ResponseEntity<Result<Map<String, Object>>> handleUpdate(HttpServletRequest request, @PathVariable("model") String model,@RequestParam("filed") String filed) {
//        for(PearObject adminObject : ApplicationInitialization.adminObjects){
//            if(adminObject.getTableName().equals(model)){
//                return adminService.handleQueryOrGetOne(adminObject);
//            }
//        }
//        Response<Map<String, Object>> response = new Response<>();
//        response.setError("Model not found");
//        response.setStatus(Constant.GENERAL_EXCEPTION);
//        return response.value();
//    }
//    @DeleteMapping("{model}")
//    public ResponseEntity<Result<Map<String, Object>>> handleDelete(HttpServletRequest request, @PathVariable("model") String model,@RequestParam("filed") String filed) {
//        for(PearObject adminObject : ApplicationInitialization.adminObjects){
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
//        for(PearObject adminObject : ApplicationInitialization.adminObjects){
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
