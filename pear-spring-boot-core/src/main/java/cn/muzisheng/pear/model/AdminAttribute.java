package cn.muzisheng.pear.model;

import lombok.Data;

/**
 * 字段的额外属性
 **/
@Data
public class AdminAttribute {
    /**
     * 默认值
     **/
    private String defaultValue;
    /**
     * 字段值可选项列表
     **/
    private AdminSelectOption[] choices;
    /**
     * 是否单选
     **/
    private boolean singleChoice;
    /**
     * 前端控件类型
     **/
    private String widget;
    /**
     * 前端过滤该字段的控件类型
     **/
    private String filterWidget;
    /**
     * 帮助性信息
     **/
    private String help;
}
