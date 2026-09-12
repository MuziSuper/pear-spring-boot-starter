package cn.muzisheng.pear.vo.user;

import cn.muzisheng.pear.params.CurrentUserResponse;
import cn.muzisheng.pear.vo.BaseVO;
import lombok.Data;

@Data
@lombok.EqualsAndHashCode(callSuper = false)
public class CurrentUserVO extends BaseVO {
    private Long id;
    private String email;
    private String displayName;
    private String firstName;
    private String lastName;
    private String avatar;
    private String locale;
    private String timezone;
    private String phone;

    public static CurrentUserVO from(CurrentUserResponse source) {
        CurrentUserVO target = new CurrentUserVO();
        target.setId(source.getId());
        target.setEmail(source.getEmail());
        target.setDisplayName(source.getDisplayName());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setAvatar(source.getAvatar());
        target.setLocale(source.getLocale());
        target.setTimezone(source.getTimezone());
        target.setPhone(source.getPhone());
        return target;
    }
}
