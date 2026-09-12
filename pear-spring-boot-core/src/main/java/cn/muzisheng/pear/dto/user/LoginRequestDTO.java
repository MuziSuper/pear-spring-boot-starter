package cn.muzisheng.pear.dto.user;

import cn.muzisheng.pear.dto.BaseDTO;
import cn.muzisheng.pear.params.LoginForm;
import lombok.Data;

@Data
@lombok.EqualsAndHashCode(callSuper = false)
/** HTTP input for password or token based login. */
public class LoginRequestDTO extends BaseDTO {
    private String email;
    private String password;
    private String timezone;
    private boolean remember;
    private String authToken;

    /** Temporary adapter used until the internal user service completes its DTO migration. */
    public LoginForm toLegacyForm() {
        return new LoginForm(email, password, timezone, remember, authToken);
    }
}
