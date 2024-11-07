package cn.muzisheng.pear.service.impl;

import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.params.RegisterUserForm;
import cn.muzisheng.pear.service.LogService;
import cn.muzisheng.pear.service.UserService;
import cn.muzisheng.pear.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private LogService logService;

    @Override
    public Result<Object> register(RegisterUserForm registerUserForm) {
        Result<Object> result = new Result<>();
        if(registerUserForm==null){
            throw new IllegalException();
        }
        if(userDAO.isExistsByEmail(registerUserForm.getEmail())){
            result.setMessage("Email has exists");
            return result;
        }
        User user=userDAO.createUser(registerUserForm.getEmail(),registerUserForm.getPassword());
        if (user==null){
            logService.error("failed to create a user: "+email);

        }

    }

}
