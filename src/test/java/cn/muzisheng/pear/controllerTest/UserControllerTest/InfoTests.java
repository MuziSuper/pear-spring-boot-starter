package cn.muzisheng.pear.controllerTest.UserControllerTest;

import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.AuthorizationException;
import cn.muzisheng.pear.service.impl.UserServiceImpl;
import cn.muzisheng.pear.utils.JwtUtil;
import cn.muzisheng.pear.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class InfoTests {
    @Mock
    private HttpSession session;
    @Mock
    private UserDAO userDAO;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UserServiceImpl userService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test(expected = AuthorizationException.class)
    public void userInfo_UserNotFound_ThrowsAuthorizationException() {
        when(request.getSession(true)).thenReturn(session);
        userService.userInfo(request, "token");
    }

    @Test(expected = AuthorizationException.class)
    public void userInfo_TokenEmpty_UserNotFound_ThrowsAuthorizationException() {
        User user = new User();
        when(request.getAttribute("user")).thenReturn(user);
        when(userDAO.getUserById(anyLong())).thenReturn(null);
        userService.userInfo(request, "");
    }

    @Test
    public void userInfo_ValidUser_ReturnsUpdatedUserInfo() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setToken("oldToken");

        when(request.getAttribute("user")).thenReturn(user);
        when(userDAO.getUserById(anyLong())).thenReturn(user);
        when(jwtUtil.refreshToken("token")).thenReturn("newToken");

        ResponseEntity<Result<User>> response = userService.userInfo(request, "token");

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(User.class, response.getBody().getData().getClass());
        assertEquals("newToken", response.getBody().getData().getToken());
    }

    @Test
    public void userInfo_InvalidToken_GeneratesNewToken() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setToken("oldToken");

        when(request.getAttribute("user")).thenReturn(user);
        when(userDAO.getUserById(anyLong())).thenReturn(user);
        when(jwtUtil.refreshToken(anyString())).thenThrow(new AuthorizationException());
        when(jwtUtil.generateTokenWithEmail(anyString())).thenReturn("newToken");

        ResponseEntity<Result<User>> response = userService.userInfo(request, "token");

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(User.class, response.getBody().getData().getClass());
        assertEquals("newToken", response.getBody().getData().getToken());
    }
}