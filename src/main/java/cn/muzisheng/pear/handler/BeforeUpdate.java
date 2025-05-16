package cn.muzisheng.pear.handler;

import cn.muzisheng.pear.model.AdminObject;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@FunctionalInterface
public interface BeforeUpdate {
    void execute(HttpServletRequest request, AdminObject adminObject, Object object);
}
