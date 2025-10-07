package cn.muzisheng.pear.service.Impl;

import cn.muzisheng.pear.initialize.PearApplicationInitialization;
import cn.muzisheng.pear.service.EnvService;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

public class EnvServiceImpl implements EnvService {
    private final static Logger LOG = LoggerFactory.getLogger(EnvServiceImpl.class);
    private final Environment environment;
    public EnvServiceImpl(Environment environment){
        this.environment = environment;
    }
    @Override
    @Nullable
    public String getEnv(String key) {
        if (PearApplicationInitialization.envCacheTemplate != null) {
            String value = PearApplicationInitialization.envCacheTemplate.get(key);
            if (value != null) {
                // 刷新缓存
                PearApplicationInitialization.envCacheTemplate.put(key, value);
                return value;
            }
            value = searchAllEnv(key);
            if (value != null) {
                PearApplicationInitialization.envCacheTemplate.put(key, value);
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
    /**
     * 搜索静态文件中所有properties文件,遍历配置项，将遍历到的配置项加载到缓存中，直到查找到对应key键的配置项，停止遍历。
     **/
    public String searchAllEnv(String key) {
        HashMap<String, String> map = new HashMap<>();
        try  {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:**/*.properties");
            for (Resource resource : resources) {
                try(InputStream inputStream = resource.getInputStream()) {
                    Properties properties = new Properties();
                    properties.load(inputStream);
                    for (String propertiesKey : properties.stringPropertyNames())   {
                        String propertiesValue=properties.getProperty(propertiesKey);
                        if(PearApplicationInitialization.envCacheTemplate!= null){
                            PearApplicationInitialization.envCacheTemplate.put(key, propertiesValue);
                        }
                        if(propertiesKey.equalsIgnoreCase(key)){
                            map.put(key, propertiesValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("File cannot be accessed");
        }
        return map.get(key);
    }
}
