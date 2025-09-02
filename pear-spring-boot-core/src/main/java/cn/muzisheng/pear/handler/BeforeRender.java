package cn.muzisheng.pear.handler;

import cn.muzisheng.pear.model.AdminObject;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface BeforeRender {
    /**
     * 数据渲染前的处理逻辑
     * @param request 请求
     * @param adminObject AdminObject对象
     * @param object 渲染前的数据集合
     **/
    void execute(HttpServletRequest request, AdminObject adminObject, List<Map<String, Object>> object);
}
