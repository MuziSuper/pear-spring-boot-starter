package cn.muzisheng.pear.handler;

import org.springframework.stereotype.Component;

@Component
public interface AdminHookHandler {
    void beforeCreate();
    void beforeUpdate();
    void beforeDelete();
    void beforeRender();
    void accessCheck();
    void AdminViewOnSite();
    void beforeCreate(Object[] args);
    void beforeUpdate(Object[] args);
    void beforeDelete(Object[] args);
    void beforeRender(Object[] args);
    void accessCheck(Object[] args);
    void AdminViewOnSite(Object[] args);
}
