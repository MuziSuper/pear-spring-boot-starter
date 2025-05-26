package cn.muzisheng.pear.annotation;

import org.springframework.core.annotation.Order;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Order(1)
public @interface PearField {
    /**
     * 是否显示
     **/
    boolean isShow() default true;
    /**
     * 是否可修改
     **/
    boolean isEdit() default true;
    /**
     * 是否可过滤
     **/
    boolean isFilterable() default true;
    /**
     * 是否可排序
     **/
    boolean isOrderable() default true;
    /**
     * 是否可查询
     **/
    boolean isSearchable() default true;
    /**
     * 是否需要
     **/
    boolean isRequire() default true;
    /**
     * 是否为主键
     **/
    boolean isPrimaryKey() default false;
    /**
     * 是否为唯一键
     **/
    boolean isUniqueKey() default false;
    /**
     * 客户端默认值
     **/
    String placeholder() default "";
    /**
     * 客户端显示名称
     **/
    String label() default "";

}
