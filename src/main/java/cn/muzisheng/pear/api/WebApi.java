package cn.muzisheng.pear.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebApi {

    @GetMapping("/dashboard")
    public String customPage() {
        return "forward:/static/dashboard.html"; // 返回静态文件的路径
    }
    @GetMapping("/settings")
    public String settingsPage() {
        return "forward:/static/settings.html"; // 返回静态文件的路径
    }
    @GetMapping("/login")
    public String loginPage() {
        return "forward:/static/login.html"; // 返回静态文件的路径
    }
    @GetMapping("/register")
    public String registerPage() {
        return "forward:/static/register.html"; // 返回静态文件的路径
    }
}