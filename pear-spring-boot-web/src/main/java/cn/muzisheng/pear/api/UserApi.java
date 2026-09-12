package cn.muzisheng.pear.api;

import cn.muzisheng.pear.dto.user.CurrentUserQueryDTO;
import cn.muzisheng.pear.dto.user.LoginRequestDTO;
import cn.muzisheng.pear.dto.user.RegisterUserRequestDTO;
import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.service.UserService;
import cn.muzisheng.pear.util.ApiResponseUtil;
import cn.muzisheng.pear.vo.common.ApiResponseVO;
import cn.muzisheng.pear.vo.user.CurrentUserVO;
import cn.muzisheng.pear.vo.user.LoginVO;
import cn.muzisheng.pear.vo.user.RegisterUserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserApi {
    @Autowired
    private UserService userService;

    /**
     * Registers a user from the public DTO contract and returns only safe registration metadata.
     **/
    @PostMapping("register")
    public ResponseEntity<ApiResponseVO<RegisterUserVO>> register(HttpServletRequest request,
                                                                    @RequestBody RegisterUserRequestDTO requestDTO) {
        ResponseEntity<Result<Map<String, Object>>> result = userService.register(request, requestDTO.toLegacyForm());
        return ApiResponseUtil.map(result, data -> data == null ? null : RegisterUserVO.from(data));
    }

    /**
     * Logs a user in and returns public user details with a JWT token.
     **/
    @PostMapping("login")
    public ResponseEntity<ApiResponseVO<LoginVO>> login(HttpServletRequest request, @RequestBody LoginRequestDTO requestDTO) {
        return ApiResponseUtil.map(userService.login(request, requestDTO.toLegacyForm()), data -> data == null ? null : LoginVO.from(data));
    }

    /**
     * Returns the currently authenticated user's public profile.
     **/
    @PostMapping("info")
    public ResponseEntity<ApiResponseVO<CurrentUserVO>> info(HttpServletRequest request, @ModelAttribute CurrentUserQueryDTO queryDTO) {
        return ApiResponseUtil.map(userService.userInfo(request, queryDTO.getWithToken()), data -> data == null ? null : CurrentUserVO.from(data));
    }

}
