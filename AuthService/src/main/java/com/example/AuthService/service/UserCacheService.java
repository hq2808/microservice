package com.example.AuthService.service;

import com.example.AuthService.model.User;
import com.example.AuthService.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public void generateUsersMultiThread() {
        int totalUsers = 1_000_000;
        int batchSize = 10_000;
        int numThreads = 8; // tùy CPU, có thể 4 hoặc 8
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        String password = encoder.encode("123456");

        // Mỗi thread phụ trách 125_000 user (với 8 thread)
        int usersPerThread = totalUsers / numThreads;

        for (int t = 0; t < numThreads; t++) {
            int start = t * usersPerThread + 1;
            int end = (t == numThreads - 1) ? totalUsers : (t + 1) * usersPerThread;

            executor.submit(() -> {
                List<User> batch = new ArrayList<>(batchSize);
                for (int i = start; i <= end; i++) {
                    String username = "username" + i;
                    batch.add(new User(username, password));

                    if (batch.size() >= batchSize) {
                        userRepository.saveAll(batch);
                        batch.clear();
                        System.out.println(Thread.currentThread().getName() + " ✅ inserted up to " + i);
                    }
                }

                if (!batch.isEmpty()) {
                    userRepository.saveAll(batch);
                    System.out.println(Thread.currentThread().getName() + " ✅ inserted remaining " + batch.size());
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // đợi tất cả thread kết thúc
        }

        System.out.println("🎯 Done generating 1,000,000 users with multithreading!");
    }
}