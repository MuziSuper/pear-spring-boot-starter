package cn.muzisheng.pear.service;

import cn.muzisheng.pear.entity.Config;
import cn.muzisheng.pear.service.impl.ConfigServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


public interface ConfigService {
    /**
     * 从环境缓存中获取环境信息，默认为字符串类型，若未命中，则从.properties文件中获取，查询不到则查询系统环境变量，还查询不到则返回null。在.properties文件的遍历中，依次将键值对存入环境缓存中，直到查找到需要的数据。
     * @param key 键名
     **/
    public String getEnv(String key);
    /**
     * 从环境缓存中获取环境信息，指定为布尔类型，若未命中，则从.properties文件中获取，查询不到则查询系统环境变量，还查询不到则返回null。在.properties文件的遍历中，依次将键值对存入环境缓存中，直到查找到需要的数据。
     * @param key 键名
     **/
    public boolean getBoolEnv(String key);
    /**
     * 从环境缓存中获取环境信息，指定为整数类型，若未命中，则从.properties文件中获取，查询不到则查询系统环境变量，还查询不到则返回null。在.properties文件的遍历中，依次将键值对存入环境缓存中，直到查找到需要的数据。
     * @param key 键名
     **/
    public int getIntEnv(String key);
    /**
     * 在配置缓存中添加配置信息
     * @param key 键名
     * @param value 键值
     * @param format 键值数据类型
     * @param autoload 是否项目启动时自动加载
     * @param pub 是否为公开数据
     **/
    public void setValue(String key, String value,String format, boolean autoload,boolean pub);
    /**
     * 获取配置缓存中的配置信息，默认为字符串类型
     **/
    public String getValue(String key);
    /**
     * 获取配置缓存中的配置信息，指定为整数类型
     **/
    public int getIntValue(String key,int defaultValue);
    /**
     * 获取配置缓存中的配置信息，指定为布尔类型
     **/
    public boolean getBoolValue(String key);
    /**
     * 将配置信息存入到数据库的config表中
     **/
    public void checkValue(String key,String defaultValue,String format,boolean autoload,boolean pub);
    public void loadAutoLoads();
    public Config[] loadPublicConfigs();
}
