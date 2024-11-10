package cn.muzisheng.pear.constant;

import java.time.format.DateTimeFormatter;

public class Constant {
    // 密码加密的盐
    public static final String APP_USER_PASSWORD_SALT = "PEAR_";

    // 帮助信息
    public static final String HELP_OPTION="option";
    public static final String HELP_LONG_OPTION="longOption";
    public static final String HELP_DESCRIPTION="description";
    public static final String HELP_ARGUMENTS="arguments";

    // 响应状态
    public static final int SCALE_EXCEPTION = 502;
    public static final int USER_EXCEPTION = 503;
    public static final int ILLEGAL_EXCEPTION = 400;

    // 响应信息
    public static final String ILLEGAL_MESSAGE_EXCEPTION = "The user passed in an illegal parameter";

    // 日期格式
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // session key
    public static final String SESSION_USER_ID = "_pear_uid";

}
