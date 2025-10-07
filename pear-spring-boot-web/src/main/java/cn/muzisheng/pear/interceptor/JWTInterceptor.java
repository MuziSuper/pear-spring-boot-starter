package cn.muzisheng.pear.interceptor;

import cn.muzisheng.pear.JwtUtil;
import cn.muzisheng.pear.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
/**
 * 拦截器, 用于检验token
 **/
@Component
public class JWTInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private static final Logger LOG = LoggerFactory.getLogger(JWTInterceptor.class);
    public JWTInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        String token=request.getHeader("Authorization");
        if(StringUtil.isEmpty(token)||jwtUtil.getEmailFromToken(token)==null){
            LOG.warn("Host with IP address "+request.getLocalAddr()+" was blocked. The token either does not exist or has expired.");
            return false;
        }
        LOG.info("Host with IP address "+request.getLocalAddr()+"'s request was blocked. However, if the token is valid, the execution will continue.");
        return true;
    }
}
