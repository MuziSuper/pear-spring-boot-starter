package cn.muzisheng.pear.controller;

import cn.muzisheng.pear.params.RegisterUserForm;
import cn.muzisheng.pear.service.UserService;
import cn.muzisheng.pear.utils.Response;
import cn.muzisheng.pear.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("test")
    public Response<Map<String,Object>> register(HttpServletRequest request, @RequestBody RegisterUserForm registerUserForm) {
        return userService.register(request, registerUserForm);
    }
}
