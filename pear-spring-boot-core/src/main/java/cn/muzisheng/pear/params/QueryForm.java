package cn.muzisheng.pear.params;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.model.Order;
import lombok.Data;

import java.util.List;

/**
 * 查询表单
 **/
@Data
public class QueryForm {
    /**
     * 分页起始位置
     **/
    private int pos;
    /**
     * 每页数量
     **/
    private int limit;
    /**
     * 关键词,用于模糊查询
     **/
    private String keyword;
    /**
     * 过滤条件列表
     **/
    private List<Filter> filters;
    /**
     * 排序条件列表
     **/
    private List<Order> orders;
    /**
     * 是否关联查询
     **/
    private boolean foreignMode;
    /**
     * 指定查询显示的字段
     **/
    private List<String> viewFields;
    /**
     * 指定搜索的字段
     **/
    private List<String> searchFields;
    /**
     * 处理请求体中的数据
     **/
    public void defaultPrepareQuery() {
        if(this.pos<0){
            throw new IllegalException("Query pos cannot be negative.");
        }
        if(this.limit<0||this.limit> Constant.DEFAULT_QUERY_LIMIT){
            throw new IllegalException("Query limit is out of range.");
        }
    }

    /**
     * 校验查询表单基础结构，字段白名单由 AdminServiceImpl 负责。
     **/
    public void validate() {
        defaultPrepareQuery();
        validateFilters();
        validateOrders();
        validateFieldList(viewFields, "viewFields");
        validateFieldList(searchFields, "searchFields");
    }

    private void validateFilters() {
        if (filters == null) {
            return;
        }
        for (Filter filter : filters) {
            if (filter == null) {
                throw new IllegalException("Filter cannot be null.");
            }
            filter.validate();
        }
    }

    private void validateOrders() {
        if (orders == null) {
            return;
        }
        for (Order order : orders) {
            if (order == null) {
                throw new IllegalException("Order cannot be null.");
            }
            order.validate();
        }
    }

    private void validateFieldList(List<String> fields, String label) {
        if (fields == null) {
            return;
        }
        for (String field : fields) {
            if (field == null || field.isBlank()) {
                throw new IllegalException(label + " cannot contain empty field.");
            }
        }
    }
}
