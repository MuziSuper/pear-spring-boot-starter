package cn.muzisheng.pear.dao;

import cn.muzisheng.pear.entity.Config;
import cn.muzisheng.pear.mapper.ConfigMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConfigDAO {

    private final ConfigMapper configMapper;
    @Autowired
    public ConfigDAO(ConfigMapper configMapper){
        this.configMapper = configMapper;
    }
    public boolean createConfig(Config config){
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        UpdateWrapper<Config> updateWrapper = new UpdateWrapper<>();
        // 查询数据是否存在
        Config existingConfig = configMapper.selectOne(queryWrapper.eq("`key`", config.getKey()));
        if (existingConfig != null) {
            // 数据存在，更新数据
            return configMapper.updateById(config) > 0;
        } else {
            // 数据不存在，插入数据
            return configMapper.insert(config) > 0;
        }
    }

    public Config get(String key){
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        return configMapper.selectOne(queryWrapper.eq("`key`", key));
    }
    public List<Config> getConfigsWithTrueAutoload(){
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        return configMapper.selectList(queryWrapper.eq("autoload", true));
    }
    public List<Config> getConfigsWithTruePub(){
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        return configMapper.selectList(queryWrapper.eq("pub", true));
    }
}
