package cn.muzisheng.pear.handler;

import cn.muzisheng.pear.model.AdminObject;
import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface BeforeDelete {
    /**
     * 数据渲染前的处理逻辑
     * @param request 请求
     * @param adminObject AdminObject对象
     * @param object 渲染前的数据集合
     **/
    void execute(HttpServletRequest request, AdminObject adminObject, Object object);
}
