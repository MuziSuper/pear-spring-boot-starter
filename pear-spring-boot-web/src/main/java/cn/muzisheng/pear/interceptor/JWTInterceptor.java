package cn.muzisheng.pear.interceptor;

import cn.muzisheng.pear.JwtUtil;
import cn.muzisheng.pear.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
/**
 * 拦截器, 用于检验token
 **/
@Component
public class JWTInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    public JWTInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token=request.getHeader("Authorization");
        if(StringUtil.isEmpty(token)){
            return false;
        }
        return jwtUtil.getEmailFromToken(token) != null;
    }
}
