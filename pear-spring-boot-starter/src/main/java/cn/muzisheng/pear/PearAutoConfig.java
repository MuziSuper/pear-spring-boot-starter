package cn.muzisheng.pear;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Pear 的聚合配置入口。
 *
 * <p>该类仅由 {@link PearApplication} 导入，不通过 Spring Boot 自动装配文件注册。
 * 启用后会加载 Pear 的组件、实体和 Mapper 配置，使框架提供的后台接口、服务和持久化对象
 * 进入应用上下文。</p>
 *
 * <p>使用 {@code proxyBeanMethods = false} 是因为本配置只负责导入其他配置类，
 * 不需要通过配置类方法之间的调用维持单例语义。</p>
 */
@Configuration(proxyBeanMethods = false)
@Import({
        PearBeanConfig.class,
        PearMapperConfig.class,
        PearEntityConfig.class
})
public class PearAutoConfig {
}
