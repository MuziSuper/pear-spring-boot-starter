package cn.muzisheng.pear.vo.user;

import cn.muzisheng.pear.vo.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@lombok.EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class RegisterUserVO extends BaseVO {
    private String email;
    private Object activation;

    public static RegisterUserVO from(Map<String, Object> source) {
        return new RegisterUserVO(String.valueOf(source.get("email")), source.get("activation"));
    }
}
