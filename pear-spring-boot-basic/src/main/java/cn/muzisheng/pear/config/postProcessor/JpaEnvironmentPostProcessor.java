package cn.muzisheng.pear.config.postProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
//@Component
public class JpaEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(JpaEnvironmentPostProcessor.class);
    public JpaEnvironmentPostProcessor() {
        LOG.info("JpaEnvironmentPostProcessor注册完成");
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Boolean enable = environment.getProperty("pear.starter.jpa.ddl-auto", Boolean.class, false);
        Boolean showSql = environment.getProperty("pear.starter.jpa.show-sql", Boolean.class, false);
        String dialect = environment.getProperty("pear.starter.jpa.dialect", String.class, "org.hibernate.dialect.MySQLDialect");
        LOG.info("jpa enable: {}", enable);
        if (enable) {
            Map<String, Object> map = new HashMap<>();
            // 这里定制 ddl-auto 的值，比如 "update"
                map.put("spring.jpa.hibernate.ddl-auto", "update");
                map.put("spring.jpa.properties.hibernate.dialect", dialect);
                if(showSql) map.put("spring.jpa.show-sql", showSql);

                LOG.info(map.toString());
            environment.getPropertySources().addLast(new MapPropertySource("jpa", map));
        }
    }

    }
