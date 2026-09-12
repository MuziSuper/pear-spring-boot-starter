package cn.muzisheng.pear.service;

import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.params.CurrentUserResponse;
import cn.muzisheng.pear.params.LoginForm;
import cn.muzisheng.pear.params.LoginResponse;
import cn.muzisheng.pear.params.RegisterUserForm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * 用户服务接口。
 **/
public interface UserService {
    /**
     * 注册用户
     **/
    ResponseEntity<Result<Map<String, Object>>> register(HttpServletRequest request, RegisterUserForm registerUserForm);

    /**
     * 登陆用户
     **/
    ResponseEntity<Result<LoginResponse>> login(HttpServletRequest request, LoginForm loginForm);

    /**
     * 获取用户信息
     **/
    ResponseEntity<Result<CurrentUserResponse>> userInfo(HttpServletRequest request, String token);
    /**
     * 获取当前用户。读取顺序为 request 当前用户、旧 request 用户缓存、session 用户 id fallback。
     **/
    User currentUser(HttpServletRequest request);

}
