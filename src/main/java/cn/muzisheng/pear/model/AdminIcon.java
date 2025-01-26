package cn.muzisheng.pear.model;

import lombok.Data;

/**
 * 图标,可以为svg或url
 **/
@Data
public class AdminIcon {
    private String url;
    private String svg;
}
