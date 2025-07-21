package cn.muzisheng.pear.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegisterUserForm {
    private String email;
    private String password;
    private String displayName;
    private String firstName;
    private String lastName;
    private String locale;
    private String timezone;
    private String source;
}
