package com.example.CustomerService.service;

import com.example.CustomerService.entity.Customer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class CustomerCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String PREFIX = "customer:";

    public CustomerCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheCustomer(Customer customer) {
        redisTemplate.opsForValue().set(PREFIX + customer.getId(), customer, Duration.ofHours(1));
    }

    public Customer getCachedCustomer(UUID id) {
        return (Customer) redisTemplate.opsForValue().get(PREFIX + id);
    }
}
