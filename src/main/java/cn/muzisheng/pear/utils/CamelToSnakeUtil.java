package cn.muzisheng.pear.utils;

/**
 * 转驼峰式命名
 **/
public class CamelToSnakeUtil {
    public static String toSnakeCase(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }
        // 在大写字母前添加下划线（首字母除外），并转为小写
        return camelCase
                .replaceAll("([a-z])([A-Z])", "$1_$2")  // userIp → user_Ip
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2") // HTTPRequest → HTTP_Request
                .toLowerCase();
    }
}