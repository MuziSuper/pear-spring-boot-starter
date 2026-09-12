package cn.muzisheng.pear.service.Impl;

import cn.muzisheng.pear.JwtUtil;
import cn.muzisheng.pear.config.TokenConfig;
import cn.muzisheng.pear.config.UserConfig;
import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.params.LoginForm;
import cn.muzisheng.pear.params.LoginResponse;
import cn.muzisheng.pear.service.PasswordService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserPasswordMigrationTest {

    @Test
    void checkPasswordAcceptsBcryptAndRejectsWrongPassword() {
        FakeUserDAO userDAO = new FakeUserDAO();
        UserServiceImpl userService = new UserServiceImpl(userDAO, jwtUtil());
        User user = user("bcrypt@example.com");
        user.setPassword(userDAO.HashPassword("secret-password"));

        assertTrue(userService.checkPassword(user, "secret-password"));
        assertFalse(userService.checkPassword(user, "wrong-password"));
        assertNull(userDAO.updatedUser);
    }

    @Test
    void legacySha256PasswordMigratesAfterSuccessfulLoginOnly() {
        FakeUserDAO userDAO = new FakeUserDAO();
        UserServiceImpl userService = new UserServiceImpl(userDAO, jwtUtil());
        User user = user("legacy@example.com");
        user.setPassword(userDAO.legacyHashPassword("secret-password"));

        assertFalse(userService.checkPassword(user, "wrong-password"));
        assertTrue(userDAO.getPasswordService().isLegacySha256Hash(user.getPassword()));
        assertNull(userDAO.updatedUser);

        assertTrue(userService.checkPassword(user, "secret-password"));
        assertNotNull(userDAO.updatedUser);
        assertTrue(user.getPassword().startsWith("{bcrypt}"));
        assertTrue(userDAO.getPasswordService().matches("secret-password", user.getPassword()));
    }

    @Test
    void loginRejectsBlankPasswordAndStillReturnsLoginResponseWithToken() {
        FakeUserDAO userDAO = new FakeUserDAO();
        UserServiceImpl userService = new UserServiceImpl(userDAO, jwtUtil());
        User user = user("login@example.com");
        user.setPassword(userDAO.HashPassword("secret-password"));
        userDAO.userByEmail = user;

        assertThrows(IllegalException.class, () -> userService.login(new MockHttpServletRequest(), new LoginForm("login@example.com", " ", null, false, null)));

        ResponseEntity<Result<LoginResponse>> response = userService.login(
                new MockHttpServletRequest(),
                new LoginForm("login@example.com", "secret-password", null, false, null)
        );

        assertNotNull(response.getBody());
        assertEquals("login@example.com", response.getBody().getData().getEmail());
        assertTrue(response.getBody().getData().getToken().startsWith("Bearer "));
    }

    private static User user(String email) {
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setDisplayName("Test User");
        return user;
    }

    private static JwtUtil jwtUtil() {
        return new JwtUtil(TokenConfig.builder()
                .secret("01234567890123456789012345678901")
                .build());
    }

    private static class FakeUserDAO extends UserDAO {
        private User userByEmail;
        private User updatedUser;

        private FakeUserDAO() {
            this(userConfig());
        }

        private FakeUserDAO(UserConfig userConfig) {
            super(null, userConfig, new PasswordService(userConfig));
        }

        @Override
        public User getUserByEmail(String email) {
            return userByEmail;
        }

        @Override
        public boolean updateUserById(User user) {
            updatedUser = user;
            return true;
        }

        @Override
        public boolean setLastLogin(User user, String ip) {
            return true;
        }
    }

    private static UserConfig userConfig() {
        return new UserConfig.Builder()
                .passwordSalt("legacy-test-salt")
                .bcryptStrength(4)
                .build();
    }
}
