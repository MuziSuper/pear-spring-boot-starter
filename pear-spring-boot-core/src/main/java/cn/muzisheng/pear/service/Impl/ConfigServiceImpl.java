package cn.muzisheng.pear.service.Impl;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.service.ConfigService;
import cn.muzisheng.pear.dao.ConfigDAO;
import cn.muzisheng.pear.entity.Config;
import cn.muzisheng.pear.initialize.PearApplicationInitialization;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

@Service
public class ConfigServiceImpl implements ConfigService {
    private final ConfigDAO configDAO;
    private final static Logger LOG = LoggerFactory.getLogger(ConfigServiceImpl.class);

    public ConfigServiceImpl(ConfigDAO configDAO) {
        this.configDAO = configDAO;
    }


    @Override
    public void setValue(String key, String value, String format, boolean autoload, boolean pub) {
        key = key.toLowerCase();
        PearApplicationInitialization.configCacheTemplate.remove(key);
        Config config = new Config();
        config.setKey(key);
        config.setValue(value);
        config.setFormat(format);
        config.setAutoload(autoload);
        config.setPub(pub);
        if (!configDAO.createConfigClausesPartialField(config)) {
            LOG.warn("setValue fail, key: {} value: {} format: {} autoload: {} pub: {}", key, value, format, autoload, pub);
        }
    }

    @Override
    public String getValue(String key) {
        key = key.toLowerCase();
        if (PearApplicationInitialization.configCacheTemplate != null) {
            String value = PearApplicationInitialization.configCacheTemplate.get(key);
            if (value != null) {
                return value;
            }
        }
        Config config = configDAO.get(key);
        if (config == null) {
            return null;
        }
        if (PearApplicationInitialization.configCacheTemplate != null) {
            PearApplicationInitialization.configCacheTemplate.put(key, config.getValue());
        }
        return config.getValue();
    }
    @SuppressWarnings("unchecked")
    public <T> T getValue(String key, Class<T> type) {
        String valueStr = getValue(key);
        if (valueStr == null) {
            return null;
        }
        if (type == Integer.class) {
            try {
                return (T) Integer.valueOf(valueStr);
            } catch (NumberFormatException e) {
                LOG.warn("Type conversion failure");
                return null;
            }
        } else if (type == Boolean.class) {
            return (T) Boolean.valueOf(valueStr);
        } else {
            return type.cast(valueStr);
        }
    }

    @Override
    public int getIntValue(String key, int defaultValue) {
        Integer intValue = getValue(key, Integer.class);
        if (intValue == null) {
            return defaultValue;
        }
        return intValue;
    }

    @Override
    public boolean getBoolValue(String key) {
        Boolean booleanValue = getValue(key, Boolean.class);
        if (booleanValue == null) {
            return false;
        }
        return booleanValue;
    }

    @Override
    public void checkValue(String key, String value, String format, boolean autoload, boolean pub) {
        Config config = new Config();
        config.setKey(key.toUpperCase());
        config.setValue(value);
        config.setFormat(format);
        config.setAutoload(autoload);
        config.setPub(pub);
        configDAO.createConfig(config);
    }

    @Override
    public void loadAutoLoads() {
        List<Config> configs = configDAO.getConfigsWithTrueAutoload();
        for (Config config : configs) {
            if (PearApplicationInitialization.configCacheTemplate != null) {
                PearApplicationInitialization.configCacheTemplate.put(config.getKey(), config.getValue());
            }
        }
    }

    @Override
    public Config[] loadPublicConfigs() {
        List<Config> configs = configDAO.getConfigsWithTruePub();
        for (Config config : configs) {
            if (PearApplicationInitialization.configCacheTemplate != null) {
                PearApplicationInitialization.configCacheTemplate.put(config.getKey(), config.getValue());
            }
        }
        return configs.toArray(new Config[0]);
    }


}
