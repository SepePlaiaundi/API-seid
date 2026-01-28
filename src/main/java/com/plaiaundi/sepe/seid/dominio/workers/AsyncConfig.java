package com.plaiaundi.sepe.seid.dominio.workers;

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
        // SimpleAsyncTaskExecutor crea un hilo nuevo por cada tarea (ideal para Virtual Threads)
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("Incidencia-VT-");

        // ¡Esta es la línea mágica! Activa los hilos virtuales (Hilo Virtual != Hilo)
        executor.setVirtualThreads(true);

        return executor;
    }

}