package cn.muzisheng.pear.handler;

import cn.muzisheng.pear.model.AdminObject;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@FunctionalInterface
public interface AfterCreate {
    /**
     * 数据创建后的处理逻辑
     * @param request 请求
     * @param adminObject AdminObject对象
     * @param object 创建后的数据
     **/
    void execute(HttpServletRequest request, AdminObject adminObject, Map<String,Object> object);
}
