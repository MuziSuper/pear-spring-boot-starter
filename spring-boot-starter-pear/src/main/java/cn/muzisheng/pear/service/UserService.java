package cn.muzisheng.pear.service;

import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.params.RegisterUserForm;
import cn.muzisheng.pear.utils.Result;

public interface UserService {
    /**
     * 注册用户
     **/
    Result<Object> register(RegisterUserForm registerUserForm);



    /**
     * 设置加密密码
     **/
    boolean setPassword(User user, String password);
    /**
     * 根据邮箱获取用户
     **/
    User getUserByEmail(String email);

    /**
     * 根据邮箱创建用户
     **/
    User createUser(String email, String password);

    /**
     * 根据主键更新用户
     **/
    boolean save(User user);
    /**
     * 根据邮箱判断用户是否存在
     **/
    boolean isExistsByEmail(String email);

}
