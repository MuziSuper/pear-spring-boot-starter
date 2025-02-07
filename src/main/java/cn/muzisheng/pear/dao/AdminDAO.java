package cn.muzisheng.pear.dao;

import cn.muzisheng.pear.exception.GeneralException;
import cn.muzisheng.pear.mapper.AdminMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AdminDAO implements AdminMapper {

    @Override
    public Object selectFirst(String tableName, Map<String, Object> keys) {
        if(tableName.isEmpty()||keys.isEmpty()){
            throw new GeneralException("The table name and query conditions cannot be left blank.");
        }
        StringBuilder builder = new StringBuilder("SELECT * FROM "+tableName+" WHERE  ");
        int index=0;
        for (Map.Entry<String,Object> entry:keys.entrySet()){
            if(index>0){
                builder.append(" AND ");
            }
            builder.append(entry.getKey()).append(" = ").append("'").append(entry.getValue()).append("'");
            index++;
        }
        builder.append(" LIMIT 1");
        return builder.toString();
    }
}
