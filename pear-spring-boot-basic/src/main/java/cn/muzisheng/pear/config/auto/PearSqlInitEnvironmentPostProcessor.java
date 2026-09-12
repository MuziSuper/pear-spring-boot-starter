package cn.muzisheng.pear.config.auto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 在 Spring SQL 初始化运行前写入 Pear 默认 schema 初始化配置。
 **/
public class PearSqlInitEnvironmentPostProcessor implements EnvironmentPostProcessor {
    public static final String SQL_INIT_ENABLED_PROPERTY = "pear.starter.sql.init.enabled";
    public static final String SQL_INIT_PROPERTY_SOURCE = "pearSqlInit";
    public static final String MYSQL_SCHEMA_LOCATION = "classpath:schema-mysql.sql";

    private static final Logger LOG = LoggerFactory.getLogger(PearSqlInitEnvironmentPostProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!isSqlInitEnabled(environment)) {
            return;
        }
        Map<String, Object> properties = new HashMap<>();
        putDefault(environment, properties, "spring.sql.init.mode", "always");
        putDefault(environment, properties, "spring.sql.init.schema-locations", MYSQL_SCHEMA_LOCATION);
        if (!properties.isEmpty()) {
            environment.getPropertySources().addLast(new MapPropertySource(SQL_INIT_PROPERTY_SOURCE, properties));
            LOG.info("Pear SQL initialization properties registered: {}", properties);
        }
    }

    /**
     * 未显式配置时仅在 MySQL JDBC URL 下启用；显式 true/false 拥有最高语义优先级。
     **/
    public static boolean isSqlInitEnabled(Environment environment) {
        Boolean configured = environment.getProperty(SQL_INIT_ENABLED_PROPERTY, Boolean.class);
        if (configured != null) {
            return configured;
        }
        return isMysqlJdbcUrl(environment.getProperty("spring.datasource.url"));
    }

    /**
     * 判断 datasource URL 是否为标准 MySQL JDBC URL。
     **/
    public static boolean isMysqlJdbcUrl(String datasourceUrl) {
        return datasourceUrl != null
                && datasourceUrl.trim().toLowerCase(Locale.ROOT).startsWith("jdbc:mysql:");
    }

    private void putDefault(Environment environment, Map<String, Object> properties, String key, String value) {
        if (!environment.containsProperty(key)) {
            properties.put(key, value);
        }
    }
}
