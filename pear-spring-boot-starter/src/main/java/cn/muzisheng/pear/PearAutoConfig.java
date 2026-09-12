package cn.muzisheng.pear;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Pear framework configuration imported only by {@link PearApplication}.
 *
 * <p>The starter deliberately does not register this class through Spring Boot's
 * auto-configuration mechanism. Applications opt in by adding {@code @PearApplication}
 * to their bootstrap configuration.</p>
 */
@Configuration(proxyBeanMethods = false)
@Import({
        PearBeanConfig.class,
        PearMapperConfig.class,
        PearEntityConfig.class
})

public class PearAutoConfig {
}
