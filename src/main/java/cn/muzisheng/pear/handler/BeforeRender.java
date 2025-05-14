package cn.muzisheng.pear.handler;

import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface BeforeRender {
    Object execute(HttpServletRequest request, Object object);
}
