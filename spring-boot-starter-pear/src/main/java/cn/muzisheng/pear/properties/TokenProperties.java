package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "token")
public class TokenProperties {
    private String tokenSalt = Constant.TOKEN_DEFAULT_SECRET_SALT;
    private String tokenHead = Constant.TOKEN_DEFAULT_SECRET_PREFIX;
    private long tokenExpire = Constant.TOKEN_DEFAULT_EXPIRE_DAY;
}
