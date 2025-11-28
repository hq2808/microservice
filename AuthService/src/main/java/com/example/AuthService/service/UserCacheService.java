package com.example.AuthService.service;

import com.example.AuthService.model.User;
import com.example.AuthService.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class UserCacheService {

    private static final String PREFIX = "USER:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User getUser(String username) {
        Object cached = redisTemplate.opsForValue().get(PREFIX + username);
        if (cached != null) return (User) cached;

        return userRepository.findByUsername(username)
                .map(user -> {
                    redisTemplate.opsForValue().set(PREFIX + username, user, Duration.ofHours(1));
                    return user;
                })
                .orElse(null);
    }

//    public void generateUsers() {
//        int batchSize = 10_000;
//        List<User> users = new ArrayList<>(batchSize);
//        String password = encoder.encode("123456");
//        for (int i = 30001; i <= 1_000_000; i++) {
//            String username = "user" + i;
//            users.add(new User(username, password));
//            if (i % batchSize == 0) {
//                userRepository.saveAll(users);
//                users.clear();
//                System.out.println("✅ Inserted " + i + " users...");
//            }
//        }
//
//        // Lưu nốt phần dư
//        if (!users.isEmpty()) {
//            userRepository.saveAll(users);
//            System.out.println("✅ Inserted remaining users (" + users.size() + ")");
//        }
//
//        System.out.println("🎯 Done generating 1,000,000 users!");
//    }
}