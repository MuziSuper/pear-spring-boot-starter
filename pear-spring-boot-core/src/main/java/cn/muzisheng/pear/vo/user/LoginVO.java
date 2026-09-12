package cn.muzisheng.pear.vo.user;

import cn.muzisheng.pear.params.LoginResponse;
import cn.muzisheng.pear.vo.BaseVO;
import lombok.Data;

@Data
@lombok.EqualsAndHashCode(callSuper = false)
public class LoginVO extends BaseVO {
    private Long id;
    private String email;
    private String displayName;
    private String firstName;
    private String lastName;
    private String avatar;
    private String locale;
    private String timezone;
    private String phone;
    private String token;

    public static LoginVO from(LoginResponse source) {
        LoginVO target = new LoginVO();
        target.setId(source.getId());
        target.setEmail(source.getEmail());
        target.setDisplayName(source.getDisplayName());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setAvatar(source.getAvatar());
        target.setLocale(source.getLocale());
        target.setTimezone(source.getTimezone());
        target.setPhone(source.getPhone());
        target.setToken(source.getToken());
        return target;
    }
}
