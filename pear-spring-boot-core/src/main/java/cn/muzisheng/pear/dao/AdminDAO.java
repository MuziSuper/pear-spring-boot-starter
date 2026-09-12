package cn.muzisheng.pear.dao;

import cn.muzisheng.pear.exception.SqlStatementException;
import cn.muzisheng.pear.params.SqlQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AdminDAO {
    private static final Logger LOG = LoggerFactory.getLogger(AdminDAO.class);

    /**
     * 生成按主键或唯一键查询第一条数据的 SQL，值全部使用 MyBatis 参数绑定。
     **/
    public String selectFirst(Map<String, Object> params) {
        String tableName = (String) params.get("tableName");
        @SuppressWarnings("unchecked")
        Map<String, Object> keys = (Map<String, Object>) params.get("keys");
        validateIdentifier(tableName, "table name");
        if (keys == null || keys.isEmpty()) {
            throw new SqlStatementException("The query keys cannot be empty.");
        }
        StringBuilder builder = new StringBuilder();
        // SELECT * FROM `tableName` WHERE
        builder.append("SELECT * FROM `").append(tableName).append("` ").append("WHERE ");
        int index = 0;
        for (Map.Entry<String, Object> entry : keys.entrySet()) {
            validateIdentifier(entry.getKey(), "field name");
            if (index > 0) {
                // AND
                builder.append(" AND ");
            }
            builder.append("`").append(entry.getKey()).append("` = #{keys['").append(entry.getKey()).append("']} ");
            index++;
        }
        // LIMIT 1
        builder.append("LIMIT 1");
        LOG.info(builder.toString());
        return builder.toString();
    }

    /**
     * 生成后台列表查询 SQL，字段、表名和排序由服务层白名单校验，值走 MyBatis 参数绑定。
     **/
    public String query(SqlQuery query) {
        StringBuilder builder = new StringBuilder();
        String tableName = query.getTableName();
        String showClause = query.getShowClause();
        String whereClause = query.getWhereClause();
        String orderClause = query.getOrderClause();
        int limit = query.getLimit();
        validateIdentifier(tableName, "table name");
        if (showClause == null) {
            showClause = "*";
        }
        // SELECT * FROM `tableName`
        builder.append("SELECT ").append(showClause).append(" ");
        if (tableName.isEmpty()) {
            throw new SqlStatementException("The table name cannot be left blank.");
        }
        builder.append("FROM `").append(tableName).append("` ");
        if (whereClause != null && !whereClause.isEmpty()) {
            builder.append("WHERE (").append(whereClause).append(") ");
        }
        if (orderClause != null && !orderClause.isEmpty()) {
            builder.append("ORDER BY ").append(orderClause).append(" ");
        }
        if (limit != 0) {
            builder.append("LIMIT ").append(limit);
        }
        LOG.info(builder.toString());
        return builder.toString();
    }

    /**
     * 生成新增 SQL，列名做标识符校验，列值全部使用 MyBatis 参数绑定。
     **/
    public String create(Map<String, Object> params) {
        String tableName = (String) params.get("tableName");
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) params.get("map");
        validateIdentifier(tableName, "table name");
        if (map == null || map.isEmpty()) {
            throw new SqlStatementException("The insert values cannot be empty.");
        }
        StringBuilder builder = new StringBuilder();
        // INSERT INTO `tableName`
        builder.append("INSERT INTO `").append(tableName).append("` ");
        StringBuilder values = new StringBuilder();
        StringBuilder keys = new StringBuilder();
        int index = 0;
        keys.append("(");
        values.append("(");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            validateIdentifier(entry.getKey(), "field name");
            if (index++ > 0) {
                values.append(", ");
                keys.append(", ");
            }
            values.append("#{map['").append(entry.getKey()).append("']}");
            keys.append("`").append(entry.getKey()).append("`");
        }
        keys.append(") ");
        values.append(") ");
        builder.append(keys).append("VALUES ");
        builder.append(values).append(";");
        LOG.info(builder.toString());
        return builder.toString();
    }

    /**
     * 生成更新 SQL，SET 与 WHERE 的值全部使用 MyBatis 参数绑定。
     **/
    public String update(Map<String, Object> params){
        String tableName = (String) params.get("tableName");
        @SuppressWarnings("unchecked")
        Map<String,Object> key = (Map<String, Object>) params.get("key");
        @SuppressWarnings("unchecked")
        Map<String,Object> map = (Map<String, Object>) params.get("map");
        validateIdentifier(tableName, "table name");
        if (key == null || key.isEmpty() || map == null || map.isEmpty()) {
            throw new SqlStatementException("The update keys or values cannot be empty.");
        }
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE `").append(tableName).append("` ");
        StringBuilder  resBuf = new StringBuilder();
        StringBuilder  keyBuf = new StringBuilder();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            validateIdentifier(entry.getKey(), "field name");
            if (!resBuf.isEmpty()) {
                resBuf.append(", ");
            }
            resBuf.append("`").append(entry.getKey()).append("` = #{map['").append(entry.getKey()).append("']}");
        }
        for( Map.Entry<String, Object> entry : key.entrySet()){
            validateIdentifier(entry.getKey(), "field name");
            if(!keyBuf.isEmpty()){
                keyBuf.append(" AND ");
            }
            keyBuf.append("`").append(entry.getKey()).append("` = #{key['").append(entry.getKey()).append("']}");
        }
        builder.append("SET ").append(resBuf).append(" WHERE ").append(keyBuf).append(";");
        LOG.info(builder.toString());
        return builder.toString();
    }

    /**
     * 生成删除 SQL，WHERE 值全部使用 MyBatis 参数绑定。
     **/
     public String delete(Map<String, Object> params){
        String tableName = (String) params.get("tableName");
        @SuppressWarnings("unchecked")
        Map<String,Object> keys = (Map<String, Object>) params.get("keys");
        validateIdentifier(tableName, "table name");
        if (keys == null || keys.isEmpty()) {
            throw new SqlStatementException("The delete keys cannot be empty.");
        }
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM `").append(tableName).append("` ");
        StringBuilder  keyBuf = new StringBuilder();
        for( Map.Entry<String, Object> entry : keys.entrySet()){
            validateIdentifier(entry.getKey(), "field name");
            if(!keyBuf.isEmpty()){
                keyBuf.append(" AND ");
            }
            keyBuf.append("`").append(entry.getKey()).append("` = #{keys['").append(entry.getKey()).append("']}");
        }
        builder.append("WHERE ").append(keyBuf).append(";");
        return builder.toString();
     }

    /**
     * SQL 标识符兜底校验，白名单校验由服务层完成。
     **/
    private void validateIdentifier(String identifier, String label) {
        if (identifier == null || !identifier.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new SqlStatementException("Invalid " + label + ": " + identifier);
        }
    }
}
