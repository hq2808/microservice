package com.example.AuthService.service.impl;

import com.example.AuthService.dto.RegisterUserDto;
import com.example.AuthService.model.User;
import com.example.AuthService.repo.UserRepository;
import com.example.AuthService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public boolean create(RegisterUserDto registerUserDto) {

        validateUniqueUser(registerUserDto);

        String passwordHash = encoder.encode(registerUserDto.getPassword());

        User user = User.builder()
                .username(registerUserDto.getUsername())
                .email(registerUserDto.getEmail())
                .passwordHash(passwordHash)
                .status(1)
                .build();

        userRepository.save(user);
        return true;
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

    private void validateUniqueUser(RegisterUserDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
    }
}
