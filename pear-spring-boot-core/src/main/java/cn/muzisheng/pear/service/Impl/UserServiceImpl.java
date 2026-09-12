package cn.muzisheng.pear.service.Impl;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.Context;
import cn.muzisheng.pear.JwtUtil;
import cn.muzisheng.pear.annotation.Verification;
import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.AuthorizationException;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.model.Response;
import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.params.CurrentUserResponse;
import cn.muzisheng.pear.params.LoginForm;
import cn.muzisheng.pear.params.LoginResponse;
import cn.muzisheng.pear.params.RegisterUserForm;
import cn.muzisheng.pear.service.UserService;
import cn.muzisheng.pear.util.AssertUtil;
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
        AssertUtil.notNull(registerUserForm, new IllegalException());
        AssertUtil.isFalse(registerUserForm.equals(new RegisterUserForm()), new IllegalException());
        AssertUtil.hasText(registerUserForm.getPassword(), new IllegalException("empty password"));
        if (userDAO.isExistsByEmail(registerUserForm.getEmail())) {
            LOG.error("email has exists: {}", registerUserForm.getEmail());
            result.setError("Email has exists");
            result.setStatus(Constant.USER_EXCEPTION);
            return result.value();
        }
        User user = userDAO.createUser(registerUserForm.getEmail(), registerUserForm.getPassword());
        if (user == null) {
            LOG.error("failed to create a user: {}", registerUserForm.getEmail());
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
            LOG.error("update user fields fail, user email: {}", registerUserForm.getEmail());
        }

        /*
         * 触发用户注册事件，发布消息，
         * 后期搭建消息系统进行补充
         */

        LOG.info("register user success, user email: {}", registerUserForm.getEmail());

        Map<String, Object> req = new HashMap<>();
        req.put("email", user.getEmail());
        req.put("activation", user.getActivated());

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
    public ResponseEntity<Result<LoginResponse>> login(HttpServletRequest request, LoginForm loginForm) {
        Response<LoginResponse> result = new Response<>();

        // 判断参数是否为空,有token直接判断token (携带token且只输入密码也可以过，但前端要限制)，没有token就必须有email和password
        AssertUtil.notNull(loginForm, new IllegalException());
        AssertUtil.isTrue(hasText(loginForm.getAuthToken()) || hasText(loginForm.getEmail()), new IllegalException("empty email"));
        AssertUtil.isTrue(hasText(loginForm.getAuthToken()) || hasText(loginForm.getPassword()), new IllegalException("empty password"));

        // 获取用户信息
        User user;
        if (hasText(loginForm.getPassword())) {
            user = userDAO.getUserByEmail(loginForm.getEmail());
            // 查无用户直接返回
            if (user == null) {
                result.setError("user not exist");
                result.setStatus(Constant.USER_EXCEPTION);
                return result.value();
            }
            // 密码错误直接返回
            if (!checkPassword(user, loginForm.getPassword())) {
                result.setError("unauthorized");
                result.setStatus(Constant.USER_EXCEPTION);
                return result.value();
            }
        } else {
            // 从token解析出user,内部错误会抛出自定义异常不用捕获
            user = decodeHashToken(loginForm.getAuthToken());
        }
        login(request, user);
        result.setData(LoginResponse.from(user, jwtUtil.generateTokenWithEmail(user.getEmail())));
        return result.value();
    }

    @Override
    public ResponseEntity<Result<CurrentUserResponse>> userInfo(HttpServletRequest request, String token) {
        Response<CurrentUserResponse> result = new Response<>();
        User user=currentUser(request);
        if(user==null){
            throw new AuthorizationException();
        }
        user=userDAO.getUserById(user.getId());
        if(user==null){
            throw new AuthorizationException();
        }
        result.setData(CurrentUserResponse.from(user));
        return result.value();
    }

    /**
     * 获取当前用户信息，并缓存到请求上下文中，返回 null 则当前为无用户状态。
     **/
    public User currentUser(HttpServletRequest request) {
        // JWT 拦截器会优先写入明确的当前用户 request scope。
        User currentUser = resolveUserFromRequestValue(Context.getRequestScope(request, Constant.REQUEST_CURRENT_USER));
        if (currentUser != null) {
            return cacheCurrentUser(request, currentUser);
        }
        // 兼容旧逻辑：部分代码曾把 User 或用户 id 放在 request 的 SESSION_USER_ID 中。
        User legacyRequestUser = resolveUserFromRequestValue(Context.getRequestScope(request, Constant.SESSION_USER_ID));
        if (legacyRequestUser != null) {
            return cacheCurrentUser(request, legacyRequestUser);
        }
        // 浏览器 session fallback，JWT 认证路径不会创建 session。
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        User sessionUser = resolveUserFromRequestValue(session.getAttribute(Constant.SESSION_USER_ID));
        if (sessionUser != null) {
            return cacheCurrentUser(request, sessionUser);
        }
        return null;
    }

    /**
     * 将不同上下文中保存的用户对象或用户 id 解析为 User。
     **/
    private User resolveUserFromRequestValue(Object value) {
        if (value instanceof User user) {
            return user;
        }
        if (value instanceof Number userId) {
            return userDAO.getUserById(userId.longValue());
        }
        if (value instanceof String userId) {
            try {
                return userDAO.getUserById(Long.parseLong(userId));
            } catch (NumberFormatException e) {
                LOG.warn("invalid user id in context: {}", userId);
            }
        }
        return null;
    }

    /**
     * 将当前用户写回新 request key，同时保留旧 request key 的兼容缓存。
     **/
    private User cacheCurrentUser(HttpServletRequest request, User user) {
        Context.setRequestScope(request, Constant.REQUEST_CURRENT_USER, user);
        Context.setRequestScope(request, Constant.SESSION_USER_ID, user);
        return user;
    }

    /**
     * 刷新用户最新登陆ip, 将用户id存入网站会话中
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
     * 校验密码，兼容旧 SHA-256 哈希并在校验成功后尝试迁移到 BCrypt。
     **/
    public boolean checkPassword(User user, String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalException("Password cannot be empty.");
        }
        String stored = user.getPassword();
        if (stored == null || stored.isBlank()) {
            return false;
        }
        if (userDAO.getPasswordService().matches(password, stored)) {
            upgradePasswordIfNeeded(user, password, stored);
            return true;
        }
        // 兼容测试或外部自定义 DAO 的历史覆写；真实 BCrypt 每次随机加盐，不会在这里误匹配。
        return stored.equals(userDAO.HashPassword(password));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * 旧哈希登录成功后尝试迁移到新 BCrypt 哈希，迁移失败不影响本次登录。
     **/
    private void upgradePasswordIfNeeded(User user, String rawPassword, String storedPassword) {
        if (!userDAO.getPasswordService().needsUpgrade(storedPassword)) {
            return;
        }
        try {
            user.setPassword(userDAO.HashPassword(rawPassword));
            if (!userDAO.updateUserById(user)) {
                LOG.warn("password hash upgrade failed for user id: {}", user.getId());
            }
        } catch (RuntimeException e) {
            LOG.warn("password hash upgrade error for user id: {}", user.getId(), e);
        }
    }

    /**
     * 解析token，返回用户信息
     * @throws AuthorizationException token过期或无法错误
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
