package cn.muzisheng.pear.params;

import java.util.Map;

/**
 * admin查询返回结果
 **/
public class AdminQueryResult {
    // type AdminQueryResult struct {
    //	TotalCount int              `json:"total,omitempty"`
    //	Pos        int              `json:"pos,omitempty"`
    //	Limit      int              `json:"limit,omitempty"`
    //	Keyword    string           `json:"keyword,omitempty"`
    //	Items      []map[string]any `json:"items"`
    //	objects    []any            `json:"-"`
    //}
    /**
     * 数据总条数
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
     * 所有数据的键值对集合
     **/
    private Map<String, Object> items;
    /**
     * 所有数据列表
     **/
    private Object[] objects;
}
