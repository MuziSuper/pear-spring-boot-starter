package cn.muzisheng.pear.service;


import cn.muzisheng.pear.entity.Config;
/**
 * Configuration配置服务层，能够存储并缓存配置，在项目中也可以及时将数据库中的Config信息进行缓存。
 **/
public interface ConfigService {

    /**
     * 将配置信息存入到数据库的config表中,若发生冲突，则只更新value、format、autoload、public字段
     * @param key 键名
     * @param value 键值
     * @param format 键值数据类型
     * @param autoload 是否项目启动时自动加载
     * @param pub 是否为公开数据
     **/
    void setValue(String key, String value,String format, boolean autoload,boolean pub);
    /**
     * 将配置信息存入到数据库的config表中,若发生冲突，则不执行任何操作
     * @param key 键名
     * @param value 键值
     * @param format 键值数据类型
     * @param autoload 是否项目启动时自动加载
     * @param pub 是否为公开数据
     **/
    void checkValue(String key,String value,String format,boolean autoload,boolean pub);

    /**
     * 获取配置缓存中的配置信息，默认为字符串类型
     * @param key 键名
     **/
    String getValue(String key);
    /**
     * 获取配置缓存中的配置信息，指定为整数类型
     * @param key 键名
     * @param defaultValue 键值默认值
     **/
    int getIntValue(String key,int defaultValue);
    /**
     * 获取配置缓存中的配置信息，指定为布尔类型
     * @param key 键名
     **/
    boolean getBoolValue(String key);
    /**
     * 将数据库中存储的autoload设为true的configs加载到缓存中
     **/
    void loadAutoLoads();
    /**
     * 将数据库中存储的pub设为true的configs加载到缓存中,并返回configs数组
     **/
    Config[] loadPublicConfigs();
}
