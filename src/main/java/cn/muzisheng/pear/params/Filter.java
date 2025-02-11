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
    /**
     * 根据filter字段，组装mysql语句
     **/
    public String getQueryClause() {
        String op = "";
        switch (this.op) {
            case Constant.FILTER_OP_IS_NOT:
                op = "IS NOT";
                break;
            case Constant.FILTER_OP_EQUAL:
                op = "=";
                break;
            case Constant.FILTER_OP_NOT_EQUAL:
                op = "<>";
                break;
            case Constant.FILTER_OP_IN:
                op = "IN";
                break;
            case Constant.FILTER_OP_NOT_IN:
                op = "NOT IN";
                break;
            case Constant.FILTER_OP_GREATER:
                op = ">";
                break;
            case Constant.FILTER_OP_GREATER_OR_EQUAL:
                op = ">=";
                break;
            case Constant.FILTER_OP_LESS:
                op = "<";
                break;
            case Constant.FILTER_OP_LESS_OR_EQUAL:
                op = "<=";
                break;
            case Constant.FILTER_OP_LIKE:
                op = "LIKE";
                break;
            case Constant.FILTER_OP_BETWEEN:
                op = "BETWEEN";
                return op;
        }
        if (op.isEmpty()) {
            return "";
        }
        return String.format("`%s` %s ", this.name, op);
    }
}
