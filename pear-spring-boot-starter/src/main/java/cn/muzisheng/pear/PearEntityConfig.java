package cn.muzisheng.pear;

import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Registers JPA entities provided by Pear without changing the application's own entity scan.
 */
@EntityScan(basePackageClasses = PearAutoConfig.class)
public class PearEntityConfig {
}
