package cn.muzisheng.pear.handler;
@FunctionalInterface
public interface BeforeDelete {
    Object execute(Object[] args);
}
