package cn.muzisheng.pear.handler;

import org.springframework.stereotype.Component;


@FunctionalInterface
public interface BeforeCreate {
    Object execute(Object[] args);

}
