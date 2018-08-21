package com.creativedrive.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;

@Configuration
public class ThreadPoolConfig {

    @Value("${app.config.threads.min:10}")
    private int threadPoolMin;

    @Value("${app.config.threads.max:30}")
    private int threadPoolMax;

    @PostConstruct
    void postConstruct() {
        /*
           Avoid misconfiguration
        */
        if (threadPoolMax > 500 || threadPoolMax < 0) {
            threadPoolMax = 30;
        }

        if (threadPoolMin > 500 || threadPoolMin < 0) {
            threadPoolMin = 10;
        }

        if (threadPoolMin > threadPoolMax) {
            threadPoolMin = threadPoolMax;
        }
    }

    /**
     * Factory for thread pool used to support async execution.
     *
     * @return {@link org.springframework.core.task.TaskExecutor}
     */
    @Bean
    public TaskExecutor futureExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // Pool bounds
        taskExecutor.setCorePoolSize(threadPoolMin);
        taskExecutor.setMaxPoolSize(threadPoolMax);
        // Log friendly
        taskExecutor.setThreadGroupName("API-");
        // Initialize and return
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

}
