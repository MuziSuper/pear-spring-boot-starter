package cn.muzisheng.pear.aspect;

import cn.muzisheng.pear.service.LogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ModelAspect {
    @Autowired
    private LogService logger;
    @Pointcut("@annotation(cn.muzisheng.pear.annotation.BackgroundApi)")
    public void modelPoint() {
    }
    @Around("modelPoint()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        Object target = jp.getTarget();
        String className=target.getClass().getSimpleName();
        System.out.println(className);
        return jp.proceed();
    }
}
