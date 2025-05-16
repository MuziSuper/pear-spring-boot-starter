package cn.muzisheng.pear.handler;

import cn.muzisheng.pear.model.AdminObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;


@FunctionalInterface
public interface BeforeCreate {
    void execute(HttpServletRequest request, AdminObject adminObject,Object object);
}
