package cn.muzisheng.pear.config.auto;

import cn.muzisheng.pear.properties.JpaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(prefix = "starter.db", name = "auto-create", havingValue = "true")
@EnableConfigurationProperties(JpaProperties.class)
@ConditionalOnClass(DataSource.class) // 只有有 DataSource 才生效
@ConditionalOnBean(DataSource.class)  // 保证数据源已经创建
@EntityScan(basePackages = "cn.muzisheng.pear") // 扫描 starter 内的实体类
public class JpaAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(JpaAutoConfiguration.class);

    public JpaAutoConfiguration(){
        LOG.info("JPA默认注册完成");
}
}