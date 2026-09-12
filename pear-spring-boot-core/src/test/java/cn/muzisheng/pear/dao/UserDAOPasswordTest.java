package cn.muzisheng.pear.dao;

import cn.muzisheng.pear.config.UserConfig;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.mapper.UserMapper;
import cn.muzisheng.pear.service.PasswordService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserDAOPasswordTest {

    @Test
    void hashPasswordUsesVersionedBcryptAndNotLegacySha256() {
        UserDAO userDAO = userDAO(mock(UserMapper.class));

        String hash = userDAO.HashPassword("secret-password");

        assertTrue(hash.startsWith("{bcrypt}"));
        assertFalse(userDAO.getPasswordService().isLegacySha256Hash(hash));
        assertTrue(userDAO.getPasswordService().matches("secret-password", hash));
    }

    @Test
    void createUserStoresBcryptHashAndRejectsBlankPassword() {
        UserMapper userMapper = mock(UserMapper.class);
        when(userMapper.insert(any(User.class))).thenReturn(1);
        when(userMapper.selectOne(any())).thenReturn(new User("alice@example.com", "stored"));
        UserDAO userDAO = userDAO(userMapper);

        userDAO.createUser("alice@example.com", "secret-password");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(captor.capture());
        assertTrue(captor.getValue().getPassword().startsWith("{bcrypt}"));
        assertTrue(userDAO.getPasswordService().matches("secret-password", captor.getValue().getPassword()));
        assertThrows(IllegalException.class, () -> userDAO.createUser("blank@example.com", " "));
    }

    @Test
    void setPasswordRejectsBlankPassword() {
        UserDAO userDAO = userDAO(mock(UserMapper.class));

        assertThrows(IllegalException.class, () -> userDAO.setPassword(new User(), ""));
        assertThrows(IllegalException.class, () -> userDAO.HashPassword(null));
    }

    private static UserDAO userDAO(UserMapper userMapper) {
        UserConfig userConfig = new UserConfig.Builder()
                .passwordSalt("legacy-test-salt")
                .bcryptStrength(4)
                .build();
        PasswordService passwordService = new PasswordService(userConfig);
        return new UserDAO(userMapper, userConfig, passwordService);
    }
}
