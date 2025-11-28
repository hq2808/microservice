package com.example.AuthService.service;

import com.example.AuthService.model.User;
import com.example.AuthService.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class UserCacheService {

    private static final String PREFIX = "USER:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public User getUser(String username) {
        Object cached = redisTemplate.opsForValue().get(PREFIX + username);
        if (cached != null) {
            return mapper.convertValue(cached, User.class);
        }

        return userRepository.findByUsername(username)
                .map(user -> {
                    redisTemplate.opsForValue().set(PREFIX + username, user, Duration.ofHours(1));
                    return user;
                })
                .orElse(null);
    }
}