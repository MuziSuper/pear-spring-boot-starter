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
    boolean isShow() default false;
    boolean isEdit() default false;
    boolean isFilterable() default false;
    boolean isOrderable() default false;
    boolean isSearchable() default false;
    boolean isRequire() default false;
    boolean isPrimaryKey() default false;
    boolean isUniqueKey() default false;

}
