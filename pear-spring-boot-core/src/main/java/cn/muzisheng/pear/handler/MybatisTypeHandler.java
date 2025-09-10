package cn.muzisheng.pear.handler;

import cn.muzisheng.pear.model.RoleEnum;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * RoleEnum类型处理器,将枚举类转为int类型
 **/
@Configuration
public class MybatisTypeHandler {

    @Bean
    public ConfigurationCustomizer typeHandlerCustomizer() {
        return configuration -> {
            configuration.getTypeHandlerRegistry().register(RoleEnum.class, RoleEnumTypeHandler.class);
        };
    }
}
