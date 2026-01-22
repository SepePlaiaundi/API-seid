package com.plaiaundi.sepe.seid.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    @Bean(name = "hilosCamaras")
    public Executor poolHilosSincronizacionCamaras() {
        // SimpleAsyncTaskExecutor crea un hilo nuevo por cada tarea (ideal para Virtual Threads)
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("Camara-VT-");

        // ¡Esta es la línea mágica! Activa los hilos virtuales (Hilo Virtual != Hilo)
        executor.setVirtualThreads(true);

        return executor;
    }

    @Bean(name = "hilosIncidencias")
    public Executor poolHilosSincronizacionIncidencias() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 10 hilos trabajando a la vez min
        executor.setMaxPoolSize(50);  // Hasta 50 si hay mucha carga
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Incidencias-Worker-");
        executor.initialize();
        return executor;
    }

}