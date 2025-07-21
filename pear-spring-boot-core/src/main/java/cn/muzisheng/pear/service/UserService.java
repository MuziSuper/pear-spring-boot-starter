package cn.muzisheng.pear.service;

import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.params.LoginForm;
import cn.muzisheng.pear.params.RegisterUserForm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserService {
    /**
     * 注册用户
     **/
    ResponseEntity<Result<Map<String, Object>>> register(HttpServletRequest request, RegisterUserForm registerUserForm);

    /**
     * 登陆用户
     **/
    ResponseEntity<Result<User>> login(HttpServletRequest request, LoginForm loginForm);

    /**
     * 获取用户信息
     **/
    ResponseEntity<Result<User>> userInfo(HttpServletRequest request, String token);
    /**
     * 获取当前用户，并将用户信息存储到session中
     **/
    User currentUser(HttpServletRequest request);
}
