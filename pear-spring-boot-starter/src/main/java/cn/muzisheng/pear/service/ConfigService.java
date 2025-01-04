package cn.muzisheng.pear.service;

import cn.muzisheng.pear.entity.Config;
import cn.muzisheng.pear.service.impl.ConfigServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


public interface ConfigService {
    /**
     * 从环境缓存中获取环境信息，默认为字符串类型，如果环境缓存中不存在，则从application.properties文件中获取，若还查询不到则返回null。在环境缓存中查找不到的数据，自动插入到环境缓存中。
     * @param key 键名
     **/
    public String getEnv(String key);
    /**
     * 从环境缓存中获取环境信息，指定为布尔类型，如果环境缓存中不存在，则从application.properties文件中获取，若还查询不到则返回null。在环境缓存中查找不到的数据，自动插入到环境缓存中。
     * @param key 键名
     **/
    public boolean getBoolEnv(String key);
    /**
     * 从环境缓存中获取环境信息，指定为整数类型，如果环境缓存中不存在，则从application.properties文件中获取，若还查询不到则返回null。在环境缓存中查找不到的数据，自动插入到环境缓存中。
     * @param key 键名
     **/
    public int getIntEnv(String key);
    /**
     * 在环境缓存中设置环境信息
     * @param key 键名
     * @param value 键值
     * @param format 键值数据类型
     * @param autoload 是否项目启动时自动加载
     * @param pub 是否为公开数据
     **/
    public void setValue(String key, String value,String format, boolean autoload,boolean pub);
    /**
     * 获取配置缓存中配置信息
     **/
    public String getValue(String key);
    /**
     * 获取缓存中配置信息，指定为整数类型
     **/
    public int getIntValue(String key,int defaultValue);
    /**
     * 获取缓存中配置信息，指定为布尔类型
     **/
    public boolean getBoolValue(String key);
    public void checkValue(String key,String defaultValue,String format,boolean autoload,boolean pub);
    public void loadAutoLoads();
    public Config[] loadPublicConfigs();
}
