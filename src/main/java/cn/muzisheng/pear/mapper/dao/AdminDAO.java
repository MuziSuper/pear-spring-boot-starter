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
        if(tableName.isEmpty()||keys.isEmpty()){
            throw new GeneralException("The table name and query conditions cannot be left blank.");
        }
        builder.append("SELECT * FROM `").append(tableName).append("` ");
        int index=0;
        if(keys.size()>1) {
            builder.append("WHERE ");
            for (Map.Entry<String, Object> entry : keys.entrySet()) {
                if (index > 0) {
                    builder.append(" AND ");
                }
                builder.append("`").append(entry.getKey()).append("`").append(" = ").append("'").append(entry.getValue()).append("'");
                index++;
            }
        }
        builder.append(" LIMIT 1");
        LOG.info(builder.toString());
        return builder.toString();
    }

    public String query(String tableName, String showClause,String whereClause, String orderClause, String extraLikeClause,int limit) {
        StringBuilder builder=new StringBuilder();
        if (showClause == null){
            showClause = "*";
        }
        builder.append("SELECT ").append(showClause).append(" ");
        if(tableName.isEmpty()){
            throw new GeneralException("The table name cannot be left blank.");
        }
         builder.append("FROM `").append(tableName).append("` ");
        if(!whereClause.isEmpty()){
            builder.append("WHERE (").append(whereClause).append(") ");
        }
        if(!extraLikeClause.isEmpty()){
            if(!whereClause.isEmpty()){
                builder.append("AND ");
            }else{
                builder.append("WHERE ");
            }
            builder.append("(").append(extraLikeClause).append(") ");
        }
        if(!orderClause.isEmpty()){
            builder.append("ORDER BY ").append(orderClause).append(" ");
        }
        if(limit!=0){
            builder.append("LIMIT ").append(limit);
        }
        LOG.info(builder.toString());
        return builder.toString();
    }
    public String create(String tableName,Object object) {
        StringBuilder builder=new StringBuilder();
        if(tableName.isEmpty()){
            throw new GeneralException("The table name cannot be left blank.");
        }
        builder.append("INSERT INTO `").append(tableName).append("` ");
        StringBuilder values = new StringBuilder();
        StringBuilder keys = new StringBuilder();
        if(object instanceof Map map) {
            int index=0;
            keys.append("(");
            values.append("(");
            for (Map.Entry<String, Object> entry : ((Map<String,Object>)map).entrySet()) {
                if(index++>0){
                    values.append(", ");
                    keys.append(", ");
                }
                values.append("'").append(entry.getValue()).append("'");
                keys.append("'").append(entry.getKey()).append("'");
            }
            keys.append(")");
            values.append(") ");
            builder.append(keys);
            builder.append(" VALUES ");
            builder.append(values).append(";");
        }else{
            throw new SqlStatementException("The object is not a map.");
        }
        return builder.toString();
    }
}
