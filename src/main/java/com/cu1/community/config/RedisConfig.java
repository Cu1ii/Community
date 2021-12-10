package com.cu1.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        //实例化 Bean
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        //设置 key 的序列化方式
        template.setKeySerializer(RedisSerializer.string());

        //设置 vaule 的序列化方式
        template.setValueSerializer(RedisSerializer.json());

        //设置 hash 的 key 的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());

        //设置 hash 的 vaule 的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());
        //触发生效
        template.afterPropertiesSet();

        return template;
    }


}
