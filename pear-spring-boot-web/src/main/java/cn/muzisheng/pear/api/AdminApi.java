package cn.muzisheng.pear.api;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.admin.AdminService;
import cn.muzisheng.pear.initialize.AdminContainer;
import cn.muzisheng.pear.model.Response;
import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.params.QueryForm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
/**
 * admin接口，用于控制所有系统及其用户自定义实体类的增删改查
 **/
@RestController
@RequestMapping("/admin")
public class AdminApi {
    @Autowired
    private AdminService adminService;

    /**
     * admin查询
     *
     * @param model     模型名称
     * @param queryForm 查询条件类
     **/
    @PostMapping("{model}")
    public ResponseEntity<Result<Object>> query(HttpServletRequest request, @PathVariable("model") String model, @RequestBody(required = false) QueryForm queryForm) {
        // 判断模型是否存在
        if(AdminContainer.existsAdminObject(model)) {
            return adminService.handleQueryOrGetOne(request, model, AdminContainer.getAdminObject(model), queryForm);
        }
        Response<Object> response = new Response<>();
        response.setError("Model not found");
        response.setStatus(Constant.ILLEGAL_EXCEPTION);
        return response.value();
    }

    /**
     * admin创建
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
    /**
     * admin更新
     *
     * @param model 模型名称
     * @param data  创建数据
     **/
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
    /**
     * admin创建
     *
     * @param model 模型名称
     **/
    @DeleteMapping("{model}")
    public ResponseEntity<Result<Map<String, Object>>> handleDelete(HttpServletRequest request, @PathVariable("model") String model) {
        if(AdminContainer.existsAdminObject(model)) {
            return adminService.handleDelete(request,model, AdminContainer.getAdminObject(model));
        }
        Response<Map<String, Object>> response = new Response<>();
        response.setError("Model not found");
        response.setStatus(Constant.ILLEGAL_EXCEPTION);
        return response.value();
    }


}
