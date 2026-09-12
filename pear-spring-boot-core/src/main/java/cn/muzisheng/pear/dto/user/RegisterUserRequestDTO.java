package cn.muzisheng.pear.dto.user;

import cn.muzisheng.pear.dto.BaseDTO;
import cn.muzisheng.pear.params.RegisterUserForm;
import lombok.Data;

@Data
@lombok.EqualsAndHashCode(callSuper = false)
/** HTTP input for creating a user account. Password is accepted here but never exposed by a VO. */
public class RegisterUserRequestDTO extends BaseDTO {
    private String email;
    private String password;
    private String displayName;
    private String firstName;
    private String lastName;
    private String locale;
    private String timezone;
    private String source;

    /** Temporary adapter used until the internal user service completes its DTO migration. */
    public RegisterUserForm toLegacyForm() {
        return new RegisterUserForm(email, password, displayName, firstName, lastName, locale, timezone, source);
    }
}
