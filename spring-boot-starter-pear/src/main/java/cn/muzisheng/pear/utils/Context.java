package cn.muzisheng.pear.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class Context {
    public static void set(String key, Object value) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            requestAttributes.setAttribute(key, value, RequestAttributes.SCOPE_SESSION);
        }
    }

    public static Object get(String key) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return requestAttributes.getAttribute(key, RequestAttributes.SCOPE_SESSION);
        }
        return null;
    }
}
