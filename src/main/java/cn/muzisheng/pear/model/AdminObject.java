package cn.muzisheng.pear.model;

import cn.muzisheng.pear.handler.*;
import lombok.Data;

import java.util.Map;
/**
 * 加载客户端模型
 **/
@Data
public class AdminObject{
    /**
     * 模型
     **/
    private Class<?> model;
    /**
     * 所属组名，多个相关的表归为一类
     **/
    private String group;
    /**
     * 对象名称
     **/
    private String name;
    /**
     * 对象描述
     **/
    private String desc;
    /**
     * 其相关CURD的路径前缀
     **/
    private String path;
    /**
     * 可展示在前端的字段列表
     **/
    private String[] shows;
    /**
     * 所有字段默认排序方式的列表
     **/
    private Order[] Orders;
    /**
     * 可编辑的字段列表
     **/
    private String[] edits;
    /**
     * 可用于过滤的字段
     **/
    private String[] filters;
    /**
     * 可用于排序的字段
     **/
    private String[] Orderables;
    /**
     * 可用于搜索的字段
     **/
    private String[] searches;
    /**
     * 必须存在的字段
     **/
    private String[] requires;
    /**
     * 主键字段列表
     **/
    private String[] primaryKeys;
    /**
     * 唯一键字段列表
     **/
    private String[] uniqueKeys;
    /**
     * 复数名称
     **/
    private String pluralName;
    /**
     * 此表所有字段的详细配置
     **/
    private AdminField[] fields;
    /**
     * 编辑此表的前端路由
     **/
    private String editPage;
    /**
     * 展示此表的前端路由
     **/
    private String listPage;
    /**
     * 脚本文件对象
     **/
    private AdminScript[] AdminScripts;
    /**
     * 需要在前端下载的样式文件源地址列表
     **/
    private String[] styles;
    /**
     * 权限设置,不同的操作权限的有无
     **/
    private Map<String, Boolean> permissions;
    /**
     * 图标
     **/
    private AdminIcon icon;
    /**
     * 是否隐藏该对象
     **/
    private boolean invisible;
    /**
     * 用于描述数据库表字段的详细属性配置
     **/
    private Map<String,AdminAttribute> Attributes;
    /**
     * 数据库表内实际名称
     **/
    private String tableName;
//    /**
//     * 数据库表关联模型类型的反射类型
//     **/
//    modelElem reflect.Type `json:"-"`

    /**
     * 执行某些数据库操作时需要忽略的字段
     **/
    private Map<String,Boolean> ignores;
    /**
     * 模型内主键字段的映射
     **/
    private Map<String,String> primaryKeyMap;
    /**
     * 渲染页面方法
     **/
    private ViewOnSite AdminViewOnSite;
    /**
     * 身份验证方法
     **/
    private AccessCheck accessCheck;
    /**
     * 预创建钩子方法
     **/
    private BeforeCreate beforeCreate;
    /**
     * 预更新钩子方法
     **/
    private BeforeUpdate beforeUpdate;
    /**
     * 预删除钩子方法
     **/
    private BeforeDelete beforeDelete;
    /**
     * 预渲染钩子方法
     **/
    private BeforeRender beforeRender;
}
