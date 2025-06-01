package cn.muzisheng.pear.mapper.dao;

import cn.muzisheng.pear.exception.GeneralException;
import cn.muzisheng.pear.exception.SqlStatementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AdminDAO {
    private static final Logger LOG = LoggerFactory.getLogger(AdminDAO.class);

    public String selectFirst(String tableName, Map<String, Object> keys) {
        StringBuilder builder = new StringBuilder();
        // SELECT * FROM `tableName` WHERE
        builder.append("SELECT * FROM `").append(tableName).append("` ").append("WHERE ");
        int index = 0;
        for (Map.Entry<String, Object> entry : keys.entrySet()) {
            if (index > 0) {
                // AND
                builder.append(" AND ");
            }
            // `key` = `value`
            if(entry.getValue() instanceof Number){
                builder.append("`").append(entry.getKey()).append("`").append(" = ").append(entry.getValue()).append(" ");
            }else{
                builder.append("`").append(entry.getKey()).append("`").append(" = ").append("'").append(entry.getValue()).append("' ");
            }
            index++;
        }
        // LIMIT 1
        builder.append("LIMIT 1");
        LOG.info(builder.toString());
        return builder.toString();
    }

    public String query(String tableName, String showClause, String whereClause, String orderClause, String extraLikeClause, int limit) {
        StringBuilder builder = new StringBuilder();
        if (showClause == null) {
            showClause = "*";
        }
        // SELECT * FROM `tableName`
        builder.append("SELECT ").append(showClause).append(" ");
        if (tableName.isEmpty()) {
            throw new SqlStatementException("The table name cannot be left blank.");
        }
        builder.append("FROM `").append(tableName).append("` ");
        if (!whereClause.isEmpty()) {
            builder.append("WHERE (").append(whereClause).append(") ");
        }
        if (!extraLikeClause.isEmpty()) {
            if (!whereClause.isEmpty()) {
                builder.append("AND ");
            } else {
                builder.append("WHERE ");
            }
            builder.append("(").append(extraLikeClause).append(") ");
        }
        if (!orderClause.isEmpty()) {
            builder.append("ORDER BY ").append(orderClause).append(" ");
        }
        if (limit != 0) {
            builder.append("LIMIT ").append(limit);
        }
        LOG.info(builder.toString());
        return builder.toString();
    }

    public String create(String tableName, Map<String, Object> map) {
        StringBuilder builder = new StringBuilder();
        // INSERT INTO `tableName`
        builder.append("INSERT INTO `").append(tableName).append("` ");
        StringBuilder values = new StringBuilder();
        StringBuilder keys = new StringBuilder();
        int index = 0;
        keys.append("(");
        values.append("(");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (index++ > 0) {
                values.append(", ");
                keys.append(", ");
            }
            if(entry.getValue() instanceof Number){
                values.append(entry.getValue());
            }else{
                values.append("'").append(entry.getValue()).append("'");
            }
//            values.append(entry.getValue());
            keys.append("`").append(entry.getKey()).append("`");
        }
        keys.append(") ");
        values.append(") ");
        builder.append(keys).append("VALUES ");
        builder.append(values).append(";");
        LOG.info(builder.toString());
        return builder.toString();
    }
    public String update(String tableName,Map<String,Object> key,Map<String,Object> map){
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE `").append(tableName).append("` ");
        StringBuilder  resBuf = new StringBuilder();
        StringBuilder  keyBuf = new StringBuilder();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!resBuf.isEmpty()) {
                resBuf.append(", ");
            }
            if(entry.getValue() instanceof Number){
                resBuf.append("`").append(entry.getKey()).append("` = ").append(entry.getValue());
            }else{
                resBuf.append("`").append(entry.getKey()).append("` = ").append("'").append(entry.getValue()).append("'");
            }
        }
        for( Map.Entry<String, Object> entry : key.entrySet()){
            if(!keyBuf.isEmpty()){
                keyBuf.append(" AND ");
            }
             if(entry.getValue() instanceof Number){
                  keyBuf.append("`").append(entry.getKey()).append("` = ").append(entry.getValue());
             }
             else{
                 keyBuf.append("`").append(entry.getKey()).append("` = ").append("'").append(entry.getValue()).append("'");
             }
        }
        builder.append("SET ").append(resBuf).append(" WHERE ").append(keyBuf).append(";");
        LOG.info(builder.toString());
        return builder.toString();
    }
     public String delete(String tableName,Map<String,Object> keys){
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM `").append(tableName).append("` ");
        StringBuilder  keyBuf = new StringBuilder();
        for( Map.Entry<String, Object> entry : keys.entrySet()){
            if(!keyBuf.isEmpty()){
                keyBuf.append(" AND ");
            }
             if(entry.getValue() instanceof Number){
                  keyBuf.append("`").append(entry.getKey()).append(entry.getValue());
             }else if(entry.getValue() instanceof String){
                 keyBuf.append("`").append(entry.getKey()).append("` = ").append("'").append(entry.getValue()).append("'");
             }
        }
        builder.append("WHERE ").append(keyBuf).append(";");
        return builder.toString();
     }

}
