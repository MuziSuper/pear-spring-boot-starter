package cn.muzisheng.pear.model;

/**
 * 客户端查询结果
 **/
public class QueryResult {
    /**
     * 总条数
     **/
    private int totalCount;
    /**
     * 当前页码
     **/
    private int pos;
    /**
     * 每页条数
     **/
    private int limit;
    /**
     * 关键词
     **/
    private String keyword;
    /**
     * 数据
     **/
    private Object items;
}
