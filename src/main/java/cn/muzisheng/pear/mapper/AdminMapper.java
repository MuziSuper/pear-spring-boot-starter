package cn.muzisheng.pear.mapper;

import cn.muzisheng.pear.dao.AdminDAO;
import cn.muzisheng.pear.model.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {
    @SelectProvider(type = AdminDAO.class, method = "selectFirst")
    public List<Map<String, Object>> selectFirst(String tableName, Map<String,Object> keys);
    @SelectProvider(type = AdminDAO.class, method = "query")
    List<Map<String, Object>> query(String tableName,String showClause, String whereClause, String orderClause, String extraLikeClause,int limit);
}
