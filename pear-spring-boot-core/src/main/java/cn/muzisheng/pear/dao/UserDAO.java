package cn.muzisheng.pear.dao;

import cn.muzisheng.pear.config.UserConfig;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.mapper.UserMapper;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.service.PasswordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
@Data
@Service
public class UserDAO {

    private final UserMapper userMapper;

    private final UserConfig userConfig;
    private final PasswordService passwordService;

    @Autowired
    public UserDAO(UserMapper userMapper, UserConfig userConfig, PasswordService passwordService){
        this.userMapper = userMapper;
        this.userConfig=userConfig;
        this.passwordService = passwordService;
    }

    public UserDAO(UserMapper userMapper, UserConfig userConfig){
        this(userMapper, userConfig, new PasswordService(userConfig));
    }

    /**
     * 更新最后登录时间与ip
     **/
    public boolean setLastLogin(User user, String ip){
        user.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        user.setLastLoginIp(ip);
        return userMapper.updateById(user)>0;
    }

    /**
     * 根据对象主键更新用户数据
     **/
    public boolean updateUserById(User user){
        return userMapper.updateById(user)>0;
    }

        /**
         * 根据map集合更新用户数据
         **/
    public boolean updateUserFields(User user, Map<String, Object> fields){
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        // 遍历map，设置要更新的字段
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            updateWrapper.set(fieldName, fieldValue);
        }
        // 执行更新操作
        int rowsAffected = userMapper.update(user, updateWrapper);

        return rowsAffected > 0;
    }

    /**
     * 设置加密密码
     **/

    public boolean setPassword(User user, String password) {
        validatePassword(password);
        String newPassword = HashPassword(password);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        // 设置新的密码
        updateWrapper.set("password", newPassword);

        // 执行更新操作
        int rowsAffected = userMapper.update(user, updateWrapper);

        return rowsAffected > 0;
    }
    /**
     * 根据邮箱获取用户
     **/
    public User getUserByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 根据邮箱创建用户
     **/
    public User createUser(String email, String password) {
        validatePassword(password);
        User user = new User(email, HashPassword(password));
        user.setActivated(false);
        user.setEnabled(true);
        if(userMapper.insert(user)>0){
            return getUserByEmail(email);
        }else {
            return null;
        }
    }
    /**
     * 根据主键更新用户
     **/
    public boolean save(User user){
        int rowsAffected = userMapper.updateById(user);
        return rowsAffected > 0;
    }
    /**
     * 根据邮箱判断用户是否存在
     **/
    public boolean isExistsByEmail(String email) {
        return getUserByEmail(email)!=null;
    }

    /**
     * 密码加密。保留方法名兼容旧调用，新密码统一使用带版本前缀的 BCrypt。
     **/
    public String HashPassword(String password) {
        return passwordService.encode(password);
    }

    /**
     * 旧 SHA-256(passwordSalt + password) 哈希，仅用于历史用户登录兼容。
     **/
    public String legacyHashPassword(String password) {
        return passwordService.legacyHash(password);
    }
    public User getUserById(long userId){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enabled", true).eq("id", userId);
        return userMapper.selectOne(queryWrapper);
    }


    /**
     * 解密hashToken
     **/
    public User decodeHashToken(String hashPassword,boolean useLastLogin){
        return null;
    }
    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalException("Password cannot be empty.");
        }
    }
}
