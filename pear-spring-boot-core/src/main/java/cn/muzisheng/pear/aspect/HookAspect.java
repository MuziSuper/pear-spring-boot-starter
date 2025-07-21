package cn.muzisheng.pear.aspect;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.annotation.*;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.AuthorizationException;
import cn.muzisheng.pear.exception.ForbiddenException;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.model.AdminObject;
import cn.muzisheng.pear.service.ConfigService;
import cn.muzisheng.pear.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class HookAspect {
    private final UserService userService;
    private final ConfigService configService;
    private static final Logger LOG = LoggerFactory.getLogger(HookAspect.class);
    @Autowired
    public HookAspect(UserService userService, ConfigService configService) {
        this.userService = userService;
        this.configService = configService;
    }
    // 定义切点：所有带有@Verification注解的方法
    @Pointcut("@annotation(cn.muzisheng.pear.annotation.Verification)")
    public void verificationPointcut() {}
    @Around("verificationPointcut()")
    public Object verificationFunc(ProceedingJoinPoint jp) throws Throwable{
        // 获取方法签名
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        // 通过方法签名获取方法其上的注解
        Verification verification = method.getAnnotation(Verification.class);
        Object[] methodArgs = jp.getArgs();
        HttpServletRequest request = null;
        AdminObject adminObject = null;
        for(Object arg:methodArgs){
            if(arg instanceof HttpServletRequest){
                request= (HttpServletRequest) arg;
            }
            if(arg instanceof AdminObject){
                adminObject= (AdminObject) arg;
            }
            if(request!=null&&adminObject!=null){
                break;
            }
        }
        if(request==null){
            throw new IllegalException("Request not found.");
        }
        if(verification.SystemVerify()){
            withAdminAuth(request);
        }
        if(verification.UserVerify()&&adminObject!=null){
            accessCheck(adminObject,request);
        }
        return jp.proceed();
    }
    /**
     * 用户级校验身份，校验不通过则抛出异常
     * @param adminObject 通用类对象
     * @param request     请求
     * @throws AuthorizationException 身份校验不通过
     **/
    private void accessCheck(AdminObject adminObject, HttpServletRequest request){
        if(adminObject.getAccessCheck()!=null){
            try {
                adminObject.getAccessCheck().execute(request,adminObject);
            } catch (Exception e) {
                LOG.error("accessCheck error", e);
                throw new AuthorizationException("accessCheck error");
            }
        }
    }
    /**
     * 系统级校验用户是否登录，未登录则抛出异常
     * @param request 请求
     **/
    private void withAdminAuth(HttpServletRequest request){
        User user=userService.currentUser(request);
        if(user==null){
            String signUrl=configService.getValue(Constant.KEY_SITE_SIGNIN_URL);
            if(signUrl==null){
                throw new AuthorizationException("unauthorized");
            }else{
                throw new AuthorizationException("unauthorized;signUrl="+signUrl);
            }
        }else if(user.getIsStaff()!=null && user.getIsSuperUser()!=null && !user.getIsStaff() && !user.getIsSuperUser()) {
            throw new ForbiddenException("forbidden");
        }
    }

}
