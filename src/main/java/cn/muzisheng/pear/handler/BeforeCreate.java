package cn.muzisheng.pear.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;


@FunctionalInterface
public interface BeforeCreate {
    Object execute(HttpServletRequest request,Object object);
}
