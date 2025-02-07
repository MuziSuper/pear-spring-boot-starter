package cn.muzisheng.pear.mapper;

import cn.muzisheng.pear.dao.AdminDAO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.Map;


public interface AdminMapper {
    @SelectProvider(type = AdminDAO.class, method = "selectFirst")
    public Object selectFirst(String tableName, Map<String,Object> keys);
}
