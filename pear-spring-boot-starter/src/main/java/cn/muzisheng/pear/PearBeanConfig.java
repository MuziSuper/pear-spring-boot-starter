package cn.muzisheng.pear;

import org.springframework.context.annotation.ComponentScan;

/**
 * Registers Pear's component-scanned services, controllers, and configuration classes.
 */
@ComponentScan(basePackageClasses = PearAutoConfig.class)
public class PearBeanConfig {
}
