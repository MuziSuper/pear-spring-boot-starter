package cn.muzisheng.pear.params;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.exception.IllegalException;
import lombok.Data;
import java.util.Collection;

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
     * 校验 filter 基础结构，字段白名单由 AdminServiceImpl 负责。
     **/
    public void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalException("Filter name cannot be empty.");
        }
        if (op == null || op.isBlank()) {
            throw new IllegalException("Filter operator cannot be empty.");
        }
        switch (op.toLowerCase()) {
            case Constant.FILTER_OP_IS_NOT:
            case Constant.FILTER_OP_EQUAL:
            case Constant.FILTER_OP_NOT_EQUAL:
            case Constant.FILTER_OP_GREATER:
            case Constant.FILTER_OP_GREATER_OR_EQUAL:
            case Constant.FILTER_OP_LESS:
            case Constant.FILTER_OP_LESS_OR_EQUAL:
                return;
            case Constant.FILTER_OP_IN:
            case Constant.FILTER_OP_NOT_IN:
                validateNonEmptyValues("IN/NOT_IN filter value cannot be empty.");
                return;
            case Constant.FILTER_OP_BETWEEN:
                if (valueSize() != 2) {
                    throw new IllegalException("BETWEEN filter value must contain exactly two values.");
                }
                return;
            case Constant.FILTER_OP_LIKE:
                validateLikeValue();
                return;
            default:
                throw new IllegalException("Invalid filter operator: " + op);
        }
    }

    private void validateNonEmptyValues(String message) {
        if (valueSize() <= 0) {
            throw new IllegalException(message);
        }
    }

    private int valueSize() {
        if (value instanceof Collection<?> collection) {
            return collection.size();
        }
        if (value instanceof Object[] values) {
            return values.length;
        }
        return value == null ? 0 : 1;
    }

    private void validateLikeValue() {
        if (value instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                throw new IllegalException("LIKE filter value cannot be empty.");
            }
            for (Object item : collection) {
                if (!(item instanceof String)) {
                    throw new IllegalException("LIKE filter value must be a string or string collection.");
                }
            }
            return;
        }
        if (value instanceof Object[] values) {
            if (values.length == 0) {
                throw new IllegalException("LIKE filter value cannot be empty.");
            }
            for (Object item : values) {
                if (!(item instanceof String)) {
                    throw new IllegalException("LIKE filter value must be a string or string collection.");
                }
            }
            return;
        }
        if (!(value instanceof String)) {
            throw new IllegalException("LIKE filter value must be a string or string collection.");
        }
    }

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
