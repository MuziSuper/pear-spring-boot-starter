package cn.muzisheng.pear.utils;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<D> implements Serializable {
    private int code=200;
    private String message;
    private D data;
}
