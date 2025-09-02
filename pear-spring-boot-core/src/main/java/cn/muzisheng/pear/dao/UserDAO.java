package cn.muzisheng.pear.dao;

import cn.muzisheng.pear.config.UserConfig;
import cn.muzisheng.pear.mapper.UserMapper;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.ScaleException;
import cn.muzisheng.pear.model.RoleEnum;
import cn.muzisheng.pear.properties.UserProperties;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.Data;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
@Data
@Service
public class UserDAO {

    private final UserMapper userMapper;

    private final UserConfig userConfig;

    @Autowired
    public UserDAO(UserMapper userMapper, UserConfig userConfig){
        this.userMapper = userMapper;
        this.userConfig=userConfig;
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
     * 密码加密
     **/
    public String HashPassword(String password) {
        if("".equals(password)){
            return "";
        }
        try {
            MessageDigest md5=MessageDigest.getInstance("SHA-256");
            byte[] digest=md5.digest((userConfig.getPasswordSalt() + password).getBytes());
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new ScaleException("The password encryption algorithm is not supported",e);
        }
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
    /**
     * 将字节数组转换为十六进制字符串
     **/
    public static String bytesToHex(byte[] bytes) {
        try {
            return Hex.encodeHexString(bytes);
        } catch (Exception e) {
            throw new ScaleException("Bytes cannot be converted to hexadecimal strings, password encryption failure", e);
        }
    }
    /**
     * 将十六进制字符串转换为字节数组
     **/
    public static byte[] hexToBytes(String hexString) {
        try {
            return Hex.decodeHex(hexString.toCharArray());
        } catch (Exception e) {
            throw new ScaleException("Hexadecimal strings cannot be converted to bytes, password decode failure", e);
        }
    }
}
