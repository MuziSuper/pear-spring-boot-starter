package cn.muzisheng.pear.handler;

import cn.muzisheng.pear.model.AdminObject;
import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface BeforeRender {
    void execute(HttpServletRequest request, AdminObject adminObject, Object object);
}
