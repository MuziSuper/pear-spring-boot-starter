package cn.muzisheng.pear.api;

import cn.muzisheng.pear.core.user.UserService;
import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.params.LoginForm;
import cn.muzisheng.pear.params.RegisterUserForm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserApi {
    @Autowired
    private UserService userService;
    @PostMapping("register")
    public ResponseEntity<Result<Map<String, Object>>> register(HttpServletRequest request, @RequestBody RegisterUserForm registerUserForm) {
        return userService.register(request, registerUserForm);
    }
    @PostMapping("login")
    public ResponseEntity<Result<User>> login(HttpServletRequest request, @RequestBody LoginForm loginForm) {
        return userService.login(request, loginForm);
    }
    @PostMapping("info")
    public ResponseEntity<Result<User>> info(HttpServletRequest request, @RequestParam(name = "with_token", required = false )String token) {
        return userService.userInfo(request ,token);
    }
}
