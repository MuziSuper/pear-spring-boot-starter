package cn.muzisheng.pear.config.auto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * globalCrossDomainConfiguration
 */
@Configuration
public class CorsAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(CorsAutoConfiguration.class);

    @Bean
    public CorsFilter CorsFilter() {
        LOG.info("CorsFilter默认注册完成");
        CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
        config.setAllowCredentials(true);
        // Set the access source address
        config.addAllowedOriginPattern("*");
        // Set the access source request header
        config.addAllowedHeader("*");
        // Set the access source request method
        config.addAllowedMethod("*");
        // Valid 1800 seconds
        // config.setMaxAge(1800L);
        // Add mapping paths and intercept all requests
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        // Return the new Cors Filter
        return new CorsFilter(source);
    }
}
