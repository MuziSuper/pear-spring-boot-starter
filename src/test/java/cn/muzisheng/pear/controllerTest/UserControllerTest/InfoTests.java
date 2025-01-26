//package cn.muzisheng.pear.controllerTest.UserControllerTest;
//
//import cn.muzisheng.pear.controller.UserApi;
//import cn.muzisheng.pear.dao.UserDAO;
//import cn.muzisheng.pear.entity.User;
//import cn.muzisheng.pear.exception.AuthorizationException;
//import cn.muzisheng.pear.service.impl.UserServiceImpl;
//import cn.muzisheng.pear.utils.JwtUtil;
//import cn.muzisheng.pear.utils.Result;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.mockito.ArgumentMatchers.*;
//
//@SpringBootTest
//public class InfoTests {
//
//    @Autowired
//    private UserApi userController;
//
//    @MockBean
//    private UserDAO userDAO;
//
//    @MockBean
//    private JwtUtil jwtUtil;
//
//    private MockMvc mockMvc;
//
//    @BeforeEach
//    public void setup() {
//        // 初始化 MockMvc 实例
//        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//    }
//
//    @Test
//    public void userInfo_UserNotFound_ThrowsAuthorizationException() throws Exception {
//        // 模拟用户未找到的情况
//        when(userDAO.getUserById(anyLong())).thenReturn(null);
//
//        mockMvc.perform(post("/user/info")
//                        .header("Authorization", "token"))  // 设置请求头，模拟携带 token
//                .andExpect(status().isUnauthorized());  // 断言返回 401 Unauthorized 状态
//    }
//
//    @Test
//    public void userInfo_TokenEmpty_UserNotFound_ThrowsAuthorizationException() throws Exception {
//        User user = new User();
//        when(userDAO.getUserById(anyLong())).thenReturn(null);
//        when(jwtUtil.refreshToken(anyString())).thenThrow(new AuthorizationException());
//
//        mockMvc.perform(post("/user/info")
//                        .header("Authorization", ""))  // 模拟 token 为空的情况
//                .andExpect(status().isUnauthorized());  // 断言返回 401 Unauthorized 状态
//    }
//
//    @Test
//    public void userInfo_ValidUser_ReturnsUpdatedUserInfo() throws Exception {
//        User user = new User();
//        user.setId(1L);
//        user.setEmail("test@example.com");
//        user.setToken("oldToken");
//
//        when(userDAO.getUserById(anyLong())).thenReturn(user);
//        when(jwtUtil.refreshToken(anyString())).thenReturn("newToken");
//
//        mockMvc.perform(post("/user/info")
//                        .header("Authorization", "token"))  // 设置携带有效 token 的请求
//                .andExpect(status().isOk())  // 断言返回 200 OK 状态
//                .andExpect(jsonPath("$.data.token").value("newToken"))  // 断言返回的 token 为 "newToken"
//                .andExpect(jsonPath("$.data.email").value("test@example.com"));  // 断言返回的 email 为 "test@example.com"
//    }
//
//    @Test
//    public void userInfo_InvalidToken_GeneratesNewToken() throws Exception {
//        User user = new User();
//        user.setId(1L);
//        user.setEmail("test@example.com");
//        user.setToken("oldToken");
//
//        when(userDAO.getUserById(anyLong())).thenReturn(user);
//        when(jwtUtil.refreshToken(anyString())).thenThrow(new AuthorizationException());
//        when(jwtUtil.generateTokenWithEmail(anyString())).thenReturn("newToken");
//
//        mockMvc.perform(post("/user/info")
//                        .header("Authorization", "invalidToken"))  // 设置携带无效 token 的请求
//                .andExpect(status().isOk())  // 断言返回 200 OK 状态
//                .andExpect(jsonPath("$.data.token").value("newToken"))  // 断言返回的 token 为 "newToken"
//                .andExpect(jsonPath("$.data.email").value("test@example.com"));  // 断言返回的 email 为 "test@example.com"
//    }
//}
