package cn.muzisheng.pear.config.auto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 数据库条件化装配类，当mysql作为数据库时，会自动运行resources内的sql脚本
 **/
@Configuration
public class SqlAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(SqlAutoConfiguration.class);

    /**
     * SQL 初始化属性由 EnvironmentPostProcessor 提前写入，这里只保留可观察的装配点。
     **/
    @Bean
    @Conditional(PearSqlInitEnabledCondition.class)
    @ConditionalOnMissingBean(name = "starterSqlInitProps")
    public CommandLineRunner starterSqlInitProps(Environment environment) {
        LOG.info("starterSqlInitProps默认注册完成, schema-locations={}", environment.getProperty("spring.sql.init.schema-locations"));
        return args -> {};
    }
}
