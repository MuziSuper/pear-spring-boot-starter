package cn.muzisheng.pear.service;

import cn.muzisheng.pear.params.RegisterUserForm;
import cn.muzisheng.pear.utils.Response;

import java.util.Map;

public interface UserService {
    /**
     * 注册用户
     **/
    Response<Map<String, String>> register(RegisterUserForm registerUserForm);

}
