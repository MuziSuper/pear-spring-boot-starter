package cn.muzisheng.pear.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginForm {
    private String email;
    private String password;
    private String timezone;
    private boolean remember;
    private String authToken;
}
