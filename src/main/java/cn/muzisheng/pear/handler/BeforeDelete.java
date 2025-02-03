package cn.muzisheng.pear.handler;

import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface BeforeDelete {
    Object execute(HttpServletRequest request, Object object);
}
