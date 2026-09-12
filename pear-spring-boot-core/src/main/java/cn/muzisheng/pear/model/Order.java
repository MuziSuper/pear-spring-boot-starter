package cn.muzisheng.pear.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.exception.IllegalException;

/**
 * 所有字段默认排序方式的列表
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    /**
     * 字段名
     **/
    private String Name;
    /**
     * 排序方式
     **/
    private String Op;

    /**
     * 校验排序结构，字段白名单由 AdminServiceImpl 负责。
     **/
    public void validate() {
        if (Name == null || Name.isBlank()) {
            throw new IllegalException("Order name cannot be empty.");
        }
        if (Op == null || (!Constant.ORDER_OP_ASC.equalsIgnoreCase(Op) && !Constant.ORDER_OP_DESC.equalsIgnoreCase(Op))) {
            throw new IllegalException("Invalid order direction: " + Op);
        }
    }
}
