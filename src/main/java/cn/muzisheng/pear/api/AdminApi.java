package cn.muzisheng.pear.api;

import cn.muzisheng.pear.params.RegisterUserForm;
import cn.muzisheng.pear.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminApi {
    @PostMapping("{model}")
    public ResponseEntity<Result<Map<String, Object>>> query(HttpServletRequest request, @RequestBody RegisterUserForm registerUserForm, @PathVariable("model") String model) {
        return null;
    }
}
