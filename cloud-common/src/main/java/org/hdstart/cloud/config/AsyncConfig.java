package org.hdstart.cloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数，线程池维护线程的最小数量
        executor.setCorePoolSize(4);

        // 最大线程数，线程池允许的最大线程数
        executor.setMaxPoolSize(8);

        // 队列容量，任务缓存队列容量
        executor.setQueueCapacity(50);

        // 线程空闲后的最大存活时间，单位秒
        executor.setKeepAliveSeconds(60);

        // 线程名称前缀，方便调试
        executor.setThreadNamePrefix("MyExecutor-");

        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 初始化
        executor.initialize();
        return executor;
    }
}
