package cn.muzisheng.pear.admin;

import cn.muzisheng.pear.model.AdminObject;
import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.params.QueryForm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AdminService {

    /**
     * 根据请求体中查询条件queryFrom或请求头参数返回查询结果
     * @param request 请求
     * @param model 模型名称
     * @param adminObject 模型对象
     * @param queryForm 查询条件
     **/
    ResponseEntity<Result<Object>> handleQueryOrGetOne(HttpServletRequest request, String model, AdminObject adminObject, QueryForm queryForm);
    /**
     * 根据请求体中的数据插入指定model对象
     * @param request 请求
     * @param model 模型名称
     * @param adminObject 模型对象
     * @param data 请求体中数据
     **/
    ResponseEntity<Result<Map<String,Object>>> handleCreate(HttpServletRequest request, String model, AdminObject adminObject, Map<String,Object> data);
    /**
     * 根据请求体中的数据更新指定model对象
     * @param request 请求
     * @param model 模型名称
     * @param adminObject 模型对象
     * @param data 请求体中数据
     **/
    ResponseEntity<Result<Map<String,Object>>> handleUpdate(HttpServletRequest request, String model, AdminObject adminObject, Map<String,Object> data);
    /**
     * 根据请求头中数据删除指定model对象
     * @param request 请求
     * @param model 模型名称
     * @param adminObject 模型对象
     **/
    ResponseEntity<Result<Map<String,Object>>> handleDelete(HttpServletRequest request, String model, AdminObject adminObject);
    /**
     * 根据请求头中数据执行指定admin开发者自定义方法
     * @param adminObject 模型对象
     **/
    ResponseEntity<Result<Map<String,Object>>> handleAction(AdminObject adminObject);
    /**
     * 获取站点信息与Pear数据集
     **/
    ResponseEntity<Result<Map<String,Object>>> adminJson(HttpServletRequest request);
    /**
     * 获取静态资源信息
     **/
    ResponseEntity<Result<Map<String, Object>>> adminFilepath(HttpServletRequest request);

}
