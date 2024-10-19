package com.example.car_management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // Số luồng chính
        executor.setMaxPoolSize(50);  // Số luồng tối đa
        executor.setQueueCapacity(100); // Số lượng nhiệm vụ chờ trong hàng đợi
        executor.setThreadNamePrefix("AsyncEmailThread-");
        executor.initialize();
        return executor;
    }
}
