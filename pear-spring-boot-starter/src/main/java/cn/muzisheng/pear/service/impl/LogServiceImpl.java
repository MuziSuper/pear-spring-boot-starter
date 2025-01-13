package cn.muzisheng.pear.service.impl;

import cn.muzisheng.pear.service.LogService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class LogServiceImpl implements LogService {
    private Logger LOG;
    public void setClass(Class<?> clazz) {
        this.LOG = LoggerFactory.getLogger(clazz);
    }
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
