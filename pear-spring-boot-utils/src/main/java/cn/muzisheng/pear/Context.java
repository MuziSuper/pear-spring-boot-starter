package cn.muzisheng.pear;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * 上下文数据存储，存储时间在application.properties中设置默认为7d
 **/
public class Context {
    private static final Logger logger = LoggerFactory.getLogger(Context.class);

    /**
     * 在当前请求中存储数据
     **/
    public static void setRequestScope(HttpServletRequest request,String key, Object value) {
        request.setAttribute(key, value);
    }

    /**
     * 在当前客户端会话中存储数据
     **/
    public static void setSessionScope(HttpServletRequest request,String key, Object value) {
        HttpSession session = request.getSession(true);
        if (session != null) {
            session.setAttribute(key, value);
        } else {
            logger.warn("无法在当前请求中存储数据, key: {}", key);
        }
    }

    /**
     * 获取当前请求中存储的数据
     *
     * @param key 数据的key
     */
    public static Object getRequestScope(HttpServletRequest request,String key) {
        return request.getAttribute(key);
    }

    /**
     * 获取当前请求中存储的数据，并转换为指定类型
     *
     * @param key   数据的key
     * @param clazz 数据的class类型
     * @throws ClassCastException 如果数据类型不匹配
     */
    public static <T> T getRequestScope(HttpServletRequest request,String key, Class<T> clazz) {
        try {
            return clazz.cast(request.getAttribute(key));
        }catch (ClassCastException e){
            logger.warn("无法将数据转换为指定类型, key: {}", key);
            return null;
        }
    }
    /**
     * 获取当前客户端会话中存储的数据
     *
     * @param key 数据的key
     */
    public static Object getSessionScope(HttpServletRequest request,String key) {
        HttpSession session = request.getSession();
        if (session != null) {
            return session.getAttribute(key);
        }
        logger.warn("无法从会话中获取数据: {}", key);
        return null;
    }

    /**
     * 获取当前客户端会话中存储的数据
     *
     * @param key   数据的key
     * @param clazz 数据的class类型
     * @throws ClassCastException 如果数据类型不匹配
     */
    public static <T> T getSessionScope(HttpServletRequest request,String key, Class<T> clazz) {
        HttpSession session = request.getSession();
        if (session != null) {
            try {
                return clazz.cast(session.getAttribute(key));
            }catch (ClassCastException e){
                logger.warn("无法将数据转换为指定类型, key: {}", key);
                return null;
            }
        }
        logger.warn("无法从会话中获取数据: {}", key);
        return null;
    }

    /**
     * 获取 HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }
}
