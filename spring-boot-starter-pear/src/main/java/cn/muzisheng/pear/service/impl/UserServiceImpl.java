package cn.muzisheng.pear.service.impl;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.params.RegisterUserForm;
import cn.muzisheng.pear.service.LogService;
import cn.muzisheng.pear.service.UserService;
import cn.muzisheng.pear.utils.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private LogService logService;
    @Override
    public Response<Map<String, Object>> register(HttpServletRequest request, RegisterUserForm registerUserForm) {
        Response<Map<String, Object>> result=new Response<>();
        if(registerUserForm==null||registerUserForm.equals(new RegisterUserForm())){
            throw new IllegalException();
        }
        if(userDAO.isExistsByEmail(registerUserForm.getEmail())){
            result.setError("Email has exists");
            result.setStatus(Constant.USER_EXCEPTION);
            return result.value();
        }
        User user=userDAO.createUser(registerUserForm.getEmail(),registerUserForm.getPassword());
        if (user==null){
            logService.error("failed to create a user: "+registerUserForm.getEmail());
            result.setStatus(Constant.USER_EXCEPTION);
            return result;
        }
        user.setDisplayName(registerUserForm.getDisplayName());
        user.setFirstName(registerUserForm.getFirstName());
        user.setLastName(registerUserForm.getLastName());
        user.setLocale(registerUserForm.getLocale());
        user.setSource(registerUserForm.getSource());
        user.setTimezone(registerUserForm.getTimezone());
        user.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        user.setLastLoginIp(request.getRemoteAddr());

        if(!userDAO.updateUserById(user)){
            logService.error("update user fields fail, user email: "+registerUserForm.getEmail());
        }

        /*
         * 触发用户注册事件，发送消息，
         * 后期消息系统进行补充
         */

        Map<String, Object> req=new HashMap<>();
        req.put("email",user.getEmail());
        req.put("activation",user.isActivated());

        /*
         * 获取缓存中的配置项，确认用户是否启用激活用户操作，
         * 后期缓存系统进行补充
         */
//        if(!user.isActivated()&&GetValue(USER_ENABLE_ACTIVATED)){
//              sendMessage();
//        }else{}

        login(request,user);
        result.setData(req);
        return result.value();
    }



    private void login(HttpServletRequest request,User user){
        userDAO.setLastLogin(user,request.getRemoteAddr());
        HttpSession session= request.getSession(true);
        session.setAttribute(Constant.SESSION_USER_ID,user.getId());

        /*
         * 触发用户登陆事件，发送消息，
         * 后期消息系统进行补充
         */

    }
}
