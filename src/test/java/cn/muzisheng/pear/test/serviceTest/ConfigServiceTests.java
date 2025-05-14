package cn.muzisheng.pear.test.serviceTest;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.initialize.PearApplicationInitialization;
import cn.muzisheng.pear.mapper.dao.ConfigDAO;
import cn.muzisheng.pear.entity.Config;
import cn.muzisheng.pear.core.config.impl.ConfigServiceImpl;
import cn.muzisheng.pear.test.TestApplication;
import cn.muzisheng.pear.utils.ExpiredCacheFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TestApplication.class)
@ExtendWith(MockitoExtension.class)
public class ConfigServiceTests {

    @Mock
    private Environment environment;
    private final Logger LOG= LoggerFactory.getLogger(ConfigServiceTests.class);
    @MockBean
    private ConfigDAO configDAO;
    @InjectMocks
    private ConfigServiceImpl configService;

    @BeforeEach
    public void setUp() {
        PearApplicationInitialization.EnvCache= ExpiredCacheFactory.newExpiredCacheFactory(Constant.CACHE_CAPACITY,Constant.CACHE_EXPIRED);
        PearApplicationInitialization.ConfigCache = ExpiredCacheFactory.newExpiredCacheFactory(Constant.CACHE_CAPACITY,Constant.CACHE_EXPIRED);
    }
    @AfterEach
    public void tearDown() {
        try {
            File propertiesFile = new File("src/Application/resources/test.properties");
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
            PearApplicationInitialization.EnvCache.add("key", "cachedValue");
            String result = configService.getEnv("key");
            assertEquals("cachedValue", result);
        }

        @Test
        @Transactional
        public void getEnv_Correct_ReturnsEnvValue() throws IOException {
            File propertiesFile = new File("src/Application/resources/test.properties");
            try (FileWriter writer = new FileWriter(propertiesFile)) {
                writer.write("key=fileValue");
            }
            String result = configService.getEnv("key");
            assertEquals("fileValue", result);
            assertEquals("fileValue", PearApplicationInitialization.EnvCache.get("key"));
        }

        @Test
        @Transactional
        public void getEnv_Error_ReturnsEnvValue() throws IOException {
            File propertiesFile = new File("src/Application/resources/test.properties");
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
            assertEquals("= value1", PearApplicationInitialization.EnvCache.get("key1"));
            assertEquals("= value2", configService.getEnv("key2"));
            assertEquals("= value2", PearApplicationInitialization.EnvCache.get("key2"));
            assertEquals("=value3", configService.getEnv("key3"));
            assertEquals("=value3", PearApplicationInitialization.EnvCache.get("key3"));
            assertNull(configService.getEnv("key4"));
            assertNull(PearApplicationInitialization.EnvCache.get("key4"));
            assertNull(configService.getEnv("key5"));
            assertNull(PearApplicationInitialization.EnvCache.get("key5"));
            assertNull(configService.getEnv("key6"));
            assertNull(PearApplicationInitialization.EnvCache.get("key6"));
            assertEquals("value7=", configService.getEnv("key7"));
            assertEquals("value7=", PearApplicationInitialization.EnvCache.get("key7"));
            assertNull(configService.getEnv("key8"));
            assertNull(PearApplicationInitialization.EnvCache.get("key8"));
        }
    }
    @Nested
    @DisplayName("GetBoolEnvTest")
    class GetBoolEnvTests {

        @Transactional
        @Test
        public void getBoolEnv_Normal() throws Exception{
            File propertiesFile = new File("src/Application/resources/test.properties");
            try (FileWriter writer = new FileWriter(propertiesFile)) {
                writer.write("boolKey=true");
            }
            boolean flag = configService.getBoolEnv("boolKey");
            assertTrue(flag);
            assertEquals("true", PearApplicationInitialization.EnvCache.get("boolKey"));
        }
        @Transactional
        @Test
        public void getBoolEnv_Error() throws Exception{
            File propertiesFile = new File("src/Application/resources/test.properties");
            try (FileWriter writer = new FileWriter(propertiesFile)) {
                writer.write("boolKey=tre");
            }
            boolean flag = configService.getBoolEnv("boolKey");
            assertFalse(flag);
            assertEquals("tre", PearApplicationInitialization.EnvCache.get("boolKey"));
        }
    }
    @Nested
    @Transactional
    @DisplayName("GetIntegerEnvTest")
    class GetIntegerEnvTests {
        @Test
        public void getBoolEnv_Normal() throws Exception{
            File propertiesFile = new File("src/Application/resources/test.properties");
            try (FileWriter writer = new FileWriter(propertiesFile)) {
                writer.write("boolKey=true");
            }
            boolean flag = configService.getBoolEnv("boolKey");
            assertTrue(flag);
            assertEquals("true", PearApplicationInitialization.EnvCache.get("boolKey"));
        }
        @Test
        public void getBoolEnv_Error() throws Exception{
            File propertiesFile = new File("src/Application/resources/test.properties");
            try (FileWriter writer = new FileWriter(propertiesFile)) {
                writer.write("boolKey=tre");
            }
            boolean flag = configService.getBoolEnv("boolKey");
            assertFalse(flag);
            assertEquals("tre", PearApplicationInitialization.EnvCache.get("boolKey"));
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
            PearApplicationInitialization.ConfigCache.add("key","value");
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
            assertEquals("value", PearApplicationInitialization.ConfigCache.get("key"));
        }
    }
    @Nested
    @Transactional
    @DisplayName("getIntegerValueTest")
    class GetIntegerValueTests {
        @Test
        public void getIntegerValue_Normal_Null() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            when(configDAO.get("key")).thenReturn(null);
            assertEquals(1, configService.getIntValue("key", 1));
        }

        @Test
        public void getIntegerValue_Normal_Value() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            Config config = new Config();
            config.setValue("2");
            when(configDAO.get("key")).thenReturn(config);
            assertEquals(2, configService.getIntValue("key", 1));
        }
        @Test
        public void getIntegerValue_Error_Type() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            Config config = new Config();
            config.setKey("key");
            config.setValue("hello world");
            when(configDAO.get("key")).thenReturn(config);
            assertEquals(1, configService.getIntValue("key", 1));
        }
    }
    @Nested
    @Transactional
    @DisplayName("getIntegerValueTest")
    class GetBoolValueTests {
        @Test
        public void getBoolValue_Normal_Null() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            when(configDAO.get("key")).thenReturn(null);
            assertFalse(configService.getBoolValue("key"));
        }

        @Test
        public void getBoolValue_Normal_Value() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            Config config = new Config();
            config.setValue("true");
            when(configDAO.get("key")).thenReturn(config);
            assertTrue(configService.getBoolValue("key"));
        }
        @Test
        public void getBoolValue_Error_Type() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            Config config = new Config();
            config.setKey("key");
            config.setValue("hello world");
            when(configDAO.get("key")).thenReturn(config);
            assertFalse(configService.getBoolValue("key"));
        }
    }
    @Nested
    @Transactional
    @DisplayName("getIntegerValueTest")
    class CheckValueTests {
        @Test
        public void checkValue_Normal() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            when(configDAO.createConfig(any(Config.class))).thenReturn(false);
            configService.checkValue("key","value","string",true,true);
            verify(configDAO).createConfig(any());
        }
    }
    @Nested
    @Transactional
    @DisplayName("getIntegerValueTest")
    class LoadAutoLoadsTests {
        @Test
        public void loadAutoLoads_Normal() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            ArrayList<Config> configList = new ArrayList<>();
            Config config = new Config();
            config.setId(1000L);
            config.setKey("key");
            config.setValue("value");
            config.setAutoload(true);
            configList.add(config);
            when(configDAO.getConfigsWithTrueAutoload()).thenReturn(configList);
            configService.loadAutoLoads();
            assertEquals("value", PearApplicationInitialization.ConfigCache.get("key"));
        }
        @Test
        public void loadAutoLoads_Error_Null() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            ArrayList<Config> configList = new ArrayList<>();
            Config config = new Config();
            config.setId(1000L);
            config.setKey("key");
            config.setValue("value");
            config.setAutoload(false);
            configList.add(config);
            when(configDAO.getConfigsWithTrueAutoload()).thenReturn(configList);
            configService.loadAutoLoads();
            assertEquals("value", PearApplicationInitialization.ConfigCache.get("key"));
        }
    }
    @Nested
    @Transactional
    @DisplayName("getIntegerValueTest")
    class LoadPublicConfigsTests {
        @Test
        public void loadPublicConfigs_Normal() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            ArrayList<Config> configList = new ArrayList<>();
            Config config = new Config();
            config.setId(1000L);
            config.setKey("key");
            config.setValue("value");
            config.setAutoload(true);
            config.setPub(true);
            configList.add(config);
            Config config2 = new Config();
            config2.setId(1001L);
            config2.setKey("key2");
            config2.setValue("value2");
            config2.setAutoload(true);
            config2.setPub(true);
            configList.add(config2);
            when(configDAO.getConfigsWithTruePub()).thenReturn(configList);
            assertEquals(2, configService.loadPublicConfigs().length);
        }
        @Test
        public void loadPublicConfigs_Error_Null() throws Exception{
            configService=spy(new ConfigServiceImpl(environment, configDAO));
            ArrayList<Config> configList = new ArrayList<>();
            Config config = new Config();
            config.setId(1000L);
            config.setKey("key");
            config.setValue("value");
            config.setAutoload(true);
            config.setPub(true);
            configList.add(config);
            Config config2 = new Config();
            config2.setId(1001L);
            config2.setKey("key2");
            config2.setValue("value2");
            config2.setAutoload(true);
            config2.setPub(false);
            configList.add(config2);
            when(configDAO.getConfigsWithTruePub()).thenReturn(configList);
            Config[] res= configService.loadPublicConfigs();
            assertTrue(List.of(res).contains(config));
            assertTrue(PearApplicationInitialization.ConfigCache.contains("key"));
        }
    }
}
