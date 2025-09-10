package cn.muzisheng.pear.handler;

import cn.muzisheng.pear.model.AdminObject;
import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface AccessCheck {
   boolean execute(HttpServletRequest request, AdminObject adminObject);
}
