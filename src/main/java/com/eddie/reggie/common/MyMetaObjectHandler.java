package com.eddie.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * //TODO Mybatis提供的公共字段自动填充接口 --元数据对象处理器
 * 自定义元数据处理器并实现MetaObjectHandler接口
 * 在需要自动填充的公共字段上加上@TableField()注解 并指定fill填充规则
 @author EddieZhang
 @create 2023-01-02 16:34
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入操作的填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        //TODO 将ThreadLocal中的id获取
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 更新操作的填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]");
        metaObject.setValue("updateTime", LocalDateTime.now());
        //TODO 将ThreadLocal中的id获取
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
