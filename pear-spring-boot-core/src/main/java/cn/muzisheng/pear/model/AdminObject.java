package cn.muzisheng.pear.model;

import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.GeneralException;
import cn.muzisheng.pear.handler.*;
import cn.muzisheng.pear.initialize.AdminContainer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加载客户端模型
 **/
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminObject {
    /**
     * 模型
     **/
    private Class<?> model;
    /**
     * 模型实例
     **/
    private Object modelElem;
    /**
     * 所属组名，多个相关的表归为一类
     **/
    private String group;
    /**
     * 模型名称
     **/
    private String name;
    /**
     * 对象描述
     **/
    private String desc;
    /**
     * 其相关CURD的路径前缀，eg: /AdminObject.name
     **/
    private String path;
    /**
     * 可展示在前端的字段列表,默认存储为驼峰式即AdminField.fieldName
     **/
    private List<String> shows;
    /**
     * 所有字段默认排序方式的列表
     **/
    private List<Order> Orders;
    /**
     * 可编辑的字段列表,默认存储为驼峰式即AdminField.fieldName
     **/
    private List<String> edits;
    /**
     * 可用于过滤的字段,默认存储为驼峰式即AdminField.fieldName
     **/
    private List<String> filterables;
    /**
     * 可用于排序的字段,默认存储为驼峰式即AdminField.fieldName
     **/
    private List<String> Orderables;
    /**
     * 可用于搜索的字段,默认存储为驼峰式即AdminField.fieldName
     **/
    private List<String> searches;
    /**
     * 必须存在的字段,默认存储为驼峰式即AdminField.fieldName
     **/
    private List<String> requires;
    /**
     * 主键字段列表,默认存储为驼峰式即AdminField.fieldName
     **/
    private List<String> primaryKeys;
    /**
     * 唯一键字段列表,默认存储为驼峰式即AdminField.fieldName
     **/
    private List<String> uniqueKeys;
    /**
     * 复数名称
     **/
    private String pluralName;
    /**
     * 此表所有字段的详细配置
     **/
    private List<AdminField> fields;
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
    private List<AdminScript> AdminScripts;
    /**
     * 需要在前端下载的样式文件源地址列表
     **/
    private List<String> styles;

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
    private Map<String, AdminAttribute> Attributes;
    /**
     * 数据库表名称,默认存储为驼峰式即AdminObject.tableName
     **/
    private String tableName;
    /**
     * 执行某些数据库操作时需要忽略的字段
     **/
    private Map<String, Boolean> ignores;
    /**
     * 模型内主键字段的映射
     **/
    private Map<String, String> primaryKeyMap;
    /**
     * 渲染页面方法
     **/
    private AdminViewOnSite adminViewOnSite;
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
     * 预展示数据钩子方法
     **/
    private BeforeRender beforeRender;
    /**
     * 创建完成钩子方法
     **/
    private AfterCreate afterCreate;
    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // 忽略空值
        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            return super.toString();
        }
    }

    public static class BuilderFactory {
        private final AdminObject adminObject;
        public BuilderFactory(Class<?> clazz) {
            AdminObject adminObject= AdminContainer.getAdminObject(clazz);
            if(adminObject == null){
                throw new GeneralException("AdminObject of "+clazz.getName()+" is null.");
            }
            this.adminObject=adminObject;
        }
        public BuilderFactory setOrders(List<Order> orders){
            adminObject.setOrders(orders);
            return this;
        }

        public BuilderFactory setOrder(Order order){
            if(adminObject.getOrders()==null){
                adminObject.setOrders(new ArrayList<>());
            }
            adminObject.getOrders().add(order);
            return this;
        }
        public BuilderFactory setStyles(List<String> styles){
            adminObject.setStyles(styles);
            return this;
        }
        public BuilderFactory setAdminScripts(List<AdminScript> AdminScripts){
            adminObject.setAdminScripts(AdminScripts);
            return this;
        }
        public BuilderFactory setAdminScript(AdminScript AdminScript){
            if(adminObject.getAdminScripts()==null){
                adminObject.setAdminScripts(new ArrayList<>());
            }
            adminObject.getAdminScripts().add(AdminScript);
            return this;
        }

        public BuilderFactory setAttributes(Map<String, AdminAttribute> Attributes){
            adminObject.setAttributes(Attributes);
            return this;
        }
        public BuilderFactory setAccessCheck(AccessCheck accessCheck){
            adminObject.setAccessCheck(accessCheck);
            return this;
        }
        public BuilderFactory setBeforeCreate(BeforeCreate beforeCreate){
            adminObject.setBeforeCreate(beforeCreate);
            return this;
        }
        public BuilderFactory setBeforeUpdate(BeforeUpdate beforeUpdate){
            adminObject.setBeforeUpdate(beforeUpdate);
            return this;
        }
        public BuilderFactory setBeforeDelete(BeforeDelete beforeDelete){
            adminObject.setBeforeDelete(beforeDelete);
            return this;
        }
        public BuilderFactory setBeforeRender(BeforeRender beforeRender){
            adminObject.setBeforeRender(beforeRender);
            return this;
        }
        public BuilderFactory setAdminViewOnSite(AdminViewOnSite adminViewOnSite){
            adminObject.setAdminViewOnSite(adminViewOnSite);
            return this;
        }
        public AdminObject build(){
            return adminObject;
        }
    }
}
