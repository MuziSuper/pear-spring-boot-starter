package cn.muzisheng.pear.service;



public interface LogService {
    void warn(String message);
    void error(String message);
    void info(String message);

}
