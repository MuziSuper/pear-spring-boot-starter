package cn.muzisheng.pear.service.impl;

import cn.muzisheng.pear.dao.ConfigDAO;
import cn.muzisheng.pear.entity.Config;
import cn.muzisheng.pear.initialize.ApplicationInitialization;
import cn.muzisheng.pear.service.ConfigService;
import cn.muzisheng.pear.service.LogService;
import cn.muzisheng.pear.utils.ReadProperties;
import io.micrometer.common.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public class ConfigServiceImpl implements ConfigService {
    private final Environment environment;
    private final ConfigDAO configDAO;
    private final LogService LOG;
    private final ReadProperties readProperties;
    @Autowired
    public ConfigServiceImpl(Environment environment, ConfigDAO configDAO, LogService logService,ReadProperties readProperties){
        this.environment = environment;
        this.LOG = logService;
        this.configDAO=configDAO;
        this.readProperties=readProperties;
    }

    @Override
    @Nullable
    public String getEnv(String key) {
        if (ApplicationInitialization.EnvCache != null) {
            String value = ApplicationInitialization.EnvCache.get(key);
            if (value != null) {
                // 刷新缓存
                ApplicationInitialization.EnvCache.add(key,value);
                return value;
            }
            value = searchAllEnv(key);
            if(value != null){
                ApplicationInitialization.EnvCache.add(key,value);
                return value;
            }
        }

        return environment.getProperty(key, String.class);
    }
    @Nullable
    public <T> T getEnv(String key,Class<T> type) {
        String valueStr=getEnv(key);
        if (valueStr == null){
            return null;
        }
        try {
            return type.cast(valueStr);
        } catch (ClassCastException e) {
            LOG.warn("Type conversion failure");
            return null;
        }
    }
    @Override
    public boolean getBoolEnv(String key) {
        return Boolean.TRUE.equals(getEnv(key, Boolean.class));
    }

    @Override
    public int getIntEnv(String key) {
        return Optional.ofNullable(getEnv(key, Integer.class)).orElse(0);
    }

    @Override
    public void setValue(String key, String value, String format, boolean autoload, boolean pub) {
        key=key.toLowerCase();
        ApplicationInitialization.ConfigCache.remove(key);
        Config config=new Config();
        config.setKey(key);
        config.setValue(value);
        config.setFormat(format);
        config.setAutoload(autoload);
        config.setPub(pub);
        if(!configDAO.createConfig(config)){
            LOG.warn("setValue fail, key "+key+" value "+value+" format "+format+" autoload "+autoload+" pub "+pub);
        }
    }

    @Override
    public String getValue(String key) {
        key=key.toLowerCase();
        if (ApplicationInitialization.ConfigCache != null) {
            String value = ApplicationInitialization.ConfigCache.get(key);
            if (value != null) {
                return value;
            }
        }
        Config config=configDAO.get(key);
        if(config==null){
            return null;
        }
        if (ApplicationInitialization.ConfigCache != null) {
            ApplicationInitialization.ConfigCache.add(key,config.getValue());
        }
        return config.getValue();
    }

    public <T> T getValue(String key,Class<T> type) {
        String valueStr=getValue(key);
        if (valueStr == null){
            return null;
        }
        try{
            return type.cast(valueStr);
        }catch(ClassCastException e){
            LOG.warn("Type conversion failure");
            return null;
        }
    }
    @Override
    public int getIntValue(String key, int defaultValue) {
        Integer intValue=getValue(key, Integer.class);
        if (intValue == null){
            return defaultValue;
        }
        return intValue;
    }

    @Override
    public boolean getBoolValue(String key) {
        Boolean booleanValue = getValue(key, Boolean.class);
        if (booleanValue == null){
            return false;
        }
        return booleanValue;
    }

    @Override
    public void checkValue(String key, String defaultValue, String format, boolean autoload, boolean pub) {
        Config config=new Config();
        config.setKey(key.toUpperCase());
        config.setValue(defaultValue);
        config.setFormat(format);
        config.setAutoload(autoload);
        config.setPub(pub);
        configDAO.createConfig(config);
    }
    /**
     * 将数据库中存储的autoload设为true的configs加载到缓存中
     **/
    @Override
    public void loadAutoLoads() {
        List<Config> configs=configDAO.getConfigsWithTrueAutoload();
        for (Config config : configs){
            if(ApplicationInitialization.ConfigCache != null){
                ApplicationInitialization.ConfigCache.add(config.getKey(),config.getValue());
            }
        }
    }

    @Override
    public Config[] loadPublicConfigs() {
        List<Config> configs=configDAO.getConfigsWithTruePub();
        for(Config config:configs){
            if(ApplicationInitialization.ConfigCache != null){
                ApplicationInitialization.ConfigCache.add(config.getKey(),config.getValue());
            }
        }
        return configs.toArray(new Config[0]);
    }
    /**
     * 查找所有配置文件，遍历配置项，将遍历到的配置项加载到缓存中，直到查找到对应key键的配置项，停止遍历。
     **/
    private String searchAllEnv(String key){
        Map<String,String> propertiesMap=readProperties.loadAllProperties();
        for (Map.Entry<String,String> entry:propertiesMap.entrySet()){
            if(ApplicationInitialization.EnvCache != null){
                ApplicationInitialization.EnvCache.add(entry.getKey(),entry.getValue());
                if(entry.getKey().equals(key)){
                    return entry.getValue();
                }
            }
        }
        return null;
    }
}
