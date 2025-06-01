package cn.muzisheng.pear.mapper;

import cn.muzisheng.pear.exception.SqlStatementException;
import cn.muzisheng.pear.mapper.dao.AdminDAO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {
    /**
     * 根据keys参数查询tableName表的第一条数据
     * @param tableName 表名
     * @param keys 主键或唯一键的集合
     * @return 查询到的数据
     **/
    @SelectProvider(type = AdminDAO.class, method = "selectFirst")
    List<Map<String, Object>> selectFirst(String tableName, Map<String,Object> keys);
     /**
     * 查询tableName表
     * @param tableName 表名
     * @param showClause 查询的字段
     * @param whereClause 查询条件
     * @param orderClause 排序条件
     * @param extraLikeClause 额外的模糊查询条件
     * @param limit 查询数量限制
     * @return 查询到的数据
     **/
    @SelectProvider(type = AdminDAO.class, method = "query")
    List<Map<String, Object>> query(String tableName,String showClause, String whereClause, String orderClause, String extraLikeClause,int limit);
     /**
     * 创建tableName表
     * @param tableName 表名
     * @param map 数据集合
     * @return 创建数量，1为成功，0为失败
     **/
    @InsertProvider(type = AdminDAO.class, method = "create")
    int create(String tableName,Map<String,Object> map);
     /**
     * 更新tableName表
     * @param tableName 表名
     * @param key 更新的主键或唯一键集合
     * @param map 更新的数据集合
     * @return 更新数量，1为成功，0为失败
     **/
    @UpdateProvider(type = AdminDAO.class, method = "update")
    int update(String tableName,Map<String,Object> key,Map<String,Object> map);
     /**
     * 删除tableName表
     * @param tableName 表名
     * @param key 删除的主键或唯一键集合
     * @return 删除数量，1为成功，0为失败
     **/
     @DeleteProvider(type = AdminDAO.class, method = "delete")
     int delete(String tableName,Map<String,Object> keys);

}
