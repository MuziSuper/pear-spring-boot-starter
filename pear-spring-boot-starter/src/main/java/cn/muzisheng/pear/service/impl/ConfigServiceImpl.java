package cn.muzisheng.pear.service.impl;

import cn.muzisheng.pear.entity.Config;
import cn.muzisheng.pear.initialize.ApplicationInitialization;
import cn.muzisheng.pear.service.ConfigService;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Comments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class ConfigServiceImpl implements ConfigService {
    @Autowired
    private Environment environment;

    @Override
    public String getEnv(String key) {
        return "";
    }

    @Override
    public boolean getBoolEnv(String key) {
        return false;
    }

    @Override
    public int getIntEnv(String key) {
        return 0;
    }

    @Override
    public void setValue(String key, String value, String format, boolean autoload, boolean pub) {

    }

    @Override
    public String getValue(String key) {
        return "";
    }

    @Override
    public int getIntValue(String key, int defaultValue) {
        return 0;
    }

    @Override
    public boolean getBoolValue(String key) {
        return false;
    }

    @Override
    public void checkValue(String key, String defaultValue, String format, boolean autoload, boolean pub) {

    }

    @Override
    public void loadAutoLoads() {

    }

    @Override
    public Config[] loadPublicConfigs() {
        return new Config[0];
    }

    public String lookupEnv(String key) {
        if (ApplicationInitialization.EnvCache != null) {
            String value = ApplicationInitialization.EnvCache.get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
