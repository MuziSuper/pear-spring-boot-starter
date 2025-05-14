package cn.muzisheng.pear.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackages = {"cn.muzisheng.pear"})
@MapperScan("cn.muzisheng.pear.mapper")
public class PearAutoConfig {
}
