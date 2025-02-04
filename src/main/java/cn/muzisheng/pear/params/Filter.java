package cn.muzisheng.pear.params;

import cn.muzisheng.pear.constant.Constant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;

/**
 * 过滤字段
 **/
@Data
public class Filter {
    /**
     * 字段是否为时间类型
     **/
    private boolean isTimeType;
    /**
     * 字段名
     **/
    private String name;
    /**
     * 操作符
     **/
    private String op;
    /**
     * 值
     **/
    private Object value;

}
