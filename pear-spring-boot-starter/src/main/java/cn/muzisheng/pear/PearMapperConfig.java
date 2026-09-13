package cn.muzisheng.pear;

import org.mybatis.spring.annotation.MapperScan;

/**
 * Pear MyBatis Mapper 扫描配置。
 *
 * <p>扫描 {@code cn.muzisheng.pear.mapper} 下由框架实现的 Mapper，
 * 以支持 Pear 内置实体的数据库访问。该配置不会自动扫描业务项目的 Mapper 包，
 * 业务方应继续使用自己的 {@code @MapperScan} 配置管理其数据访问层。</p>
 */
@MapperScan("cn.muzisheng.pear.mapper")
public class PearMapperConfig {
}
