package cn.muzisheng.pear.config.auto;

import cn.muzisheng.pear.processor.BusinessProcessor;
import cn.muzisheng.pear.processor.DefaultBusinessProcessor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/** Registers the optional Processor capability and allows applications to replace it. */
@Configuration
public class BusinessProcessorAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(BusinessProcessor.class)
    public BusinessProcessor businessProcessor(ObjectProvider<PlatformTransactionManager> transactionManager) {
        return new DefaultBusinessProcessor(transactionManager.getIfAvailable());
    }
}
