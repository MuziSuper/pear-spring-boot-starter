package cn.muzisheng.pear.params;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin CRUD 查询 SQL 片段与 MyBatis 绑定参数。
 **/
@Data
public class SqlQuery {
    /**
     * 已经过白名单校验的表名。
     **/
    private String tableName;
    /**
     * 已经过白名单校验的 SELECT 字段片段。
     **/
    private String showClause;
    /**
     * 已经过白名单校验、仅包含 MyBatis 占位符的 WHERE 片段。
     **/
    private String whereClause;
    /**
     * 已经过白名单校验的排序片段。
     **/
    private String orderClause;
    /**
     * 查询数量限制。
     **/
    private int limit;
    /**
     * WHERE 片段中使用的绑定参数。
     **/
    private Map<String, Object> bindings = new HashMap<>();
}
