package com.eddie.reggie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 @author EddieZhang
 @create 2023-01-06 16:08
 */
@Configuration
public class RedisConfig {

    @Bean//TODO 配置redisTemplate 设置redis的key和value的序列化方式
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

        //默认的Key序列化器为：JdkSerializationRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // key序列化
        redisTemplate.setValueSerializer(new StringRedisSerializer()); // value序列化

        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

}
