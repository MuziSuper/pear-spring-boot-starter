package cn.muzisheng.pear.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 字段值可选项列表
 **/
@Data
@AllArgsConstructor
public class AdminSelectOption {
    /**
     * 键 - 对应客户端input的label
     **/
    private String label;
    /**
     * 值 - 对应客户端input的value
     **/
    private Object value;
}
