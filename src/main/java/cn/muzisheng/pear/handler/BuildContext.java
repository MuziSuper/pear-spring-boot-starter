package cn.muzisheng.pear.handler;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@FunctionalInterface
public interface BuildContext {
    Map<String,Object> execute(HttpServletRequest request, Map<String,Object> data);
}
