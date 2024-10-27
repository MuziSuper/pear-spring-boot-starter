package cn.muzisheng.pear.service.impl;

import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.mapper.UserMapper;
import cn.muzisheng.pear.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public String setPassword(User user, String password) {
        String newPassword = HashPassword(password);

    }

    private String HashPassword(String password) {
        if("".equals(password)){
            return "";
        }
    }
}
