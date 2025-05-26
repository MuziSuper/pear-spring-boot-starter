package cn.muzisheng.pear.mapper;

import cn.muzisheng.pear.mapper.dao.AdminDAO;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {
    @SelectProvider(type = AdminDAO.class, method = "selectFirst")
    List<Map<String, Object>> selectFirst(String tableName, Map<String,Object> keys);
    @SelectProvider(type = AdminDAO.class, method = "query")
    List<Map<String, Object>> query(String tableName,String showClause, String whereClause, String orderClause, String extraLikeClause,int limit);
    @InsertProvider(type = AdminDAO.class, method = "create")
    int create(String tableName,Map<String,Object> map);
    @UpdateProvider(type = AdminDAO.class, method = "update")
    int update(String tableName,Map<String,Object> map);


}
