package cn.muzisheng.pear.test.api;


import cn.muzisheng.pear.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testApi {

    @Autowired
    private LogService logService;
    @PostMapping("test")
    public void test() {
        logService.info("hello world");
        logService.warn("hello world");
        logService.error("hello world");
    }
}
