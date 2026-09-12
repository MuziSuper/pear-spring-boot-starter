package cn.muzisheng.pear;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that starter packaging preserves explicit activation and does not impose logging.
 */
class PearStarterContractTest {

    @Test
    void pearApplicationExplicitlyImportsFrameworkConfiguration() {
        ImportAutoConfiguration annotation = PearApplication.class.getAnnotation(ImportAutoConfiguration.class);

        assertTrue(Arrays.asList(annotation.value()).contains(PearAutoConfig.class));
    }

    @Test
    void starterResourcesDoNotOverrideHostLogging() {
        assertTrue(new ClassPathResource("static/favicon.ico").exists());
        assertTrue(new ClassPathResource("logback/pear-logback-spring.xml").exists());
    }
}
