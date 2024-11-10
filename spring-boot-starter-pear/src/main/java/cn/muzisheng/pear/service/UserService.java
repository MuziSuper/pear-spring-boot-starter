package cn.muzisheng.pear.service;

import cn.muzisheng.pear.params.RegisterUserForm;
import cn.muzisheng.pear.utils.Response;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface UserService {
    /**
     * 注册用户
     **/
    Response<Map<String, Object>> register(HttpServletRequest request, RegisterUserForm registerUserForm);

}
