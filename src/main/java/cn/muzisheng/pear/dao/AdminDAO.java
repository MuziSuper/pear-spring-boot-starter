package cn.muzisheng.pear.dao;

import cn.muzisheng.pear.mapper.AdminMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AdminDAO<T> {
    private final AdminMapper<T> adminMapper;
    public AdminDAO(AdminMapper<T> adminMapper) {
        this.adminMapper = adminMapper;
    }
    public T getFirst(Map<String,Object> keys) {
        return adminMapper.selectByMap(keys).getFirst();
    }
}
