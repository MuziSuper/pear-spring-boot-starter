package cn.muzisheng.pear.utils;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.service.ConfigService;
import cn.muzisheng.pear.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

@Slf4j
@Component
public class ReadProperties {

    private final LogService logger;
    @Autowired
    public ReadProperties(LogService logger) {
        this.logger=logger;
    }
    /**
     * 搜索/src/main/resources目录下所有properties文件,并返回键值对
     **/
    public Map<String,String> loadAllProperties() {
        HashMap<String,String> map = new HashMap<>();
        try(Stream<Path> paths=Files.walk(Paths.get(Constant.APP_DEFAULT_SEARCH_PROPERTIES_PATH))){
                paths.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".properties"))
                .forEach(p -> {
                    try {
                        Properties properties = new Properties();
                        properties.load(Files.newInputStream(p));
                        properties.forEach((k, v) -> {
                            map.put(k.toString(), v.toString());
                        });
                    } catch (Exception e) {
                        logger.error("The properties file is incorrectly formatted and located in "+p.getFileName().toString());
                    }
                });
        } catch(Exception e){
            logger.error("File cannot be accessed");
        }
        return map;
    }
}
