package com.certidevs;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadsTest {


    private static final int NUM_THREADS = 100_000;

    /**
     * 1. CREAR HILOS CON EXTENDS THREAD
     */
    static class CustomThread extends Thread {
        @Override
        public void run() {
            System.out.println("My name is " + this.getName() + " , state: " + this.getState());
        }
    }
    @Test
    void createWithClass() throws InterruptedException {
        Thread hilo1 = new CustomThread();
        hilo1.start();
        Thread hilo2 = new CustomThread();
        hilo2.start();

        Thread.sleep(100L);
        System.out.println("Principal: My name is " + Thread.currentThread().getName()
                + " , state: " + Thread.currentThread().getState());
    }

    /**
     * 2. CREAR HILOS CON IMPLEMENTS RUNNABLE
     */
    static class CustomRunnable implements Runnable{
        @Override
        public void run() {
            System.out.println("My name is " + Thread.currentThread().getName()
                    + " , state: " + Thread.currentThread().getState());    }
    }
    @Test
    void createWithRunnable() throws InterruptedException {
        Runnable task = new CustomRunnable();
        Thread hilo = new Thread(task);
        hilo.start();

        System.out.println("My name is " + Thread.currentThread().getName()
                + " , state: " + Thread.currentThread().getState());

    }

    /**
     * 3. HILOS CON LAMBDA RUNNABLE
     */
    @Test
    void createWithLambda() throws InterruptedException {
        Thread hilo = new Thread(() -> {
            System.out.println("My name is " + Thread.currentThread().getName()
                    + " , state: " + Thread.currentThread().getState());
        });
        hilo.start();

        System.out.println("My name is " + Thread.currentThread().getName()
                + " , state: " + Thread.currentThread().getState());
    }

    @Test
    void createWithLambdaAndName() throws InterruptedException {
        Runnable tarea = () -> {
            System.out.println("Hilo ejecutándose: " + Thread.currentThread().getName());
        };

        Thread hilo1 = new Thread(tarea, "Hilo-1");
        Thread hilo2 = new Thread(tarea, "Hilo-2");

        hilo1.start();
        hilo2.start();

        hilo1.join();
        hilo2.join();
    }
    /**
     * RUNNABLE Y FUTURE - no devuelve nada
     */
    @Test
    void runnableAndFuture() throws InterruptedException, ExecutionException {
        Runnable tarea = () -> {
            System.out.println("Hilo ejecutándose: " + Thread.currentThread().getName());
        };


        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<?> future = executor.submit(tarea);

        System.out.println("Hilo principal: " + Thread.currentThread().getName());
        System.out.println("Resultado del callable: " + future.get());

        executor.shutdown();
    }


    /**
     * CALLABLE Y FUTURE - devuelve algo
     */

    @Test
    void createWithCallableAndFuture() throws InterruptedException, ExecutionException {
         Callable<Integer> tarea = () -> {
             System.out.println("Empezando tarea Callable en hilo: " + Thread.currentThread().getName());
             Thread.sleep(1000);
                System.out.println("Terminando tarea Callable en hilo: " + Thread.currentThread().getName());
             return 1;
         };

         ExecutorService executor = Executors.newFixedThreadPool(1);
         Future<Integer> future = executor.submit(tarea);

         System.out.println("Hilo principal: " + Thread.currentThread().getName());
         System.out.println("Resultado del callable: " + future.get());

         executor.shutdown();
    }


    /**
     * EXECUTOR Y COMPLETABLE FUTURE
     */

    @Test
    void createWithExecutorAndCompletableFuture() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("Empezando tarea en hilo: " + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Terminando tarea en hilo: " + Thread.currentThread().getName());
            return 1;
        }, executor);

        System.out.println("Hilo principal: " + Thread.currentThread().getName());
        System.out.println("Resultado del callable: " + future.get());

        executor.shutdown();
    }


    // FACTORY OFplatform

    @Test
    void ofPlatformTest() throws InterruptedException, ExecutionException {
        Thread platformThread = Thread.ofPlatform()
                .name("hilo-plataforma-1")
                .unstarted(() -> {
                    System.out.println("Hola desde un hilo de plataforma!");
                    try {
                        Thread.sleep(3000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Adiós desde un hilo de plataforma!");
                });

        platformThread.start();
        platformThread.join();
    }
    @Test
    void ofVirtualTest() throws InterruptedException, ExecutionException {
        Thread virtualThread = Thread.ofVirtual()
                .name("hilo-virtual-1")
                .unstarted(() -> {
                    System.out.println("Hola desde un hilo de virtual!");
                    try {
                        Thread.sleep(3000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Adiós desde un hilo de virtual!");
                });

        virtualThread.start();
        virtualThread.join();
    }



    // 33368 ms
    @Test
    void platformThreadsCreationTimeTest() throws InterruptedException {
        long startTime = System.currentTimeMillis();

        List<Thread> threads = new ArrayList<>(NUM_THREADS);

        for (int i = 0; i < NUM_THREADS; i++) {
            Thread thread = Thread.ofPlatform()
                    .name("platform-thread-" + i)
                    .unstarted(() -> {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Tiempo de creación y ejecución de hilos de plataforma: " + duration + " ms");
    }

    // 16198 ms
    @Test
    void virtualThreadsCreationTimeTest() throws InterruptedException {
        long startTime = System.currentTimeMillis();

        List<Thread> threads = new ArrayList<>(NUM_THREADS);

        for (int i = 0; i < NUM_THREADS; i++) {
            Thread thread = Thread.ofVirtual()
                    .name("virtual-thread-" + i)
                    .unstarted(() -> {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Tiempo de creación y ejecución de hilos virtuales: " + duration + " ms");
    }

}
