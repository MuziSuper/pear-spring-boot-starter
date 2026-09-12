package cn.muzisheng.pear.service.Impl;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.Context;
import cn.muzisheng.pear.JwtUtil;
import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.params.CurrentUserResponse;
import cn.muzisheng.pear.params.LoginForm;
import cn.muzisheng.pear.params.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class UserServiceResponseTest {

    @Test
    void loginWithEmailAndPasswordReturnsLoginResponseAndDoesNotWriteEntityToken() {
        User user = user(21L, "login@example.com");
        user.setPassword("hashed-raw-password");
        FakeUserDAO userDAO = new FakeUserDAO();
        userDAO.userByEmail = user;
        userDAO.hashedPassword = "hashed-raw-password";
        FakeJwtUtil jwtUtil = new FakeJwtUtil("issued-token");
        UserServiceImpl userService = new UserServiceImpl(userDAO, jwtUtil);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.5");

        ResponseEntity<Result<LoginResponse>> response = userService.login(request, new LoginForm("login@example.com", "raw-password", null, false, null));

        LoginResponse loginResponse = response.getBody().getData();
        assertNotNull(loginResponse);
        assertEquals(21L, loginResponse.getId());
        assertEquals("login@example.com", loginResponse.getEmail());
        assertEquals("Alice", loginResponse.getDisplayName());
        assertEquals("issued-token", loginResponse.getToken());
        assertNull(user.getToken());
        assertSame(user, userDAO.lastLoginUser);
        assertEquals("10.0.0.5", userDAO.lastLoginIp);
        assertEquals("login@example.com", jwtUtil.tokenEmail);
    }

    @Test
    void userInfoReturnsCurrentUserResponseWithoutMutatingSensitiveFields() {
        User requestUser = user(22L, "request@example.com");
        User persistedUser = user(22L, "persisted@example.com");
        persistedUser.setPassword("hashed-password");
        persistedUser.setToken("entity-token");
        FakeUserDAO userDAO = new FakeUserDAO();
        userDAO.userById = persistedUser;
        UserServiceImpl userService = new UserServiceImpl(userDAO, new FakeJwtUtil("unused"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        Context.setRequestScope(request, Constant.REQUEST_CURRENT_USER, requestUser);

        ResponseEntity<Result<CurrentUserResponse>> response = userService.userInfo(request, null);

        CurrentUserResponse currentUserResponse = response.getBody().getData();
        assertNotNull(currentUserResponse);
        assertEquals(22L, currentUserResponse.getId());
        assertEquals("persisted@example.com", currentUserResponse.getEmail());
        assertEquals("Alice", currentUserResponse.getDisplayName());
        assertEquals(22L, userDAO.requestedUserId);
        assertSame(requestUser, request.getAttribute(Constant.REQUEST_CURRENT_USER));
        assertEquals("entity-token", persistedUser.getToken());
        assertEquals("hashed-password", persistedUser.getPassword());
    }

    private static User user(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setDisplayName("Alice");
        user.setFirstName("A");
        user.setLastName("Liddell");
        user.setAvatar("/avatar.png");
        user.setLocale("en_US");
        user.setTimezone("UTC");
        user.setPhone("+1-555-0100");
        return user;
    }

    private static class FakeUserDAO extends UserDAO {
        private User userByEmail;
        private User userById;
        private String hashedPassword;
        private User lastLoginUser;
        private String lastLoginIp;
        private Long requestedUserId;

        private FakeUserDAO() {
            super(null, null);
        }

        @Override
        public User getUserByEmail(String email) {
            return userByEmail;
        }

        @Override
        public String HashPassword(String password) {
            return hashedPassword;
        }

        @Override
        public boolean setLastLogin(User user, String ip) {
            lastLoginUser = user;
            lastLoginIp = ip;
            return true;
        }

        @Override
        public User getUserById(long userId) {
            requestedUserId = userId;
            return userById;
        }
    }

    private static class FakeJwtUtil extends JwtUtil {
        private final String token;
        private String tokenEmail;

        private FakeJwtUtil(String token) {
            super(cn.muzisheng.pear.config.TokenConfig.builder()
                    .secret("01234567890123456789012345678901")
                    .build());
            this.token = token;
        }

        @Override
        public String generateTokenWithEmail(String email) {
            tokenEmail = email;
            return token;
        }
    }
}
