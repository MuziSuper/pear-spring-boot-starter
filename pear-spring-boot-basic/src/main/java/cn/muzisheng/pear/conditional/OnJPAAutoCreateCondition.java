package cn.muzisheng.pear.conditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;


/**
 * 自定义条件，根据配置文件中的pear.starter.db.auto-create属性判断是否注入
 **/
@Component
public class OnJPAAutoCreateCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        return environment.getProperty("pear.starter.db.auto-create", Boolean.class, true);
    }
}