package cn.muzisheng.pear.service.impl;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.params.RegisterUserForm;
import cn.muzisheng.pear.service.LogService;
import cn.muzisheng.pear.service.UserService;
import cn.muzisheng.pear.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private LogService logService;

    @Override
    public Response<Map<String, String>> register(RegisterUserForm registerUserForm) {
        Response<Map<String, String>> result=new Response<>();
        Map<String,String> map=new HashMap<>();
        if(registerUserForm==null){
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
        map.put("LastLogin", LocalDateTime.now().toString());

    }

}
