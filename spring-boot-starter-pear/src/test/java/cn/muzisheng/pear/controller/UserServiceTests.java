package cn.muzisheng.pear.controller;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.params.RegisterUserForm;
import cn.muzisheng.pear.service.LogService;
import cn.muzisheng.pear.service.UserService;
import cn.muzisheng.pear.service.impl.UserServiceImpl;
import cn.muzisheng.pear.utils.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTests {
    @Mock
    private UserDAO userDAO;

    @Mock
    private LogService logService;

    @InjectMocks
    private UserServiceImpl userService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void register_NullRegisterUserForm_ThrowsIllegalException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        assertThrows(IllegalException.class, () -> userService.register(request, null));
    }

    @Test
    void register_EmptyRegisterUserForm_ThrowsIllegalException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        RegisterUserForm emptyForm = new RegisterUserForm();
        assertThrows(IllegalException.class, () -> userService.register(request, emptyForm));
    }

    @Test
    void register_EmailExists_ReturnsError() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        RegisterUserForm registerUserForm = new RegisterUserForm("test@example.com", "password", "displayName", "firstName", "lastName", "locale", "timezone", "source");

        when(userDAO.isExistsByEmail(anyString())).thenReturn(false);

        Response<Map<String, Object>> response = userService.register(request, registerUserForm);

        assertEquals(Constant.USER_EXCEPTION, response.getStatus());
        assertEquals("Email has exists", response.getError());
        verify(userDAO, never()).createUser(anyString(), anyString());
    }

    @Test
    void register_UserCreationFails_ReturnsError() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        RegisterUserForm registerUserForm = new RegisterUserForm("test@example.com", "password", "displayName", "firstName", "lastName", "locale", "timezone", "source");

        when(userDAO.isExistsByEmail(anyString())).thenReturn(false);
        when(userDAO.createUser(anyString(), anyString())).thenReturn(null);

        Response<Map<String, Object>> response = userService.register(request, registerUserForm);

        assertEquals(Constant.USER_EXCEPTION, response.getStatus());
        verify(logService).error("failed to create a user: " + registerUserForm.getEmail());
    }

    @Test
    void register_UserCreationAndUpdatesSuccess_ReturnsSuccess() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);

        RegisterUserForm registerUserForm = new RegisterUserForm("test@example.com", "password", "displayName", "firstName", "lastName", "locale", "timezone", "source");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userDAO.isExistsByEmail(anyString())).thenReturn(false);
        when(userDAO.createUser(anyString(), anyString())).thenReturn(user);
        when(userDAO.updateUserById(any(User.class))).thenReturn(true);

        Response<Map<String, Object>> response = userService.register(request, registerUserForm);

        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());

        verify(userDAO).setLastLogin(user, request.getRemoteAddr());
        verify(session).setAttribute(eq(Constant.SESSION_USER_ID), eq(user.getId()));
    }

    @Test
    void register_UserUpdateFails_LogsError() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);

        RegisterUserForm registerUserForm = new RegisterUserForm("test@example.com", "password", "displayName", "firstName", "lastName", "locale", "timezone", "source");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userDAO.isExistsByEmail(anyString())).thenReturn(false);
        when(userDAO.createUser(anyString(), anyString())).thenReturn(user);
        when(userDAO.updateUserById(any(User.class))).thenReturn(false);

        Response<Map<String, Object>> response = userService.register(request, registerUserForm);

        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());

        verify(logService).error("update user fields fail, user email: " + registerUserForm.getEmail());
        verify(userDAO).setLastLogin(user, request.getRemoteAddr());
        verify(session).setAttribute(eq(Constant.SESSION_USER_ID), eq(user.getId()));
    }
}
