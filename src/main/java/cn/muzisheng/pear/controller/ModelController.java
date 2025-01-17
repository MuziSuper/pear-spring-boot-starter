package cn.muzisheng.pear.controller;

import cn.muzisheng.pear.params.RegisterUserForm;
import cn.muzisheng.pear.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/{model}")
public class ModelController {
//    @PostMapping("create")
//    public ResponseEntity<Result<Map<String, Object>>> register(HttpServletRequest request, @PathVariable String model) {
//        List<>
//        return userService.register(request, registerUserForm);
//    }
}
