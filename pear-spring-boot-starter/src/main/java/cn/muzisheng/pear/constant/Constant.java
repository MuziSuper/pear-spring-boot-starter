package cn.muzisheng.pear.constant;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

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
    public static final int UNAPPROVED_EXCEPTION = 401;

    // 响应信息
    public static final String ILLEGAL_MESSAGE_EXCEPTION = "The user passed in an illegal parameter";

    // 日期格式
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // session的用户id键名
    public static final String SESSION_USER_ID = "_pear_uid";

    // token默认配置
    public static final long TOKEN_DEFAULT_EXPIRE_DAY = 7 * 24 * 60 * 60 * 1000L;
    public static final String TOKEN_DEFAULT_SECRET_SALT = "pear_token_";
    public static final String TOKEN_DEFAULT_SECRET_PREFIX = "Bearer ";

    // 缓存
    public static final Long CACHE_EXPIRED=60*10L;

//    const (
//    ConfigFormatJSON     = "json"
//    ConfigFormatYAML     = "yaml"
//    ConfigFormatInt      = "int"
//    ConfigFormatFloat    = "float"
//    ConfigFormatBool     = "bool"
//    ConfigFormatText     = "text"
//    ConfigFormatSecurity = "security"
//    ConfigFormatDate     = "date"
//    ConfigFormatDatetime = "datetime"
//    ConfigFormatDuration = "duration"
//            )
    // 配置项数据类型
    public static final String ConfigFormatJSON="json";
    public static final String ConfigFormatYAML="yaml";
    public static final String ConfigFormatInt="int";
    public static final String ConfigFormatFloat="float";
    public static final String ConfigFormatBool="bool";
    public static final String ConfigFormatText="text";
    public static final String ConfigFormatSecurity="security";
    public static final String ConfigFormatDate="date";
    public static final String ConfigFormatDatetime="datetime";
    public static final String ConfigFormatLocalDateTime="localDateTime";
    public static final String ConfigFormatDuration="duration";

    // 缓存项
    public static final String ENV_CONFIG_CACHE_SIZE="env_config_cache_size";

    // 日志默认配置
    public static final String LOG_DEFAULT_PATTERN="%d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level) %-17black(%thread) %-82green(%logger{70}-%line) %highlight(%msg){black} %highlight(%ex){red} \n";
    public static final String LOG_DEFAULT_LEVEL="INFO";
    public static final String LOG_DEFAULT_FILE_PATTERN="%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";
    public static final String LOG_DEFAULT_FILE_PATH="log";
    public static final String LOG_DEFAULT_LOG_CATALOGUE_PATH="log/log-day";
    public static final String LOG_DEFAULT_WARN_CATALOGUE_PATH="log/warn-day";
    public static final String LOG_DEFAULT_ERROR_CATALOGUE_PATH="log/error-day";

}
