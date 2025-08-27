package cn.muzisheng.pear.conditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;
/**
 * 自定义条件，根据CacheConfig中的enable属性判断是否注入
 **/
@Component
public class OnCacheEnabledCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        return environment.getProperty("pear.starter.cache.redis.enable", Boolean.class, false);
   }
}