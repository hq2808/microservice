package com.example.AuthService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {
    @Bean(name = "authExecutor")
    public ExecutorService executorService() {
        int core = Math.max(4, Runtime.getRuntime().availableProcessors());
        return new ThreadPoolExecutor(core, core * 4,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(200000),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}