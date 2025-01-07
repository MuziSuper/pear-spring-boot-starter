package cn.muzisheng.pear.serviceTest;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.dao.ConfigDAO;
import cn.muzisheng.pear.entity.Config;
import cn.muzisheng.pear.initialize.ApplicationInitialization;
import cn.muzisheng.pear.service.LogService;
import cn.muzisheng.pear.service.impl.ConfigServiceImpl;
import cn.muzisheng.pear.utils.ExpiredCache;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ConfigServiceTests {

    @Mock
    private Environment environment;
    private final Logger LOG= LoggerFactory.getLogger(ConfigServiceTests.class);
    @MockBean
    private ConfigDAO configDAO;
    @InjectMocks
    private ConfigServiceImpl configService;
    @Autowired
    private ConfigServiceImpl configServiceImpl;

    @BeforeEach
    public void setUp() {
        ApplicationInitialization.EnvCache=new ExpiredCache<String,String>().newExpiredCache(Constant.CACHE_EXPIRED);
        ApplicationInitialization.ConfigCache = new ExpiredCache<String,String>().newExpiredCache(Constant.CACHE_EXPIRED);
    }
    @AfterEach
    public void tearDown() {
        try {
            File propertiesFile = new File("src/main/resources/test.properties");
            if (propertiesFile.exists()) {
                propertiesFile.delete();
            }
        }catch(SecurityException e){
            LOG.error("SecurityException: {}", e.getMessage());
        }
    }
    @Nested
    @DisplayName("GetEnvTest")
    class GetEnvTests {
        @Test
        @Transactional
        public void getEnv_CacheHit_ReturnsCachedValue() {
            ApplicationInitialization.EnvCache.add("key", "cachedValue");
            String result = configService.getEnv("key");
            assertEquals("cachedValue", result);
        }

        @Test
        @Transactional
        public void getEnv_Correct_ReturnsEnvValue() throws IOException {
            File propertiesFile = new File("src/main/resources/test.properties");
            try (FileWriter writer = new FileWriter(propertiesFile)) {
                writer.write("key=fileValue");
            }
            String result = configService.getEnv("key");
            assertEquals("fileValue", result);
            assertEquals("fileValue", ApplicationInitialization.EnvCache.get("key"));
        }

        @Test
        @Transactional
        public void getEnv_Error_ReturnsEnvValue() throws IOException {
            File propertiesFile = new File("src/main/resources/test.properties");
            try (FileWriter writer = new FileWriter(propertiesFile)) {
                writer.write("""
                        key1 == value1
                        key2== value2
                        key3 ==value3
                        key4 value4
                        key5 
                        key6 - value6
                        key7 = value7=
                        #key8=value8
                            
                        """);
            }
            assertEquals("= value1", configService.getEnv("key1"));
            assertEquals("= value1", ApplicationInitialization.EnvCache.get("key1"));
            assertEquals("= value2", configService.getEnv("key2"));
            assertEquals("= value2", ApplicationInitialization.EnvCache.get("key2"));
            assertEquals("=value3", configService.getEnv("key3"));
            assertEquals("=value3", ApplicationInitialization.EnvCache.get("key3"));
            assertNull(configService.getEnv("key4"));
            assertNull(ApplicationInitialization.EnvCache.get("key4"));
            assertNull(configService.getEnv("key5"));
            assertNull(ApplicationInitialization.EnvCache.get("key5"));
            assertNull(configService.getEnv("key6"));
            assertNull(ApplicationInitialization.EnvCache.get("key6"));
            assertEquals("value7=", configService.getEnv("key7"));
            assertEquals("value7=", ApplicationInitialization.EnvCache.get("key7"));
            assertNull(configService.getEnv("key8"));
            assertNull(ApplicationInitialization.EnvCache.get("key8"));
        }
    }
    @Nested
    @DisplayName("GetBoolEnvTest")
    class GetBoolEnvTests {

        @Transactional
        @Test
        public void getBoolEnv_Normal() throws Exception{
            File propertiesFile = new File("src/main/resources/test.properties");
            try (FileWriter writer = new FileWriter(propertiesFile)) {
                writer.write("boolKey=true");
            }
            boolean flag = configService.getBoolEnv("boolKey");
            assertTrue(flag);
            assertEquals("true", ApplicationInitialization.EnvCache.get("boolKey"));
        }
        @Transactional
        @Test
        public void getBoolEnv_Error() throws Exception{
            File propertiesFile = new File("src/main/resources/test.properties");
            try (FileWriter writer = new FileWriter(propertiesFile)) {
                writer.write("boolKey=tre");
            }
            boolean flag = configService.getBoolEnv("boolKey");
            assertFalse(flag);
            assertEquals("tre", ApplicationInitialization.EnvCache.get("boolKey"));
        }
    }
    @Nested
    @Transactional
    @DisplayName("GetIntegerEnvTest")
    class GetIntegerEnvTests {
        @Test
        public void getBoolEnv_Normal() throws Exception{
            File propertiesFile = new File("src/main/resources/test.properties");
            try (FileWriter writer = new FileWriter(propertiesFile)) {
                writer.write("boolKey=true");
            }
            boolean flag = configService.getBoolEnv("boolKey");
            assertTrue(flag);
            assertEquals("true", ApplicationInitialization.EnvCache.get("boolKey"));
        }
        @Test
        public void getBoolEnv_Error() throws Exception{
            File propertiesFile = new File("src/main/resources/test.properties");
            try (FileWriter writer = new FileWriter(propertiesFile)) {
                writer.write("boolKey=tre");
            }
            boolean flag = configService.getBoolEnv("boolKey");
            assertFalse(flag);
            assertEquals("tre", ApplicationInitialization.EnvCache.get("boolKey"));
        }
    }
    @Nested
    @Transactional
    @DisplayName("SetValueTest")
    class SetValueTests {

        @Test
        public void setValue_Normal() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            when(configDAO.createConfigClausesPartialField(any(Config.class))).thenReturn(true);
            Config config = new Config();
            config.setId(1000L);
            config.setKey("key");
            config.setValue("value");
            config.setFormat("string");
            config.setAutoload(true);
            config.setPub(true);
            assertDoesNotThrow(()->{
                configService.setValue(config.getKey(),config.getValue(),config.getFormat(),config.isAutoload(),config.isPub());
            });
        }
    }
    @Nested
    @Transactional
    @DisplayName("getValueTest")
    class GetValueTests {

        @Test
        public void getValue_Normal_ConfigDAO_Get_Null() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            when(configDAO.get("key")).thenReturn(null);
            assertNull(configService.getValue("key"));
        }

        @Test
        public void getValue_Normal_ConfigCache_Get_Value() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            ApplicationInitialization.ConfigCache.add("key","value");
            assertEquals("value", configService.getValue("key"));
        }
        @Test
        public void getValue_Normal_ConfigDAO_Get_Value() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            Config config = new Config();
            config.setId(1000L);
            config.setKey("key");
            config.setValue("value");
            when(configDAO.get("key")).thenReturn(config);
            assertEquals("value", configService.getValue("key"));
            assertEquals("value", ApplicationInitialization.ConfigCache.get("key"));
        }
    }
    @Nested
    @Transactional
    @DisplayName("getIntegerValueTest")
    class GetIntegerValueTests {
        @Test
        public void getIntegerValue_Normal() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            when(configService.getValue("key")).thenReturn("123");
            assertEquals(123, configService.getIntValue("key", 0));
        }

        @Test
        public void getIntegerValue_Error_Null() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            when(configService.getValue("key")).thenReturn(null);
            int defaultValue = 0;
            assertEquals(0, configService.getIntValue("key", defaultValue));
        }
        @Test
        public void getValue_Normal_Error_type() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            when(configService.getValue("key")).thenReturn("hello world");
            int defaultValue = 0;
            assertEquals(0, configService.getIntValue("key", defaultValue));
        }
    }
}
