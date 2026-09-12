package cn.muzisheng.pear.service.Impl;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class UserServiceCurrentUserTest {

    @Test
    void currentUserPrefersRequestCurrentUser() {
        FakeUserDAO userDAO = new FakeUserDAO();
        UserServiceImpl userService = new UserServiceImpl(userDAO, null);
        MockHttpServletRequest request = new MockHttpServletRequest();
        User requestUser = user(11L, "request@example.com");
        request.setAttribute(Constant.REQUEST_CURRENT_USER, requestUser);

        User currentUser = userService.currentUser(request);

        assertSame(requestUser, currentUser);
        assertSame(requestUser, request.getAttribute(Constant.REQUEST_CURRENT_USER));
        assertSame(requestUser, request.getAttribute(Constant.SESSION_USER_ID));
        assertNull(userDAO.requestedUserId);
    }

    @Test
    void currentUserSupportsLegacyRequestSessionUserId() {
        FakeUserDAO userDAO = new FakeUserDAO();
        UserServiceImpl userService = new UserServiceImpl(userDAO, null);
        MockHttpServletRequest request = new MockHttpServletRequest();
        User legacyUser = user(12L, "legacy@example.com");
        request.setAttribute(Constant.SESSION_USER_ID, legacyUser);

        User currentUser = userService.currentUser(request);

        assertSame(legacyUser, currentUser);
        assertSame(legacyUser, request.getAttribute(Constant.REQUEST_CURRENT_USER));
        assertSame(legacyUser, request.getAttribute(Constant.SESSION_USER_ID));
        assertNull(userDAO.requestedUserId);
    }

    @Test
    void currentUserFallsBackToSessionUserIdAndCachesUserInRequest() {
        FakeUserDAO userDAO = new FakeUserDAO();
        UserServiceImpl userService = new UserServiceImpl(userDAO, null);
        MockHttpServletRequest request = new MockHttpServletRequest();
        User sessionUser = user(13L, "session@example.com");
        request.getSession(true).setAttribute(Constant.SESSION_USER_ID, 13L);
        userDAO.userById = sessionUser;

        User currentUser = userService.currentUser(request);

        assertSame(sessionUser, currentUser);
        assertSame(sessionUser, request.getAttribute(Constant.REQUEST_CURRENT_USER));
        assertSame(sessionUser, request.getAttribute(Constant.SESSION_USER_ID));
        assertEquals(13L, userDAO.requestedUserId);
    }

    @Test
    void currentUserReturnsNullWithoutRequestOrSessionContext() {
        UserServiceImpl userService = new UserServiceImpl(new FakeUserDAO(), null);

        User currentUser = userService.currentUser(new MockHttpServletRequest());

        assertNull(currentUser);
    }

    private static User user(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }

    private static class FakeUserDAO extends UserDAO {
        private Long requestedUserId;
        private User userById;

        private FakeUserDAO() {
            super(null, null);
        }

        @Override
        public User getUserById(long userId) {
            requestedUserId = userId;
            return userById;
        }
    }
}
