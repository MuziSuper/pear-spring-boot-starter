package cn.muzisheng.pear.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PearObject {
    String TableName() default "";
    String group() default "";
    String desc() default "";
    String path() default "";
    String editPage() default "";
    String listPage() default "";
    String pluralName() default "";
    String iconUrl() default "";
    boolean isInvisible() default false;
}
