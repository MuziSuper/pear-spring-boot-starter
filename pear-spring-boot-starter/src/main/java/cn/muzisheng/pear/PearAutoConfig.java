package cn.muzisheng.pear;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@Import({
        PearBeanConfig.class,
        PearMapperConfig.class
})
public class PearAutoConfig {
}
