package cn.muzisheng.pear.handler;
@FunctionalInterface
public interface BeforeUpdate {
    Object execute(Object[] args);
}
