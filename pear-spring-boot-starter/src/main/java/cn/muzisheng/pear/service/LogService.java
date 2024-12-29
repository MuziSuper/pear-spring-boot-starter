package cn.muzisheng.pear.service;

import org.springframework.stereotype.Service;


public interface LogService {
    public void warn(String message);
    public void error(String message);
    public void info(String message);

}
