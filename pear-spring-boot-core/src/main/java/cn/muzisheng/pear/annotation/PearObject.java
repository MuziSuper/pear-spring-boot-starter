package cn.muzisheng.pear.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PearObject {
    /**
     * 表名
     **/
    String TableName() default "";
    /**
     * 分组
     **/
    String group() default "";
    /**
     * 介绍
     **/
    String desc() default "";
    /**
     * 路径
     **/
    String path() default "";
    /**
     * 修改页面地址
     **/
    String editPage() default "";
    /**
     * 展示页面地址
     **/
    String listPage() default "";
    /**
     * 复数
     **/
    String pluralName() default "";
    /**
     * 图标地址
     **/
    String iconUrl() default "";
    /**
     * 是否显示此表
     **/
    boolean isInvisible() default false;
}
