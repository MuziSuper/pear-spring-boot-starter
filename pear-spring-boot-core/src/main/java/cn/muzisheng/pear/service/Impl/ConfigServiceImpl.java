package cn.muzisheng.pear.service.Impl;

import cn.muzisheng.pear.CacheContainer;
import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.service.ConfigService;
import cn.muzisheng.pear.dao.ConfigDAO;
import cn.muzisheng.pear.entity.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigServiceImpl implements ConfigService {
    private final ConfigDAO configDAO;
    private final static Logger LOG = LoggerFactory.getLogger(ConfigServiceImpl.class);
    private final CacheContainer cacheContainer;
    public ConfigServiceImpl(ConfigDAO configDAO, CacheContainer cacheContainer) {
        this.configDAO = configDAO;
        this.cacheContainer = cacheContainer;
    }


    @Override
    public void setValue(String key, String value, String format, boolean autoload, boolean pub) {
        key = key.toLowerCase();
        if(cacheContainer.contains(Constant.PEAR_CONFIG_CACHE_NAME)){
            cacheContainer.put(Constant.PEAR_CONFIG_CACHE_NAME, key, value);
        }
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
        if (cacheContainer.contains(Constant.PEAR_CONFIG_CACHE_NAME)) {
            String value = cacheContainer.get(Constant.PEAR_CONFIG_CACHE_NAME, key);
            if (value != null) {
                return value;
            }
        }
        Config config = configDAO.get(key);
        if (config == null) {
            return null;
        }
        if (cacheContainer.contains(Constant.PEAR_CONFIG_CACHE_NAME)) {
            cacheContainer.put(Constant.PEAR_CONFIG_CACHE_NAME, key, config.getValue());
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
            if (cacheContainer.contains(Constant.PEAR_CONFIG_CACHE_NAME)) {
                cacheContainer.put(Constant.PEAR_CONFIG_CACHE_NAME, config.getKey(), config.getValue());
            }
        }
    }

    @Override
    public Config[] loadPublicConfigs() {
        List<Config> configs = configDAO.getConfigsWithTruePub();
        for (Config config : configs) {
            if (cacheContainer.contains(Constant.PEAR_CONFIG_CACHE_NAME)) {
                cacheContainer.put(Constant.PEAR_CONFIG_CACHE_NAME, config.getKey(), config.getValue());
            }
        }
        return configs.toArray(new Config[0]);
    }


}
