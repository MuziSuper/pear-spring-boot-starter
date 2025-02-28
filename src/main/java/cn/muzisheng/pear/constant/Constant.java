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
    public static final int GENERAL_EXCEPTION = 500;
    public static final int SCALE_EXCEPTION = 502;
    public static final int USER_EXCEPTION = 503;
    public static final int HOOK_EXCEPTION = 504;
    public static final int SQL_EXCEPTION = 505;
    public static final int ILLEGAL_EXCEPTION = 400;
    public static final int UNAPPROVED_EXCEPTION = 401;

    // 响应信息
    public static final String ILLEGAL_MESSAGE_EXCEPTION = "The user passed in an illegal parameter.";
    public static final String HOOK_NOTFOUND_EXCEPTION = "The hook method failed to obtain the result.";
    public static final String HOOK_UNCHECK_EXCEPTION = "An exception of checking type occurs after the hook method is called, or the calling is unauthorized.";
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

    // 默认搜索properties路径
    public static final String APP_DEFAULT_SEARCH_PROPERTIES_PATH="src/main/resources";

    // static resources
    public static final String ICON_SVG_ADDRESS="ICON_SVG_ADDRESS";
    public static final String ICON_PNG_ADDRESS="ICON_PNG_ADDRESS";
    public static final String KEY_SITE_NAME="KEY_SITE_NAME";
    public static final String KEY_SITE_SIGNIN_URL= "KEY_SITE_SIGNIN_URL";
    public static final String KEY_SITE_SIGNUP_URL= "KEY_SITE_SIGNUP_URL";
    public static final String KEY_SITE_LOGOUT_URL= "KEY_SITE_LOGOUT_URL";
    public static final String KEY_SITE_RESET_PASSWORD_URL= "KEY_SITE_RESET_PASSWORD_URL";
    public static final String KEY_SITE_SIGNIN_API= "KEY_SITE_SIGNIN_API";
    public static final String KEY_SITE_SIGNUP_API= "KEY_SITE_SIGNUP_API";
    public static final String KEY_SITE_RESET_PASSWORD_DONE_API= "KEY_SITE_RESET_PASSWORD_DONE_API";
    public static final String KEY_SITE_CHANGE_EMAIL_DONE_API= "KEY_SITE_CHANGE_EMAIL_DONE_API";
    public static final String KEY_SITE_LOGIN_NEXT= "KEY_SITE_LOGIN_NEXT";
    public static final String KEY_SITE_USER_ID_TYPE= "KEY_SITE_USER_ID_TYPE";


    // permission type
    public static final String PERMISSION_ALL="all";
    public static final String PERMISSION_CREATE="create";
    public static final String PERMISSION_READ="read";
    public static final String PERMISSION_UPDATE="update";
    public static final String PERMISSION_DELETE="delete";

    // groupRole type
    public static final String GROUP_ROLE_ADMIN="Admin";
    public static final String GROUP_ROLE_MEMBER="member";

    // group type
    public static final String GROUP_TYPE_ADMIN="Admin";
    public static final String GROUP_TYPE_APP="app";

    // Admin param
    public static final int DEFAULT_QUERY_LIMIT =102400;

    // dataset statement
//    const (
//	FilterOpIsNot          = "is not"
//	FilterOpEqual          = "="
//	FilterOpNotEqual       = "<>"
//	FilterOpIn             = "in"
//	FilterOpNotIn          = "not_in"
//	FilterOpGreater        = ">"
//	FilterOpGreaterOrEqual = ">="
//	FilterOpLess           = "<"
//	FilterOpLessOrEqual    = "<="
//	FilterOpLike           = "like"
//	FilterOpBetween        = "between"
//)
//
//// 排序条件
//const (
//	OrderOpDesc = "desc"
//	OrderOpAsc  = "asc"
//)
    public static final String FILTER_OP_IS_NOT = "is not";
    public static final String FILTER_OP_EQUAL = "=";
    public static final String FILTER_OP_NOT_EQUAL = "<>";
    public static final String FILTER_OP_IN = "in";
    public static final String FILTER_OP_NOT_IN = "not_in";
    public static final String FILTER_OP_GREATER = ">";
    public static final String FILTER_OP_GREATER_OR_EQUAL = ">=";
    public static final String FILTER_OP_LESS = "<";
    public static final String FILTER_OP_LESS_OR_EQUAL = "<=";
    public static final String FILTER_OP_LIKE = "like";
    public static final String FILTER_OP_BETWEEN = "between";
    public static final String ORDER_OP_DESC = "desc";
    public static final String ORDER_OP_ASC = "asc";
}
