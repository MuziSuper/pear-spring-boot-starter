package cn.muzisheng.pear.core.user.Impl;

import cn.muzisheng.pear.core.user.UserService;
import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.mapper.dao.UserDAO;
import cn.muzisheng.pear.exception.AuthorizationException;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.params.LoginForm;
import cn.muzisheng.pear.params.RegisterUserForm;
import cn.muzisheng.pear.model.Response;
import cn.muzisheng.pear.utils.Context;
import cn.muzisheng.pear.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
@Service
public class UserServiceImpl implements UserService {
    private final static Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDAO userDAO;
    private final JwtUtil jwtUtil;
    @Autowired
    public UserServiceImpl(UserDAO userDAO, JwtUtil jwtUtil) {
        this.userDAO=userDAO;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public ResponseEntity<Result<Map<String, Object>>> register(HttpServletRequest request, RegisterUserForm registerUserForm) {
        Response<Map<String, Object>> result = new Response<>();
        if (registerUserForm == null || registerUserForm.equals(new RegisterUserForm())) {
            throw new IllegalException();
        }
        if (userDAO.isExistsByEmail(registerUserForm.getEmail())) {
            result.setError("Email has exists");
            result.setStatus(Constant.USER_EXCEPTION);
            return result.value();
        }
        User user = userDAO.createUser(registerUserForm.getEmail(), registerUserForm.getPassword());
        if (user == null) {
            LOG.error("failed to create a user: " + registerUserForm.getEmail());
            result.setStatus(Constant.USER_EXCEPTION);
            return result.value();
        }
        user.setDisplayName(registerUserForm.getDisplayName());
        user.setFirstName(registerUserForm.getFirstName());
        user.setLastName(registerUserForm.getLastName());
        user.setLocale(registerUserForm.getLocale());
        user.setSource(registerUserForm.getSource());
        user.setTimezone(registerUserForm.getTimezone());
        user.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        user.setLastLoginIp(request.getRemoteAddr());

        if (!userDAO.updateUserById(user)) {
            LOG.error("update user fields fail, user email: " + registerUserForm.getEmail());
        }

        /*
         * 触发用户注册事件，发布消息，
         * 后期消息系统进行补充
         */
        LOG.info("register user success, user email: " + registerUserForm.getEmail());

        Map<String, Object> req = new HashMap<>();
        req.put("email", user.getEmail());
        req.put("activation", user.isActivated());

        /*
         * 获取缓存中的配置项，确认用户是否启用激活用户操作，触发身份校验事件，发布消息
         * 后期缓存系统进行补充
         */
//        if(!user.isActivated()&&GetValue(USER_ENABLE_ACTIVATED)){
//              sendMessage();
//        }else{}

        login(request, user);
        result.setData(req);
        return result.value();
    }

    @Override
    public ResponseEntity<Result<User>> login(HttpServletRequest request, LoginForm loginForm) {
        Response<User> result = new Response<>();

        // 判断参数是否为空,有token直接判断token (携带token且只输入密码也可以过，但前端要限制)，没有token就必须有email和password
        if (loginForm == null || loginForm.equals(new LoginForm())) {
            throw new IllegalException();
        }
        if ((loginForm.getAuthToken() == null||loginForm.getAuthToken().isEmpty()) && loginForm.getEmail().isEmpty()) {
            throw new IllegalException("email is required");
        }
        if ((loginForm.getAuthToken() == null||loginForm.getAuthToken().isEmpty()) && loginForm.getPassword().isEmpty()) {
            throw new IllegalException("empty password");
        }

        // 获取用户信息
        User user;
        if (loginForm.getPassword()!=null) {
            user = userDAO.getUserByEmail(loginForm.getEmail());
            if (user == null) {
                result.setError("user not exist");
                result.setStatus(Constant.USER_EXCEPTION);
                return result.value();
            }
            if (!checkPassword(user, loginForm.getPassword())) {
                result.setError("unauthorized");
                result.setStatus(Constant.USER_EXCEPTION);
                return result.value();
            }
        } else {
            user = decodeHashToken(loginForm.getAuthToken());
        }
        login(request, user);
        user.setToken(jwtUtil.generateTokenWithEmail(user.getEmail()));
        result.setData(user);
        return result.value();
    }

    @Override
    public ResponseEntity<Result<User>> userInfo(HttpServletRequest request, String token) {
        Response<User> result = new Response<>();
        User user=currentUser(request);
        if(user==null){
            throw new AuthorizationException();
        }
        user=userDAO.getUserById(user.getId());
        if(user==null){
            throw new AuthorizationException();
        }
        if(!"".equals(token)){
            String newToken;
            try{
             newToken=jwtUtil.refreshToken(token);
            }catch (AuthorizationException e){
                newToken=jwtUtil.generateTokenWithEmail(user.getEmail());
            }
            user.setToken(newToken);
        }
        result.setData(user);
        return result.value();
    }

    /**
     * 获取当前用户信息，并且缓存到上下文中
     **/
    public User currentUser(HttpServletRequest request) {
        Object objectUser = Context.getRequestScope(request,Constant.SESSION_USER_ID);
        if (objectUser != null) {
            if(objectUser instanceof User){
                return (User) objectUser;
            }
        }
        Object userId = Context.getSessionScope(request,Constant.SESSION_USER_ID);
        if (userId == null) {
            return null;
        }
        User user = null;
        if(userId instanceof Long){
            user = userDAO.getUserById((long) userId);
            if (user == null) {
                return null;
            }
        }
        if(user!=null){
            Context.setRequestScope(request,Constant.SESSION_USER_ID, user);
            return user;
        }
        return null;
    }

    /**
     * 刷新用户最新登陆ip, 将用户id存入session中
     **/
    private void login(HttpServletRequest request, User user) {
        userDAO.setLastLogin(user, request.getRemoteAddr());
        Context.setSessionScope(request,Constant.SESSION_USER_ID, user.getId());
        /*
         * 触发用户登陆事件，发布消息，
         * 后期消息系统进行补充
         */
        LOG.info("login user success, user email: " + user.getEmail());
    }

    /**
     * 校验密码, 正确返回true
     **/
    public boolean checkPassword(User user, String password) {
        return user.getPassword().equals(userDAO.HashPassword(password));
    }

    /**
     * 解析token，返回用户信息
     **/
    public User decodeHashToken(String token) {
        String email;
        email = jwtUtil.getEmailFromToken(token);
        if (email == null) {
            throw new AuthorizationException("token expired");
        }
        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            throw new AuthorizationException("bad token");
        }
        return user;
    }
}
