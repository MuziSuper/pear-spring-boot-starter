package cn.muzisheng.pear;

import org.mybatis.spring.annotation.MapperScan;

/**
 * Registers MyBatis mappers implemented by the Pear framework.
 */
@MapperScan("cn.muzisheng.pear.mapper")
public class PearMapperConfig {
}
