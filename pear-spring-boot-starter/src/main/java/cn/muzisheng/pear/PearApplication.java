package cn.muzisheng.pear;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Pear 框架的显式启用注解。
 *
 * <p>将该注解标记在 Spring Boot 启动类或其他配置类上，才会导入 Pear 的组件扫描、
 * JPA 实体扫描和 MyBatis Mapper 扫描配置。仅引入 starter 依赖不会自动启用这些能力，
 * 从而避免框架在未确认的情况下改变业务应用的 Bean 注册和 Web 接口。</p>
 *
 * <p>业务项目自己的组件、实体和 Mapper 仍应按其既有 Spring Boot 配置方式注册。</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ImportAutoConfiguration(PearAutoConfig.class)
public @interface PearApplication {
}
