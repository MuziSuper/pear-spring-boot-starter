package cn.muzisheng.pear.service;

import cn.muzisheng.pear.entity.User;

public interface UserService {
    public String setPassword(User user, String password);

    User getUserByEmail(String email);

    User createUserByEmail(String email, String password);

    boolean save(User user);
}
