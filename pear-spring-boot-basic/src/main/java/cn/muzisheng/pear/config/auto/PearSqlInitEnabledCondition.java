package cn.muzisheng.pear.config.auto;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 判断 Pear SQL 初始化是否应该注册可观察的自动配置 bean。
 **/
public class PearSqlInitEnabledCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return PearSqlInitEnvironmentPostProcessor.isSqlInitEnabled(context.getEnvironment());
    }
}
