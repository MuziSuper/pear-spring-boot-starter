package cn.muzisheng.pear.dao;

import cn.muzisheng.pear.entity.Config;
import cn.muzisheng.pear.mapper.ConfigMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ConfigDAO {

    private final ConfigMapper configMapper;
    @Autowired
    public ConfigDAO(ConfigMapper configMapper){
        this.configMapper = configMapper;
    }
    /**
     * 创建或更新config,若发生冲突，则只更新value、format、autoload、public字段
     **/
    public boolean createConfigClausesPartialField(Config config){
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        // 查询数据是否存在
        Config existingConfig = configMapper.selectOne(queryWrapper.eq("`key`", config.getKey()));
        if (existingConfig != null) {
            config.setDesc(existingConfig.getDesc());
            config.setPub(existingConfig.isPub());
            config.setId(existingConfig.getId());
            // 数据存在，更新数据
            return configMapper.updateById(config) > 0;
        } else {
            // 数据不存在，插入数据
            return configMapper.insert(config) > 0;
        }
    }
    /**
     * 创建或更新config,若发生冲突，则不执行任何操作
     **/
    public boolean createConfig(Config config){
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        // 查询数据是否存在
        Config existingConfig = configMapper.selectOne(queryWrapper.eq("`key`", config.getKey()));
        if (existingConfig == null){
            return configMapper.insert(config)>0;
        }
        return false;
    }
    /**
     * 获取config
     * @param key 键
     **/
    public Config get(String key){
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        return configMapper.selectOne(queryWrapper.eq("`key`", key));
    }
    /**
     * 获取所有autoload为true的config
     **/
    public List<Config> getConfigsWithTrueAutoload(){
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        return configMapper.selectList(queryWrapper.eq("autoload", true));
    }
    /**
     * 获取所有pub为true的config
     **/
    public List<Config> getConfigsWithTruePub(){
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        return configMapper.selectList(queryWrapper.eq("pub", true));
    }
}
