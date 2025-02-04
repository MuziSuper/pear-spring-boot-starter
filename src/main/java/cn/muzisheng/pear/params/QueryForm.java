package cn.muzisheng.pear.params;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.model.Order;
import lombok.Data;

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
    private Filter[] filters;
    /**
     * 排序条件列表
     **/
    private Order[] orders;
    /**
     * 是否关联查询
     **/
    private boolean foreignMode;
    /**
     * 指定查询显示的字段
     **/
    private String[] viewFields;
    /**
     * 指定搜索的字段
     **/
    private String[] searchFields;
    /**
     * 处理请求体中的数据
     **/
    public void defaultPrepareQuery() {
        if(this.pos<0){
            this.pos=0;
        }
        if(this.limit<0||this.limit> Constant.DEFAULT_QUERY_LIMIT){
            this.limit=Constant.DEFAULT_QUERY_LIMIT;
        }
    }
}
