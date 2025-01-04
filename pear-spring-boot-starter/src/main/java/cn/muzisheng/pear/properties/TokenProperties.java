package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "app.token")
public class TokenProperties {
    private String tokenSalt;
    private String tokenHead;
    private long tokenExpire;
}
