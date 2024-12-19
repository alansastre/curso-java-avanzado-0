package com.certidevs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


/*
Si no creamos Bean Executor o TaskExecutor Spring usará el SimpleAsyncTaskExecutor
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Usar VisualVM y JMC para entender bien qué configuración es la mejor para nosotros
     */
//    @Bean("prueba")
    @Bean()
    public Executor taskExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("CertiDevs-");
        // Java 21 Hilos virtuales
        // executor.setVirtualThreads(true);
        executor.initialize();
        return executor;
    }

    // Aquí se pueden crear varios Bean Executor, y desde @Async usarlos por el nombre
}
