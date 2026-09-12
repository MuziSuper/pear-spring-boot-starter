package cn.muzisheng.pear.api;

import cn.muzisheng.pear.admin.AdminService;
import cn.muzisheng.pear.dto.admin.AdminCreateRequestDTO;
import cn.muzisheng.pear.dto.admin.AdminQueryDTO;
import cn.muzisheng.pear.dto.admin.AdminUpdateRequestDTO;
import cn.muzisheng.pear.exception.NotFoundException;
import cn.muzisheng.pear.initialize.AdminContainer;
import cn.muzisheng.pear.util.ApiResponseUtil;
import cn.muzisheng.pear.vo.admin.AdminPayloadVO;
import cn.muzisheng.pear.vo.common.ApiResponseVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * admin接口，用于控制所有系统及其用户自定义实体类的增删改查
 **/
@RestController
@RequestMapping("/admin")
public class AdminApi {
    @Autowired
    private AdminService adminService;

    /**
     * Queries an Admin-managed model using its dynamic query DTO.
     *
     * @param model     模型名称
     * @param queryDTO query filters, ordering and pagination
     **/
    @PostMapping("{model}")
    public ResponseEntity<ApiResponseVO<AdminPayloadVO>> query(HttpServletRequest request, @PathVariable("model") String model, @RequestBody(required = false) AdminQueryDTO queryDTO) {
        // 判断模型是否存在
        if(AdminContainer.existsAdminObject(model)) {
            return ApiResponseUtil.map(adminService.handleQueryOrGetOne(request, model, new AdminContainer.AdminFactory(model).getAdminObject(), queryDTO), this::payload);
        }
        throw new NotFoundException("Model not found: " + model);
    }

    /**
     * Creates an Admin-managed model from a dynamic DTO payload.
     *
     * @param model 模型名称
     * @param requestDTO dynamic field payload
     **/
    @PutMapping("{model}")
    public ResponseEntity<ApiResponseVO<AdminPayloadVO>> handleCreate(HttpServletRequest request, @PathVariable("model") String model, @RequestBody(required = false) AdminCreateRequestDTO requestDTO) {
        if(AdminContainer.existsAdminObject(model)) {
            return ApiResponseUtil.map(adminService.handleCreate(request, model, new AdminContainer.AdminFactory(model).getAdminObject(), requestDTO == null ? null : requestDTO.getData()), this::payload);
        }
        throw new NotFoundException("Model not found: " + model);
    }
    /**
     * 获取pear当前权限的数据集、当前用户信息与所有站点信息的JSON文件，需要管理员权限（系统级）
     **/
    @PostMapping("/json")
    public ResponseEntity<ApiResponseVO<AdminPayloadVO>> AdminJson(HttpServletRequest request) {
        return ApiResponseUtil.map(adminService.adminJson(request), this::payload);
    }
    /**
     * 获取所有静态文件信息
     **/
    @GetMapping("/")
    public ResponseEntity<ApiResponseVO<AdminPayloadVO>> AdminFilepath(HttpServletRequest request) {
        return ApiResponseUtil.map(adminService.adminFilepath(request), this::payload);
    }
    /**
     * Updates an Admin-managed model from a dynamic DTO payload.
     *
     * @param model 模型名称
     * @param requestDTO dynamic field payload
     **/
    @PatchMapping("{model}")
    public ResponseEntity<ApiResponseVO<AdminPayloadVO>> handleUpdate(HttpServletRequest request, @PathVariable("model") String model, @RequestBody(required = false) AdminUpdateRequestDTO requestDTO) {
        if(AdminContainer.existsAdminObject(model)) {
            return ApiResponseUtil.map(adminService.handleUpdate(request,model, new AdminContainer.AdminFactory(model).getAdminObject(), requestDTO == null ? null : requestDTO.getData()), this::payload);
        }
        throw new NotFoundException("Model not found: " + model);
    }
    /**
     * Deletes an Admin-managed model identified by request parameters.
     *
     * @param model 模型名称
     **/
    @DeleteMapping("{model}")
    public ResponseEntity<ApiResponseVO<AdminPayloadVO>> handleDelete(HttpServletRequest request, @PathVariable("model") String model) {
        if(AdminContainer.existsAdminObject(model)) {
            return ApiResponseUtil.map(adminService.handleDelete(request,model, new AdminContainer.AdminFactory(model).getAdminObject()), this::payload);
        }
        throw new NotFoundException("Model not found: " + model);
    }

    private AdminPayloadVO payload(Object data) {
        return data == null ? null : new AdminPayloadVO(data);
    }


}
