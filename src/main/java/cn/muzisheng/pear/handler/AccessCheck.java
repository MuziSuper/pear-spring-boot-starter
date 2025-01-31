package cn.muzisheng.pear.handler;
@FunctionalInterface
public interface AccessCheck {
    Object execute(Object[] args);
}
