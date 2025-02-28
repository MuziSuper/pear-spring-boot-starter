package cn.muzisheng.pear.initialize;

import cn.muzisheng.pear.model.AdminObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AdminFactory {
    /**
     * 获取pear所有的内置类
     **/
    public static ArrayList<AdminObject> getAdminContainer(){
        return AdminContainer.getPearAdminObjects();
    }
}
