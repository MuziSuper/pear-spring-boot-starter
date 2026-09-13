package cn.muzisheng.pear;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that starter packaging preserves explicit activation and default logging resources.
 */
class PearStarterContractTest {

    @Test
    void pearApplicationExplicitlyImportsFrameworkConfiguration() {
        ImportAutoConfiguration annotation = PearApplication.class.getAnnotation(ImportAutoConfiguration.class);

        assertTrue(Arrays.asList(annotation.value()).contains(PearAutoConfig.class));
    }

    @Test
    void starterResourcesContainDefaultLoggingConfiguration() {
        assertTrue(new ClassPathResource("static/favicon.ico").exists());
        assertTrue(new ClassPathResource("logback-spring.xml").exists());
    }
}
