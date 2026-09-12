package cn.muzisheng.pear.interceptor;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.Context;
import cn.muzisheng.pear.JwtUtil;
import cn.muzisheng.pear.StringUtil;
import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.AuthorizationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器，用于校验 /admin/** 请求并建立当前用户上下文。
 **/
@Component
public class JWTInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDAO userDAO;
    private static final Logger LOG = LoggerFactory.getLogger(JWTInterceptor.class);

    public JWTInterceptor(JwtUtil jwtUtil, UserDAO userDAO) {
        this.jwtUtil = jwtUtil;
        this.userDAO = userDAO;
    }

    /**
     * 校验 Authorization 头中的 JWT，成功后将用户写入 request scope，不创建 session。
     **/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        String token=request.getHeader("Authorization");
        if(StringUtil.isEmpty(token)){
            LOG.warn("Host with remote address {} was blocked. The token does not exist.", request.getRemoteAddr());
            throw new AuthorizationException("token missing");
        }
        String email;
        try {
            email = jwtUtil.getEmailFromToken(token);
        } catch (AuthorizationException e) {
            LOG.warn("Host with remote address {} was blocked. The token is invalid or expired.", request.getRemoteAddr());
            throw e;
        }
        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            LOG.warn("Host with remote address {} was blocked. The token user does not exist.", request.getRemoteAddr());
            throw new AuthorizationException("bad token");
        }
        Context.setRequestScope(request, Constant.REQUEST_CURRENT_USER, user);
        LOG.info("Host with remote address {} passed JWT authentication.", request.getRemoteAddr());
        return true;
    }
}
