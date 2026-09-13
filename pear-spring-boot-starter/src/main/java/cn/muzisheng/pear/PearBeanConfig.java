package cn.muzisheng.pear;

import org.springframework.context.annotation.ComponentScan;

/**
 * Pear 组件扫描配置。
 *
 * <p>以 {@link PearAutoConfig} 所在包为根路径扫描 Pear 自身的 Spring 组件，
 * 包括 Service、Controller、拦截器相关 Bean 和框架内部配置类。使用类型作为扫描锚点，
 * 可避免包名调整后字符串配置失效。</p>
 *
 * <p>该扫描范围只面向 Pear 包，不替代业务项目自身的 {@code @ComponentScan} 配置。</p>
 */
@ComponentScan(basePackageClasses = PearAutoConfig.class)
public class PearBeanConfig {
}
