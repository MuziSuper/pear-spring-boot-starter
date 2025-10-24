package cn.muzisheng.pear.annotation;

import cn.muzisheng.pear.Constant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 权限校验, 主要分为系统级权限校验和用户级权限校验，对Operation行为
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Verification {
    // 是否启用系统级权限校验，对自动生成的CRUD方法按照AdminObject的permissions字段进行权限校验，默认为启用
    boolean SystemVerify() default true;
    // 是否启用用户级权限校验，调用AccessCheck钩子函数，默认为启用
    boolean UserVerify() default true;
    // 权限
    String Permission() default Constant.ADMIN_NULL;
}
