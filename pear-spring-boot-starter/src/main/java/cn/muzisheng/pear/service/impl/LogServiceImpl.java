package cn.muzisheng.pear.service.impl;

import cn.muzisheng.pear.service.LogService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component

public class LogServiceImpl implements LogService {
    private static final Logger LOG= LoggerFactory.getLogger(LogServiceImpl.class);
    @Override
    public void warn(String message) {
        LOG.warn(message);
    }

    @Override
    public void error(String message) {
        LOG.error(message);
    }

    @Override
    public void info(String message) {
        LOG.info(message);
    }
}
