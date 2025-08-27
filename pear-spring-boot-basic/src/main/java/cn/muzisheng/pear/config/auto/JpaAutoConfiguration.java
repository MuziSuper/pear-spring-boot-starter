package cn.muzisheng.pear.config.auto;

import cn.muzisheng.pear.conditional.OnJPAAutoCreateCondition;
import cn.muzisheng.pear.properties.JpaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.sql.DataSource;
/**
 * JPA条件化装配类，当starter.db.auto-create=true（默认）且存在DataSource时，自动扫描该包下的实体类JPA注解进行自动建表
 **/
@Configuration
@Conditional(OnJPAAutoCreateCondition.class)
@EnableConfigurationProperties(JpaProperties.class)
@ConditionalOnBean(DataSource.class)  // 保证数据源已经创建
@EntityScan(basePackages = "cn.muzisheng.pear") // 扫描 starter 内的实体类
public class JpaAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(JpaAutoConfiguration.class);

    public JpaAutoConfiguration(){
        LOG.info("JPA默认注册完成");
    }
}