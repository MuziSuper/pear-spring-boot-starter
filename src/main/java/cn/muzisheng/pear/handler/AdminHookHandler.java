package cn.muzisheng.pear.handler;

import java.util.Map;

public interface AdminHookHandler {
    void BeforeCreateFunc(Class<?> clazz);
    void BeforeDeleteFunc(Class<?> clazz);
    void BeforeUpdateFunc(Class<?> clazz, Map<String, Object> map);
    Object BeforeRenderFunc(Class<?> clazz);
    Object BeforeQueryRenderFunc(Class<?> clazz);
}
