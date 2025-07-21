package cn.muzisheng.pear.model;

import lombok.Data;

/**
 * 此表所有字段的详细配置
 **/
@Data
public class AdminField {
    /**
     * 定义前端表单元素的提示信息
     **/
    private String placeholder;
    /**
     * 定义前端表单元素的显示样式
     **/
    private String label;
    /**
     * 定义此字段是否映射到数据库中，可能用于辅助和计算等功能
     **/
    private Boolean notColumn;
    /**
     * 定义此字段是否是必填字段
     **/
    private Boolean required;
    /**
     * 定义此字段在模型中的名称
     **/
    private String name;
    /**
     * 定义此字段在数据库中的名称, 系统默认为驼峰式
     **/
    private String fieldName;
    /**
     * 定义此字段的数据类型
     **/
    private Class<?> type;
    /**
     * 定义此字段的注解
     **/
    private String[] annotation;
    /**
     * 定义此字段的额外属性
     **/
    private AdminAttribute attribute;
    /**
     * 能否为空
     **/
    private Boolean canNull;
    /**
     * 是否为列表
     **/
    private Boolean isArray;
    /**
     * 是否为主键
     **/
    private Boolean primary;
    /**
     * 此字段存在则证明为外键，描述了外键的信息
     **/
    private AdminForeign foreign;
    /**
     * 字段是否数据库自动生成
     **/
    private Boolean isAutoId;
    /**
     * 自动更新时间
     **/
    private Boolean isAutoUpdateTime;
    /**
     * 自动插入时间
     **/
    private Boolean isAutoInsertTime;

}
