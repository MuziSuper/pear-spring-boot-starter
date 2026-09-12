package cn.muzisheng.pear.interceptor;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.JwtUtil;
import cn.muzisheng.pear.config.TokenConfig;
import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.AuthorizationException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JWTInterceptorTest {
    private static final String EMAIL = "alice@example.com";
    private static final String STRONG_SECRET = "01234567890123456789012345678901";

    @Test
    void validAuthorizationTokenLoadsUserIntoRequestWithoutCreatingSession() {
        JwtUtil jwtUtil = jwtUtil();
        User user = user(7L, EMAIL);
        FakeUserDAO userDAO = new FakeUserDAO();
        userDAO.userByEmail = user;
        JWTInterceptor interceptor = new JWTInterceptor(jwtUtil, userDAO);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", jwtUtil.generateTokenWithEmail(EMAIL));

        boolean result = interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertTrue(result);
        assertSame(user, request.getAttribute(Constant.REQUEST_CURRENT_USER));
        assertNull(request.getSession(false));
        assertEquals(EMAIL, userDAO.requestedEmail);
    }

    @Test
    void missingTokenIsRejected() {
        JWTInterceptor interceptor = new JWTInterceptor(jwtUtil(), new FakeUserDAO());

        assertThrows(
                AuthorizationException.class,
                () -> interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), new Object())
        );
    }

    @Test
    void tokenForMissingUserIsRejected() {
        JwtUtil jwtUtil = jwtUtil();
        FakeUserDAO userDAO = new FakeUserDAO();
        JWTInterceptor interceptor = new JWTInterceptor(jwtUtil, userDAO);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", jwtUtil.generateTokenWithEmail(EMAIL));

        assertThrows(
                AuthorizationException.class,
                () -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object())
        );
    }

    private static JwtUtil jwtUtil() {
        return new JwtUtil(TokenConfig.builder()
                .secret(STRONG_SECRET)
                .expire(60_000L)
                .issue("pear-test")
                .subject("authentication-test")
                .build());
    }

    private static User user(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }

    private static class FakeUserDAO extends UserDAO {
        private String requestedEmail;
        private User userByEmail;

        private FakeUserDAO() {
            super(null, null);
        }

        @Override
        public User getUserByEmail(String email) {
            requestedEmail = email;
            return userByEmail;
        }
    }
}
