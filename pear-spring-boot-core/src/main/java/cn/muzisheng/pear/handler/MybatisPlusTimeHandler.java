package cn.muzisheng.pear.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component // 必须声明为Spring组件
public class MybatisPlusTimeHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "gmt_created", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "gmt_modified", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "gmt_modified", LocalDateTime.class, LocalDateTime.now());
    }

}
