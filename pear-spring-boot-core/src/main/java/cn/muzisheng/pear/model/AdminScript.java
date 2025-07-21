package cn.muzisheng.pear.model;

import lombok.Data;

/**
 * 脚本文件对象
 **/
@Data
public class AdminScript {
    /**
     * 脚本文件路径
     **/
    private String src;
    /**
     * 脚本文件类型
     **/
    private Boolean onload;
}
