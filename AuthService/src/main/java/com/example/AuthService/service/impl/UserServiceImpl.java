package com.example.AuthService.service.impl;

import com.example.AuthService.dto.RegisterUserDto;
import com.example.AuthService.exception.TooManyRequestsException;
import com.example.AuthService.model.User;
import com.example.AuthService.repo.UserRepository;
import com.example.AuthService.service.UserService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedissonClient redissonClient;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public boolean create(RegisterUserDto dto) {
        String lockKey = "lock:user:" + dto.getUsername() + ":" + dto.getEmail();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // Cố gắng lấy lock, timeout = 5s, lease = 10s
            boolean acquired = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!acquired) {
                System.out.println("Another request is registering this user, try again later");
                throw new TooManyRequestsException("User đang được đăng ký, thử lại sau");
            }

            // Kiểm tra unique trước khi save
            validateUniqueFields(dto);

            // Hash password
            String passwordHash = encoder.encode(dto.getPassword());

            User user = User.builder()
                    .username(dto.getUsername())
                    .email(dto.getEmail())
                    .passwordHash(passwordHash)
                    .status(1)
                    .build();

            try {
                userRepository.save(user);
                return true;
            } catch (DataIntegrityViolationException e) {
                // Fallback cho trường hợp race condition vẫn xảy ra
                System.out.println("Fallback cho trường hợp race condition vẫn xảy ra");
                throw e;
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // reset interrupted flag
            System.out.println("Lock interrupted: " + e.getMessage());
            throw new RuntimeException("Lock interrupted", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                System.out.println("un lock");
                lock.unlock();
            }
        }
    }

    @Override
    public void generateUsers() {
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
                    batch.add(new User(username, password, username + "@gmail.com"));

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

    private void validateUniqueFields(RegisterUserDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
    }
}
