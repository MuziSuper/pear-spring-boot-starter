package cn.muzisheng.pear.handler;

import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.model.AdminObject;
import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface AccessCheck {
   /**
    * 用户级校验，若系统级校验开启会在其后验证
    * @param request 请求
    * @param adminObject 通用类对象
    * @param user 当前用户
    * @return 校验结果
    * @throws Exception 抛出任意异常都会被捕获并返回false拦截调用
    **/
   boolean execute(HttpServletRequest request, AdminObject adminObject, User user) throws Exception;
}
