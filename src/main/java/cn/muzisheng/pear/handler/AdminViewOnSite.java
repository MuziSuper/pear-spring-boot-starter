package cn.muzisheng.pear.handler;

import cn.muzisheng.pear.model.AdminObject;
import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface AdminViewOnSite {
    Object execute(HttpServletRequest request, AdminObject adminObject, Object object);
}
