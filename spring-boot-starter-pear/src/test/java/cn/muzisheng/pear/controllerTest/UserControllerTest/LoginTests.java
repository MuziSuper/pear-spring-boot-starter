package cn.muzisheng.pear.controllerTest.UserControllerTest;


import java.util.*;
import java.math.*;


import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.AuthorizationException;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.params.LoginForm;
import cn.muzisheng.pear.properties.TokenProperties;
import cn.muzisheng.pear.service.LogService;
import cn.muzisheng.pear.service.UserService;
import cn.muzisheng.pear.service.impl.UserServiceImpl;
import cn.muzisheng.pear.utils.JwtUtil;
import cn.muzisheng.pear.utils.Response;
import cn.muzisheng.pear.utils.Result;
import org.hibernate.mapping.Any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class LoginTests {

    @Mock
    private UserDAO userDAO;

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private TokenProperties tokenProperties;
    @Mock
    private LogService logService;
    @InjectMocks
    private UserServiceImpl userService;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        when(tokenProperties.getTokenExpire()).thenReturn(1000000000L);
        when(tokenProperties.getTokenHead()).thenReturn("Bearer ");
        when(tokenProperties.getTokenSalt()).thenReturn("secret");

    }

    @Test
    void login_InvalidForm() {
        LoginForm loginForm = null;
        assertThrows(IllegalException.class, () -> userService.login(request, loginForm));
    }

    @Test
    void login_InvalidEmailAndPassword() {
        LoginForm loginForm = new LoginForm();
        loginForm.setAuthToken("");
        loginForm.setEmail("");
        loginForm.setPassword("");
        assertThrows(IllegalException.class, () -> userService.login(request, loginForm), "The user passed in an illegal parameter");
    }

    @Test
    void login_ErrorPassword() {
        LoginForm loginForm = new LoginForm();
        loginForm.setAuthToken("");
        loginForm.setEmail("test@example.com");
        loginForm.setPassword("");
        assertThrows(IllegalException.class, () -> userService.login(request, loginForm), "empty password");
    }

    @Test
    void login_InvalidUser() {
        LoginForm loginForm = new LoginForm();
        loginForm.setAuthToken("");
        loginForm.setEmail("test@example.com");
        loginForm.setPassword("wrongPassword");

        when(userDAO.getUserByEmail("test@example.com")).thenReturn(null);
        ResponseEntity<Result<User>> response = userService.login(request, loginForm);

        assertEquals(Constant.USER_EXCEPTION, response.getStatusCode().value());
        assertEquals("user not exist", response.getBody().getError());
    }

    @Test
    void login_UserError() {
        LoginForm loginForm = new LoginForm();
        loginForm.setAuthToken("");
        loginForm.setEmail("test@example.com");
        loginForm.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("wrongPassword");
        when(userDAO.HashPassword(anyString())).thenReturn("");
        when(userDAO.getUserByEmail("test@example.com")).thenReturn(user);
        when(userDAO.HashPassword("wrongPassword")).thenReturn("wrongPassword");
        ResponseEntity<Result<User>> response = userService.login(request, loginForm);

        assertEquals(Constant.USER_EXCEPTION, response.getStatusCode().value());
        assertEquals("unauthorized", response.getBody().getError());
    }

    @Test
    void login_Normal_WithPasswordAndEmail() {
        LoginForm loginForm = new LoginForm();
        loginForm.setAuthToken("");
        loginForm.setEmail("test@example.com");
        loginForm.setPassword("validPassword");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("validPassword");
        when(userDAO.getUserByEmail("test@example.com")).thenReturn(user);
        when(userService.checkPassword(user, "validPassword")).thenReturn(true);
        when(userService.decodeHashToken("test@example.com")).thenReturn(user);

        ResponseEntity<Result<User>> response = userService.login(request, loginForm);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("test@example.com", response.getBody().getData().getEmail());
        assertEquals("test", response.getBody().getData().getToken());
    }

    @Test
    void login_WrongDecodeHashToken_Expired() {

            LoginForm loginForm = new LoginForm();
            loginForm.setAuthToken("test");
            when(jwtUtil.getEmailFromToken("test")).thenReturn(null);

        assertThrows(AuthorizationException.class, () -> userService.login(request, loginForm), "token expired");
    }
    @Test
    void login_WrongDecodeHashToken_BadToken() {

            LoginForm loginForm = new LoginForm();
            loginForm.setAuthToken("test");
            when(jwtUtil.getEmailFromToken("token")).thenReturn("testEmail");
            when(userDAO.getUserByEmail("testEmail")).thenReturn(null);
            ResponseEntity<Result<User>> response = userService.login(request, loginForm);
            assertEquals(401, response.getStatusCode().value());

        assertThrows(AuthorizationException.class, () -> userService.login(request, loginForm), "bad token");
    }

}