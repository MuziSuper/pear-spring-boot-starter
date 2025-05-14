package cn.muzisheng.pear.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    // 头像
    private String avatar;
    // 性别
    private String gender;
    // 所在市
    private String city;
    // 所在州省
    private String region;
    // 所在国家
    private String country;
    // 额外公共信息
    private Map<String, Object> extra;
    // 额外私有信息
    private Map<String, Object> privateExtra;
}
