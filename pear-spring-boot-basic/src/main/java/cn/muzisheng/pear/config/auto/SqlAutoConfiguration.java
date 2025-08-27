package cn.muzisheng.pear.config.auto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据库条件化装配类，当mysql作为数据库时，会自动运行
 **/
@Configuration
@ConditionalOnProperty(
        prefix = "spring.datasource",
        name = "url",
        havingValue = "mysql"
)
public class SqlAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(SqlAutoConfiguration.class);
    @Bean
    @ConditionalOnMissingBean(name = "starterSqlInitProps")
    public CommandLineRunner starterSqlInitProps() {
        LOG.info("starterSqlInitProps默认注册完成");
        System.setProperty("spring.sql.init.mode", "always");
        System.setProperty("spring.sql.init.schema-locations", "classpath:schema-mysql.sql");
        return args -> {};
    }
}
