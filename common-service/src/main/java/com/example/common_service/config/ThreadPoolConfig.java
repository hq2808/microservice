package com.example.common_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ThreadPoolConfig {
//    @Bean(name = "authExecutor")
//    public ExecutorService executorService() {
//        int core = Math.max(4, Runtime.getRuntime().availableProcessors());
//        return new ThreadPoolExecutor(core, core * 4,
//                60L, TimeUnit.SECONDS,
//                new LinkedBlockingQueue<>(200000),
//                new ThreadPoolExecutor.CallerRunsPolicy());
//    }

    @Bean(name = "threadPoolTask")
    public Executor authExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);      // số thread hoạt động thường xuyên
        executor.setMaxPoolSize(100);      // tối đa thread song song
        executor.setQueueCapacity(500);    // hàng đợi chờ request
        executor.setThreadNamePrefix("AuthAsync-");
        executor.initialize();
        return executor;
    }
}