package com.sgr.config.data;

import com.sgr.domain.UserRegistration;
import com.sgr.repositories.redis.RedisAlbumRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Profile("redis")
public class RedisConfig {

    @Bean
    public RedisAlbumRepository redisRepository(RedisTemplate<String, UserRegistration> redisTemplate) {
        return new RedisAlbumRepository(redisTemplate);
    }

    @Bean
    public RedisTemplate<String, UserRegistration> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, UserRegistration> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<UserRegistration> albumSerializer = new Jackson2JsonRedisSerializer<>(UserRegistration.class);

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(albumSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(albumSerializer);

        return template;
    }

}
