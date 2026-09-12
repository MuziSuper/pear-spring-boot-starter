package cn.muzisheng.pear.config.auto;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PearSqlInitEnvironmentPostProcessorTest {

    @Test
    void mysqlJdbcUrlEnablesSqlInitializationByDefault() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/pear");

        postProcess(environment);

        assertTrue(PearSqlInitEnvironmentPostProcessor.isSqlInitEnabled(environment));
        assertEquals("always", environment.getProperty("spring.sql.init.mode"));
        assertEquals(PearSqlInitEnvironmentPostProcessor.MYSQL_SCHEMA_LOCATION,
                environment.getProperty("spring.sql.init.schema-locations"));
    }

    @Test
    void explicitFalseDisablesSqlInitializationEvenForMysql() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/pear")
                .withProperty(PearSqlInitEnvironmentPostProcessor.SQL_INIT_ENABLED_PROPERTY, "false");

        postProcess(environment);

        assertFalse(PearSqlInitEnvironmentPostProcessor.isSqlInitEnabled(environment));
        assertEquals(null, environment.getProperty("spring.sql.init.mode"));
        assertEquals(null, environment.getProperty("spring.sql.init.schema-locations"));
    }

    @Test
    void explicitTrueEnablesSqlInitializationForNonMysqlUrl() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/pear")
                .withProperty(PearSqlInitEnvironmentPostProcessor.SQL_INIT_ENABLED_PROPERTY, "true");

        postProcess(environment);

        assertTrue(PearSqlInitEnvironmentPostProcessor.isSqlInitEnabled(environment));
        assertEquals("always", environment.getProperty("spring.sql.init.mode"));
        assertEquals(PearSqlInitEnvironmentPostProcessor.MYSQL_SCHEMA_LOCATION,
                environment.getProperty("spring.sql.init.schema-locations"));
    }

    @Test
    void userSqlInitPropertiesAreNotOverridden() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/pear")
                .withProperty("spring.sql.init.mode", "never")
                .withProperty("spring.sql.init.schema-locations", "classpath:custom.sql");

        postProcess(environment);

        assertEquals("never", environment.getProperty("spring.sql.init.mode"));
        assertEquals("classpath:custom.sql", environment.getProperty("spring.sql.init.schema-locations"));
    }

    private void postProcess(MockEnvironment environment) {
        new PearSqlInitEnvironmentPostProcessor().postProcessEnvironment(environment, null);
    }
}
