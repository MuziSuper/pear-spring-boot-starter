package cn.muzisheng.pear.aspect;

import cn.muzisheng.pear.annotation.*;
import cn.muzisheng.pear.constant.Constant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class HookAspect {
    @Pointcut("@annotation(cn.muzisheng.pear.annotation.BeforeCreate)")
    public void beforeCreate() {
    }

    @Pointcut("@annotation(cn.muzisheng.pear.annotation.BeforeRender)")
    public void beforeRender() {
    }

    @Pointcut("@annotation(cn.muzisheng.pear.annotation.BeforeDelete)")
    public void beforeDelete() {
    }

    @Pointcut("@annotation(cn.muzisheng.pear.annotation.BeforeUpdate)")
    public void beforeUpdate() {
    }

    @Pointcut("@annotation(cn.muzisheng.pear.annotation.AccessCheck)")
    public void accessCheck() {
    }
    @Pointcut("@annotation(cn.muzisheng.pear.annotation.AdminViewOnSite)")
    public void adminViewOnSite() {
    }

    @Around("beforeCreate()")
    public Object beforeCreateFunc(ProceedingJoinPoint jp) throws Throwable {
        // 获取方法签名
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        // 通过方法签名获取方法其上的注解
        BeforeCreate beforeCreate = method.getAnnotation(BeforeCreate.class);
        // 获取方法参数
        Object[] methodArgs = jp.getArgs();
        // 获取方法对象
        Object methodTarget = jp.getTarget();
        try {
            // 若方法其上的注解存在
            if (beforeCreate != null) {
                List<Object> actualArgs = new ArrayList<>();
                // 通过方法对象获取其类对象，并通过注解的value钩子方法名获取其方法对象
                Method hookmethod = methodTarget.getClass().getMethod(beforeCreate.value());

                // 若注解args所需参数不为空
                if (beforeCreate.args().length != 0) {
                    // 获取带参的钩子方法对象
                    hookmethod = methodTarget.getClass().getMethod(beforeCreate.value(), methodArgs.getClass());
                    // 将注解中标注需要的对象名其数据存入实参列表中
                    for (String needArg : beforeCreate.args()) {
                        for (Object methodArg : methodArgs) {
                            if (methodArg.getClass().getSimpleName().equals(needArg)) {
                                actualArgs.add(methodArg);
                            }
                        }
                    }
                    // 调用带参钩子方法
                    hookmethod.invoke(methodTarget, actualArgs.toArray());
                }
                // 调用无参钩子方法
                hookmethod.invoke(methodTarget);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(Constant.HOOK_UNCHECK_EXCEPTION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(Constant.HOOK_NOTFOUND_EXCEPTION);
        }
        return jp.proceed();
    }
    @Around("beforeDelete()")
    public Object beforeDeleteFunc(ProceedingJoinPoint jp) throws Throwable {
        // 获取方法签名
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        // 通过方法签名获取方法其上的注解
        BeforeDelete beforeCreate = method.getAnnotation(BeforeDelete.class);
        // 获取方法参数
        Object[] methodArgs = jp.getArgs();
        // 获取方法对象
        Object methodTarget = jp.getTarget();
        try {
            // 若方法其上的注解存在
            if (beforeCreate != null) {
                List<Object> actualArgs = new ArrayList<>();
                // 通过方法对象获取其类对象，并通过注解的value钩子方法名获取其方法对象
                Method hookmethod = methodTarget.getClass().getMethod(beforeCreate.value());
                // 若注解args所需参数不为空
                if (beforeCreate.args().length != 0) {
                    // 获取带参的钩子方法对象
                    hookmethod = methodTarget.getClass().getMethod(beforeCreate.value(), methodArgs.getClass());
                    // 将注解中标注需要的对象名其数据存入实参列表中
                    for (String needArg : beforeCreate.args()) {
                        for (Object methodArg : methodArgs) {
                            if (methodArg.getClass().getSimpleName().equals(needArg)) {
                                actualArgs.add(methodArg);
                            }
                        }
                    }
                    // 调用带参钩子方法
                    hookmethod.invoke(methodTarget, actualArgs.toArray());
                }
                // 调用无参钩子方法
                hookmethod.invoke(methodTarget);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(Constant.HOOK_UNCHECK_EXCEPTION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(Constant.HOOK_NOTFOUND_EXCEPTION);
        }
        return jp.proceed();
    }
    @Around("beforeUpdate()")
    public Object beforeUpdateFunc(ProceedingJoinPoint jp) throws Throwable {
        // 获取方法签名
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        // 通过方法签名获取方法其上的注解
        BeforeUpdate beforeCreate = method.getAnnotation(BeforeUpdate.class);
        // 获取方法参数
        Object[] methodArgs = jp.getArgs();
        // 获取方法对象
        Object methodTarget = jp.getTarget();
        try {
            // 若方法其上的注解存在
            if (beforeCreate != null) {
                List<Object> actualArgs = new ArrayList<>();
                // 通过方法对象获取其类对象，并通过注解的value钩子方法名获取其方法对象
                Method hookmethod = methodTarget.getClass().getMethod(beforeCreate.value());
                // 若注解args所需参数不为空
                if (beforeCreate.args().length != 0) {
                    // 获取带参的钩子方法对象
                    hookmethod = methodTarget.getClass().getMethod(beforeCreate.value(), methodArgs.getClass());
                    // 将注解中标注需要的对象名其数据存入实参列表中
                    for (String needArg : beforeCreate.args()) {
                        for (Object methodArg : methodArgs) {
                            if (methodArg.getClass().getSimpleName().equals(needArg)) {
                                actualArgs.add(methodArg);
                            }
                        }
                    }
                    // 调用带参钩子方法
                    hookmethod.invoke(methodTarget, actualArgs.toArray());
                }
                // 调用无参钩子方法
                hookmethod.invoke(methodTarget);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(Constant.HOOK_UNCHECK_EXCEPTION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(Constant.HOOK_NOTFOUND_EXCEPTION);
        }
        return jp.proceed();
    }
    @Around("beforeRender()")
    public Object beforeRenderFunc(ProceedingJoinPoint jp) throws Throwable {
        // 获取方法签名
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        // 通过方法签名获取方法其上的注解
        BeforeRender beforeCreate = method.getAnnotation(BeforeRender.class);
        // 获取方法参数
        Object[] methodArgs = jp.getArgs();
        // 获取方法对象
        Object methodTarget = jp.getTarget();
        try {
            // 若方法其上的注解存在
            if (beforeCreate != null) {
                List<Object> actualArgs = new ArrayList<>();
                // 通过方法对象获取其类对象，并通过注解的value钩子方法名获取其方法对象
                Method hookmethod = methodTarget.getClass().getMethod(beforeCreate.value());
                // 若注解args所需参数不为空
                if (beforeCreate.args().length != 0) {
                    // 获取带参的钩子方法对象
                    hookmethod = methodTarget.getClass().getMethod(beforeCreate.value(), methodArgs.getClass());
                    // 将注解中标注需要的对象名其数据存入实参列表中
                    for (String needArg : beforeCreate.args()) {
                        for (Object methodArg : methodArgs) {
                            if (methodArg.getClass().getSimpleName().equals(needArg)) {
                                actualArgs.add(methodArg);
                            }
                        }
                    }
                    // 调用带参钩子方法
                    hookmethod.invoke(methodTarget, actualArgs.toArray());
                }
                // 调用无参钩子方法
                hookmethod.invoke(methodTarget);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(Constant.HOOK_UNCHECK_EXCEPTION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(Constant.HOOK_NOTFOUND_EXCEPTION);
        }
        return jp.proceed();
    }
    @Around("accessCheck()")
    public Object accessCheckFunc(ProceedingJoinPoint jp) throws Throwable {
        // 获取方法签名
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        // 通过方法签名获取方法其上的注解
        AccessCheck beforeCreate = method.getAnnotation(AccessCheck.class);
        // 获取方法参数
        Object[] methodArgs = jp.getArgs();
        // 获取方法对象
        Object methodTarget = jp.getTarget();
        try {
            // 若方法其上的注解存在
            if (beforeCreate != null) {
                List<Object> actualArgs = new ArrayList<>();
                // 通过方法对象获取其类对象，并通过注解的value钩子方法名获取其方法对象
                Method hookmethod = methodTarget.getClass().getMethod(beforeCreate.value());
                // 若注解args所需参数不为空
                if (beforeCreate.args().length != 0) {
                    // 获取带参的钩子方法对象
                    hookmethod = methodTarget.getClass().getMethod(beforeCreate.value(), methodArgs.getClass());
                    // 将注解中标注需要的对象名其数据存入实参列表中
                    for (String needArg : beforeCreate.args()) {
                        for (Object methodArg : methodArgs) {
                            if (methodArg.getClass().getSimpleName().equals(needArg)) {
                                actualArgs.add(methodArg);
                            }
                        }
                    }
                    // 调用带参钩子方法
                    hookmethod.invoke(methodTarget, actualArgs.toArray());
                }
                // 调用无参钩子方法
                hookmethod.invoke(methodTarget);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(Constant.HOOK_UNCHECK_EXCEPTION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(Constant.HOOK_NOTFOUND_EXCEPTION);
        }
        return jp.proceed();
    }
    @Around("adminViewOnSite()")
    public Object adminViewOnSiteFunc(ProceedingJoinPoint jp) throws Throwable {
        // 获取方法签名
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        // 通过方法签名获取方法其上的注解
        AdminViewOnSite beforeCreate = method.getAnnotation(AdminViewOnSite.class);
        // 获取方法参数
        Object[] methodArgs = jp.getArgs();
        // 获取方法对象
        Object methodTarget = jp.getTarget();
        try {
            // 若方法其上的注解存在
            if (beforeCreate != null) {
                List<Object> actualArgs = new ArrayList<>();
                // 通过方法对象获取其类对象，并通过注解的value钩子方法名获取其方法对象
                Method hookmethod = methodTarget.getClass().getMethod(beforeCreate.value());
                // 若注解args所需参数不为空
                if (beforeCreate.args().length != 0) {
                    // 获取带参的钩子方法对象
                    hookmethod = methodTarget.getClass().getMethod(beforeCreate.value(), methodArgs.getClass());
                    // 将注解中标注需要的对象名其数据存入实参列表中
                    for (String needArg : beforeCreate.args()) {
                        for (Object methodArg : methodArgs) {
                            if (methodArg.getClass().getSimpleName().equals(needArg)) {
                                actualArgs.add(methodArg);
                            }
                        }
                    }
                    // 调用带参钩子方法
                    hookmethod.invoke(methodTarget, actualArgs.toArray());
                }
                // 调用无参钩子方法
                hookmethod.invoke(methodTarget);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(Constant.HOOK_UNCHECK_EXCEPTION);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(Constant.HOOK_NOTFOUND_EXCEPTION);
        }
        return jp.proceed();
    }
}
