package cn.muzisheng.pear.service;

import cn.muzisheng.pear.entity.Config;
import cn.muzisheng.pear.service.impl.ConfigServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


public interface ConfigService {
    public String getEnv(String key);
    public boolean getBoolEnv(String key);
    public int getIntEnv(String key);
    public void setValue(String key, String value,String format, boolean autoload,boolean pub);
    public String getValue(String key);
    public int getIntValue(String key,int defaultValue);
    public boolean getBoolValue(String key);
    public void checkValue(String key,String defaultValue,String format,boolean autoload,boolean pub);
    public void loadAutoLoads();
    public Config[] loadPublicConfigs();
}
