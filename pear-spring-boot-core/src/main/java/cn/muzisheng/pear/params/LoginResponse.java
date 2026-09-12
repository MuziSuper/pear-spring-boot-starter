package cn.muzisheng.pear.params;

import cn.muzisheng.pear.entity.User;
import lombok.Data;

/**
 * 登录成功后的用户响应信息，包含前端继续认证所需的 JWT token。
 **/
@Data
public class LoginResponse {
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

    /**
     * 从 User 实体创建登录响应 DTO。
     *
     * @param user  当前登录用户
     * @param token JWT token
     * @return 登录响应 DTO
     **/
    public static LoginResponse from(User user, String token) {
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setDisplayName(user.getDisplayName());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setAvatar(user.getAvatar());
        response.setLocale(user.getLocale());
        response.setTimezone(user.getTimezone());
        response.setPhone(user.getPhone());
        response.setToken(token);
        return response;
    }
}
