package cn.muzisheng.pear.mapper;

import cn.muzisheng.pear.dao.AdminDAO;
import cn.muzisheng.pear.params.SqlQuery;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {
    /**
     * 根据 keys 参数查询 tableName 表的第一条数据。
     *
     * @param tableName 表名
     * @param keys 主键或唯一键的集合
     * @return 查询到的数据
     **/
    @SelectProvider(type = AdminDAO.class, method = "selectFirst")
    List<Map<String, Object>> selectFirst(@Param("tableName") String tableName, @Param("keys") Map<String,Object> keys);
     /**
     * 查询 tableName 表，SQL 片段已在服务层完成白名单校验。
     *
     * @param query 查询 SQL 片段与绑定参数
     * @return 查询到的数据
     **/
    @SelectProvider(type = AdminDAO.class, method = "query")
    List<Map<String, Object>> query(SqlQuery query);
     /**
     * 创建 tableName 表的数据。
     *
     * @param tableName 表名
     * @param map 数据集合
     * @return 创建数量，1为成功，0为失败
     **/
    @InsertProvider(type = AdminDAO.class, method = "create")
    int create(@Param("tableName") String tableName, @Param("map") Map<String,Object> map);
     /**
     * 更新 tableName 表的数据。
     *
     * @param tableName 表名
     * @param key 更新的主键或唯一键集合
     * @param map 更新的数据集合
     * @return 更新数量，1为成功，0为失败
     **/
    @UpdateProvider(type = AdminDAO.class, method = "update")
    int update(@Param("tableName") String tableName, @Param("key") Map<String,Object> key, @Param("map") Map<String,Object> map);
     /**
     * 删除 tableName 表的数据。
     *
     * @param tableName 表名
     * @param keys 删除的主键或唯一键集合
     * @return 删除数量，1为成功，0为失败
     **/
     @DeleteProvider(type = AdminDAO.class, method = "delete")
     int delete(@Param("tableName") String tableName, @Param("keys") Map<String,Object> keys);

}
