package cn.muzisheng.pear.handler;
@FunctionalInterface
public interface BeforeRender {
    Object execute(Object[] args);
}
