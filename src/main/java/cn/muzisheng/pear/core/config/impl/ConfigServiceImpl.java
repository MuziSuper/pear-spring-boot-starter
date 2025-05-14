package cn.muzisheng.pear.core.config.impl;

import cn.muzisheng.pear.core.config.ConfigService;
import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.initialize.PearApplicationInitialization;
import cn.muzisheng.pear.mapper.dao.ConfigDAO;
import cn.muzisheng.pear.entity.Config;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ConfigServiceImpl implements ConfigService {
    private final Environment environment;
    private final ConfigDAO configDAO;
    private final static Logger LOG = LoggerFactory.getLogger(ConfigServiceImpl.class);

    public ConfigServiceImpl(Environment environment, ConfigDAO configDAO) {
        this.environment = environment;
        this.configDAO = configDAO;
    }

    @Override
    @Nullable
    public String getEnv(String key) {
        if (PearApplicationInitialization.EnvCache != null) {
            String value = PearApplicationInitialization.EnvCache.get(key);
            if (value != null) {
                // 刷新缓存
                PearApplicationInitialization.EnvCache.add(key, value);
                return value;
            }
            value = searchAllEnv(key);
            if (value != null) {
                PearApplicationInitialization.EnvCache.add(key, value);
                return value;
            }
        }

        return environment.getProperty(key, String.class);
    }
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getEnv(String key, Class<T> type) {
        String valueStr = getEnv(key);
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
    public boolean getBoolEnv(String key) {
        return Boolean.TRUE.equals(getEnv(key, Boolean.class));
    }

    @Override
    public int getIntEnv(String key) {
        return Optional.ofNullable(getEnv(key, Integer.class)).orElse(0);
    }

    @Override
    public void setValue(String key, String value, String format, boolean autoload, boolean pub) {
        key = key.toLowerCase();
        PearApplicationInitialization.ConfigCache.remove(key);
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
        if (PearApplicationInitialization.ConfigCache != null) {
            String value = PearApplicationInitialization.ConfigCache.get(key);
            if (value != null) {
                return value;
            }
        }
        Config config = configDAO.get(key);
        if (config == null) {
            return null;
        }
        if (PearApplicationInitialization.ConfigCache != null) {
            PearApplicationInitialization.ConfigCache.add(key, config.getValue());
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
            if (PearApplicationInitialization.ConfigCache != null) {
                PearApplicationInitialization.ConfigCache.add(config.getKey(), config.getValue());
            }
        }
    }

    @Override
    public Config[] loadPublicConfigs() {
        List<Config> configs = configDAO.getConfigsWithTruePub();
        for (Config config : configs) {
            if (PearApplicationInitialization.ConfigCache != null) {
                PearApplicationInitialization.ConfigCache.add(config.getKey(), config.getValue());
            }
        }
        return configs.toArray(new Config[0]);
    }

    /**
     * 搜索/src/Application/resources目录下所有properties文件,遍历配置项，将遍历到的配置项加载到缓存中，直到查找到对应key键的配置项，停止遍历。
     **/
    public String searchAllEnv(String key) {
        HashMap<String, String> map = new HashMap<>();
        try (Stream<Path> paths = Files.walk(Paths.get(Constant.APP_DEFAULT_SEARCH_PROPERTIES_PATH))) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".properties"))
                    .forEach(p -> {
                        try {
                            String line;
                            BufferedReader buf = new BufferedReader(new FileReader(p.toFile()));
                            while ((line = buf.readLine()) != null) {
                                line = line.trim();
                                if (line.isEmpty()) {
                                    continue;
                                }
                                if (line.startsWith("#")) {
                                    continue;
                                }
                                if (!line.contains("=")) {
                                    continue;
                                }
                                String[] properties = line.split("=", 2);
                                if (properties.length != 2) {
                                    continue;
                                }
                                properties[0] = properties[0].trim().toLowerCase();
                                properties[1] = properties[1].trim();
                                if (PearApplicationInitialization.EnvCache != null) {
                                    PearApplicationInitialization.EnvCache.add(properties[0], properties[1]);
                                }
                                if (key.equalsIgnoreCase(properties[0])) {
                                    map.put(key, properties[1]);
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (Exception e) {
            LOG.error("File cannot be accessed");
        }
        return map.get(key);
    }
}
