package cn.muzisheng.pear;

import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Pear JPA 实体扫描配置。
 *
 * <p>注册框架内置的用户、角色、权限和配置等实体，使其能够被应用的 JPA 持久化单元识别。
 * 扫描锚点限定在 Pear 包内，因此不会扩展或覆盖业务项目自定义的实体扫描范围。</p>
 *
 * <p>业务实体仍应由业务启动类或独立配置类通过 {@code @EntityScan} 管理。</p>
 */
@EntityScan(basePackageClasses = PearAutoConfig.class)
public class PearEntityConfig {
}
