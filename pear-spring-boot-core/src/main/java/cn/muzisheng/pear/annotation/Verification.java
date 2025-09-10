package cn.muzisheng.pear.annotation;

import cn.muzisheng.pear.model.OperationEnum;
import cn.muzisheng.pear.model.RoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 权限校验层
 * SystemVerify 系统级校验，对参数中存在request获取用户信息并确定isSuperUser或isStaff字段，默认为true
 * UserVerify 用户级校验，使用参数中存在adminObject的accessCheck钩子函数，默认为false
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Verification {
    // 系统级权限校验，对自动生成的CRUD方法按照AdminObject的permissions字段进行权限校验
    boolean SystemVerify() default true;
    // 用户级权限校验，调用AccessCheck钩子函数
    boolean UserVerify() default true;
    // 最小权限等级
    RoleEnum MinLevel() default RoleEnum.CUSTOMER;
    // admin行为
    OperationEnum Operation() default OperationEnum.ADMIN_NULL;
}
