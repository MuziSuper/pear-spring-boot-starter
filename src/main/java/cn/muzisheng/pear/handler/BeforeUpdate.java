package cn.muzisheng.pear.handler;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@FunctionalInterface
public interface BeforeUpdate {
    Object execute(HttpServletRequest request, Object object, Map<String,Object> val);
}
