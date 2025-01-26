package cn.muzisheng.pear.handler;

public interface AdminExtraHandler {
    // 后端处理函数
    Object handler(Class<?> clazz);
    // 访问权限检测函数
    String AdminViewOnSite(Class<?> clazz);
    void AdminAccessCheck(Class<?> clazz);
}
