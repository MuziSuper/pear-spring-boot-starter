package cn.muzisheng.pear.model;

import cn.muzisheng.pear.handler.AdminExtraHandler;
import lombok.Data;

/**
 * 后台操作api概括
 **/
@Data
public class AdminAction {
    /**
     * api路径
     **/
    private String path;
    /**
     * 操作名，eg: delete
     **/
    private String name;
    /**
     * 前端操作的标签文本
     **/
    private String label;
    /**
     * 前端图标
     **/
    private String icon;
    /**
     * 前端class样式
     **/
    private String clazz;
    /**
     * 是否不选定具体对象也可执行操作
     **/
    private boolean withoutObject;
    /**
     * 是否支持
     **/
    private boolean batch;
    /**
     * 后台操作处理
     **/
    private AdminExtraHandler handler;

}
