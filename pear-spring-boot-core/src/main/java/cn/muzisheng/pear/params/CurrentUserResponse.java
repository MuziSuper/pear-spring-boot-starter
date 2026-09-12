package cn.muzisheng.pear.params;

import cn.muzisheng.pear.entity.User;
import lombok.Data;

/**
 * 当前用户公开信息响应，不包含密码、token 和审计/内部字段。
 **/
@Data
public class CurrentUserResponse {
    private Long id;
    private String email;
    private String displayName;
    private String firstName;
    private String lastName;
    private String avatar;
    private String locale;
    private String timezone;
    private String phone;

    /**
     * 从 User 实体创建当前用户响应 DTO。
     *
     * @param user 当前用户
     * @return 当前用户公开信息 DTO
     **/
    public static CurrentUserResponse from(User user) {
        CurrentUserResponse response = new CurrentUserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setDisplayName(user.getDisplayName());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setAvatar(user.getAvatar());
        response.setLocale(user.getLocale());
        response.setTimezone(user.getTimezone());
        response.setPhone(user.getPhone());
        return response;
    }
}
