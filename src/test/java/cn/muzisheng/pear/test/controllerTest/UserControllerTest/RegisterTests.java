package cn.muzisheng.pear.test.controllerTest.UserControllerTest;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.params.RegisterUserForm;
import cn.muzisheng.pear.core.user.Impl.UserServiceImpl;
import cn.muzisheng.pear.test.TestApplication;
import cn.muzisheng.pear.utils.JwtUtil;
import cn.muzisheng.pear.model.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@SpringBootTest(classes = TestApplication.class)
@ExtendWith(MockitoExtension.class)
public class RegisterTests {
    @MockBean
    private UserDAO userDAO;
    @MockBean
    private JwtUtil jwtUtil;
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
        userService=spy(new UserServiceImpl(userDAO, jwtUtil));
        HttpServletRequest request = mock(HttpServletRequest.class);
        RegisterUserForm registerUserForm = new RegisterUserForm("test@example.com", "123", "displayName", "firstName", "lastName", "locale", "timezone", "source");

        when(userDAO.isExistsByEmail(anyString())).thenReturn(true);

        ResponseEntity<Result<Map<String, Object>>> response = userService.register(request, registerUserForm);

        assertEquals(Constant.USER_EXCEPTION, response.getStatusCode().value());
        assertEquals("Email has exists", Objects.requireNonNull(response.getBody()).getError());
        verify(userDAO, never()).createUser(anyString(), anyString());
    }

    @Test
    void register_UserCreationFails_ReturnsError() {
        userService=spy(new UserServiceImpl(userDAO, jwtUtil));
        HttpServletRequest request = mock(HttpServletRequest.class);
        RegisterUserForm registerUserForm = new RegisterUserForm("test@example.com", "password", "displayName", "firstName", "lastName", "locale", "timezone", "source");

        when(userDAO.isExistsByEmail(anyString())).thenReturn(false);
        when(userDAO.createUser(anyString(), anyString())).thenReturn(null);

        ResponseEntity<Result<Map<String, Object>>> response = userService.register(request, registerUserForm);

        assertEquals(Constant.USER_EXCEPTION, response.getStatusCode().value());
    }

    @Test
    void register_UserCreationAndUpdatesSuccess_ReturnsSuccess() {
        userService=spy(new UserServiceImpl(userDAO, jwtUtil));
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);

        RegisterUserForm registerUserForm = new RegisterUserForm("test@example.com", "123", "displayName", "firstName", "lastName", "locale", "timezone", "source");

        User user = new User("test@example.com", "123");

        when(userDAO.isExistsByEmail(anyString())).thenReturn(false);
        when(userDAO.createUser(anyString(), anyString())).thenReturn(user);
        when(userDAO.updateUserById(any(User.class))).thenReturn(true);

        ResponseEntity<Result<Map<String, Object>>> response = userService.register(request, registerUserForm);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        verify(userDAO).setLastLogin(user, request.getRemoteAddr());
        verify(session).setAttribute(eq(Constant.SESSION_USER_ID), eq(user.getId()));
    }

    @Test
    void register_UserUpdateFails_LogsError() {
        userService=spy(new UserServiceImpl(userDAO, jwtUtil));
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

        ResponseEntity<Result<Map<String, Object>>> response = userService.register(request, registerUserForm);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }
}
