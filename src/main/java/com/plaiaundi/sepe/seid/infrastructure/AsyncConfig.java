package com.plaiaundi.sepe.seid.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    @Bean(name = "hilosCamaras")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 10 hilos trabajando a la vez min
        executor.setMaxPoolSize(50);  // Hasta 50 si hay mucha carga
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("CamaraThread-");
        executor.initialize();
        return executor;
    }
}