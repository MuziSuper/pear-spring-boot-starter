package cn.muzisheng.pear.handler;

import cn.muzisheng.pear.model.QueryResult;
import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface BeforeQueryRenderFunc {
    Object execute(HttpServletRequest request, Object object, QueryResult queryResult);
}
