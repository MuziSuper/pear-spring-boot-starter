package cn.muzisheng.pear.service.impl;

import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.exception.ScaleException;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.mapper.UserMapper;
import cn.muzisheng.pear.params.RegisterUserForm;
import cn.muzisheng.pear.properties.UserProperties;
import cn.muzisheng.pear.service.UserService;
import cn.muzisheng.pear.utils.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserProperties userProperties;

    @Override
    public boolean setPassword(User user, String password) {
        String newPassword = HashPassword(password);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        // 设置新的密码
        updateWrapper.set("password", newPassword);

        // 执行更新操作
        int rowsAffected = userMapper.update(user, updateWrapper);

        return rowsAffected > 0;
    }
    @Override
    public User getUserByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User createUser(String email, String password) {
        User user = new User(email, HashPassword(password));
        user.setActivated(false);
        user.setEnabled(true);
        if(userMapper.insert(user)>0){
            return user;
        }else {
            return null;
        }
    }

    @Override
    public boolean save(User user){
        BaseMapper<User> baseMapper = userMapper;
        int rowsAffected =baseMapper.updateById(user);
        return rowsAffected > 0;
    }

    @Override
    public Result<Object> register(RegisterUserForm registerUserForm) {
        Result<Object> result = new Result<>();
        if(registerUserForm==null){
            throw new IllegalException();
        }
        if(isExistsByEmail(registerUserForm.getEmail())){
            result.setMessage("Email has exists");
            return result;
        }
        User user=createUser(registerUserForm.getEmail(),registerUserForm.getPassword());
    }

    @Override
    public boolean isExistsByEmail(String email) {
        return getUserByEmail(email)==null;
    }

    private String HashPassword(String password) {
        if("".equals(password)){
            return "";
        }
        try {
            MessageDigest md5=MessageDigest.getInstance("SHA-256");
            byte[] digest=md5.digest((userProperties.salt + password).getBytes());
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new ScaleException("The password encryption algorithm is not supported",e);
        }
    }
    public static String bytesToHex(byte[] bytes) {
        try {
            return Hex.encodeHexString(bytes);
        } catch (Exception e) {
            throw new ScaleException("Bytes cannot be converted to hexadecimal strings, password encryption failure", e);
        }
    }

    public static byte[] hexToBytes(String hexString) {
        try {
            return Hex.decodeHex(hexString.toCharArray());
        } catch (Exception e) {
            throw new ScaleException("Hexadecimal strings cannot be converted to bytes, password decode failure", e);
        }
    }
}
