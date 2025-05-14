package cn.muzisheng.pear.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class WebApi {

    @GetMapping("/dashboard")
    public String customPage() {
        return "dashboard.html"; // 返回静态文件的路径
    }
    @GetMapping("/settings")
    public String settingsPage() {
        return "settings.html"; // 返回静态文件的路径
    }
    @GetMapping("/login")
    public String loginPage() {
        return "login.html"; // 返回静态文件的路径
    }
    @GetMapping("/register")
    public String registerPage() {
        return "register.html"; // 返回静态文件的路径
    }
}