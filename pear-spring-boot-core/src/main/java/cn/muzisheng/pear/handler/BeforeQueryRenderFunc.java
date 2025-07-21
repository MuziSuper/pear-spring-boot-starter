package cn.muzisheng.pear.handler;

import cn.muzisheng.pear.model.AdminObject;
import cn.muzisheng.pear.model.QueryResult;
import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface BeforeQueryRenderFunc {
    void execute(HttpServletRequest request, AdminObject adminObject, Object object, QueryResult queryResult);
}
