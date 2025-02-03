package cn.muzisheng.pear.params;

import cn.muzisheng.pear.model.Order;
/**
 * 查询表单
 **/
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
    private Filter[] filters;
    /**
     * 排序条件列表
     **/
    private Order[] orders;
    /**
     * 是否关联查询
     **/
    private boolean foreign;
    /**
     * 指定查询显示的字段
     **/
    private String[] viewFields;
    /**
     * 指定搜索的字段
     **/
    private String[] searchFields;
}
