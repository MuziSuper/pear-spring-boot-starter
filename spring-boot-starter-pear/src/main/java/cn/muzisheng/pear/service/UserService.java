package cn.muzisheng.pear.service;

import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.params.RegisterUserForm;
import cn.muzisheng.pear.utils.Result;

public interface UserService {
    /**
     * 注册用户
     **/
    Result<Object> register(RegisterUserForm registerUserForm);

}
