package cn.muzisheng.pear.service;
/**
 * Environment环境信息服务层，能够自动获取、管理环境变量，并进行缓存。
 **/
public interface EnvService {
    /**
     * 从环境缓存中获取环境信息，默认为字符串类型，若未命中，则从.properties文件中获取，查询不到则查询系统环境变量，还查询不到则返回null。在.properties文件的遍历中，依次将键值对存入环境缓存中，直到查找到需要的数据。
     * @param key 键名
     **/
    String getEnv(String key);
    /**
     * 从环境缓存中获取环境信息，指定为布尔类型，若未命中，则从.properties文件中获取，查询不到则查询系统环境变量，还查询不到则返回null。在.properties文件的遍历中，依次将键值对存入环境缓存中，直到查找到需要的数据。
     * @param key 键名
     **/
    boolean getBoolEnv(String key);
    /**
     * 从环境缓存中获取环境信息，指定为整数类型，若未命中，则从 .properties文件中获取，查询不到则查询系统环境变量，还查询不到则返回null。在.properties文件的遍历中，依次将键值对存入环境缓存中，直到查找到需要的数据。
     * @param key 键名
     **/
    int getIntEnv(String key);

}
