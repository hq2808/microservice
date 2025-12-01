package com.example.AuthService.service;

import com.example.AuthService.dto.RegisterUserDto;
import com.example.AuthService.model.User;
import com.example.AuthService.repo.UserRepository;
import com.example.AuthService.service.impl.UserServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;  // Service thật

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock lock;

    @Test
    void testConcurrentCreate_DistributedLock() throws Exception {

        // ===== Mock lock =====
        Mockito.when(redissonClient.getLock(Mockito.anyString()))
                .thenReturn(lock);

        AtomicBoolean lockTaken = new AtomicBoolean(false);

        // Chỉ đúng 1 thread lấy được lock
        Mockito.when(lock.tryLock(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenAnswer(invocation -> lockTaken.compareAndSet(false, true));

        Mockito.when(lock.isHeldByCurrentThread()).thenReturn(true);

        // ===== Mock userRepository.save =====
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // ===== Prepare DTO =====
        RegisterUserDto dto = new RegisterUserDto();
        dto.setUsername("distUser");
        dto.setEmail("dist@example.com");
        dto.setPassword("123456");

        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<String>> results = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            int id = i;
            results.add(executor.submit(() -> {
                try {
                    boolean ok = userService.create(dto);
                    return "T" + id + " SUCCESS";
                } catch (Exception e) {
                    return "T" + id + " FAIL";
                }
            }));
        }

        executor.shutdown();
        executor.awaitTermination(20, TimeUnit.SECONDS);

        int success = 0;
        int fail = 0;

        for (Future<String> f : results) {
            String r = f.get();
            System.out.println(r);
            if (r.contains("SUCCESS")) success++;
            else fail++;
        }

        System.out.println("Success = " + success);
        System.out.println("Fail = " + fail);

        // ===== Assertions =====
        Assertions.assertEquals(1, success, "Chỉ 1 thread được quyền create user");
        Assertions.assertEquals(threadCount - 1, fail, "Còn lại phải FAIL do lock");
    }
}