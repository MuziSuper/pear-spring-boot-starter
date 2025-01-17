package cn.muzisheng.pear.test.api;


import cn.muzisheng.pear.service.ConfigService;
import cn.muzisheng.pear.service.LogService;
import cn.muzisheng.pear.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testApi {

    @Autowired
    private LogService logService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private UserService userService;
    @PostMapping("test")
    public void test() {
        logService.info("hello world");
        logService.warn("hello world");
        logService.error("hello world");
        configService.setValue("test","test","test",true,true);
        configService.loadAutoLoads();
    }
}
