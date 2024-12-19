package com.certidevs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

public class FutureAndCompletableFutureTests {


    /**
     * Test 1: Uso básico de Future con ExecutorService
     * Concepto: Mostrar cómo obtener un resultado en el futuro usando Future.
     * Limitación: Necesitamos bloquear el hilo llamando a get().
     */
    @Test
    void testBasicFuture() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(() -> {
            // Simulamos una tarea costosa
            Thread.sleep(500);
            return 42;
        });

        // Como la tarea es asincrónica, podemos hacer otras cosas mientras,
        // pero eventualmente necesitamos obtener el resultado.
        Integer result = future.get(); // Bloquea hasta que se complete
        Assertions.assertEquals(42, result);
        executor.shutdown();
    }

    /**
     * Test 2: Uso básico de CompletableFuture
     * Concepto: Iniciar un CompletableFuture de manera explícita y completarlo manualmente.
     */
    @Test
    void testManualCompletableFutureCompletion() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cf = new CompletableFuture<>();

        // Podríamos completar la operación más tarde
        new Thread(() -> {
            try {
                Thread.sleep(200);
                cf.complete("Hola Mundo");
            } catch (InterruptedException e) {
                cf.completeExceptionally(e);
            }
        }).start();

        String result = cf.get(); // Bloquea hasta completarse
        Assertions.assertEquals("Hola Mundo", result);
    }

    /**
     * Test 3: supplyAsync() con CompletableFuture
     * Concepto: Iniciar una tarea asincrónica sin manejar el Executor explícitamente.
     * supplyAsync() ejecuta una función que retorna un valor.
     */
    @Test
    void testSupplyAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return 10;
        });

        Integer result = cf.get();
        Assertions.assertEquals(10, result);
    }

    /**
     * Test 4: Encadenamiento básico con thenApply()
     * Concepto: Encadenar operaciones asincrónicas usando thenApply para transformar el resultado.
     */
    @Test
    void testThenApply() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> 20)
                .thenApply(x -> x * 2) // se ejecuta cuando la tarea previa complete
                .thenApply(x -> x + 5);

        // Esperamos el resultado final
        Integer result = cf.get();
        Assertions.assertEquals(45, result); // (20 * 2) + 5 = 45
    }

    /**
     * Test 5: Composición de Futures con thenCompose()
     * Concepto: Encadenar tareas asíncronas que dependen del resultado anterior.
     * thenCompose() "aplana" otro CompletableFuture dentro del flujo.
     */
    @Test
    void testThenCompose() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> "Backend")
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " Avanzado"));

        String result = cf.get();
        Assertions.assertEquals("Backend Avanzado", result);
    }

    /**
     * Test 6: Combinación de Futures con thenCombine()
     * Concepto: Correr dos tareas en paralelo y combinar sus resultados.
     * thenCombine() espera a que ambos se completen y luego aplica una función.
     */
    @Test
    void testThenCombine() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(300); } catch (InterruptedException e) {}
            return 5;
        });

        CompletableFuture<Integer> cf2 = CompletableFuture.supplyAsync(() -> 10);

        // Combinar resultados de cf1 y cf2 cuando ambos terminen
        CompletableFuture<Integer> combined = cf1.thenCombine(cf2, Integer::sum);
        Assertions.assertEquals(15, combined.get());
    }

    /**
     * Test 7: Manejo de excepciones con exceptionally()
     * Concepto: Capturar y manejar excepciones asincrónicas.
     * exceptionally() nos permite recuperar de un error y retornar un valor alternativo.
     */
    @Test
    void testExceptionHandling() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> {
            if(true)
                throw new RuntimeException("Error en la tarea asincrónica");

            return 10;
        }).exceptionally(ex -> {
            // Manejamos la excepción retornando un valor por defecto
            return -1;
        });

        Integer result = cf.get();
        Assertions.assertEquals(-1, result);
    }

    /**
     * Test 8: Uso de handle() para manejar resultado y excepciones
     * Concepto: handle() se ejecuta siempre, haya o no excepción, permitiendo lógica unificada.
     */
    @Test
    void testHandle() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            if (true) throw new IllegalStateException("Error forzado");
            return "Valor Inalcanzable";
        }).handle((res, ex) -> {
            if (ex != null) {
                return "Valor por defecto tras excepción";
            } else {
                return res;
            }
        });

        String result = cf.get();
        Assertions.assertEquals("Valor por defecto tras excepción", result);
    }

    /**
     * Test 9: Combinación de múltiples tareas con allOf()
     * Concepto: Esperar a que múltiples CompletableFuture se completen antes de continuar.
     */
    @Test
    void testAllOf() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> 1);
        CompletableFuture<Integer> cf2 = CompletableFuture.supplyAsync(() -> 2);
        CompletableFuture<Integer> cf3 = CompletableFuture.supplyAsync(() -> 3);

        CompletableFuture<Void> all = CompletableFuture.allOf(cf1, cf2, cf3);

        // Cuando allOf complete, todos han terminado
        all.get(); // Bloquea hasta que todos se completen

        // join para obtener el resultado de cada uno
        int sum = cf1.join() + cf2.join() + cf3.join();

        Assertions.assertEquals(6, sum);
    }

    /**
     * Test 10: Composición avanzada con thenApplyAsync y un executor custom
     * Concepto: Control de la ejecución asincrónica mediante un Executor propio.
     * Muestra cómo personalizar el pool de hilos usado por CompletableFuture.
     */
    @Test
    void testCustomExecutor() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> 5, executor)
                .thenApplyAsync(x -> x * 3, executor);

        Integer result = cf.get();
        Assertions.assertEquals(15, result);
        executor.shutdown();
    }

    /**
     * Test 11: thenApply vs thenApplyAsync
     * Concepto: Demostrar la diferencia entre thenApply (sincrónico en el hilo de compleción)
     * y thenApplyAsync (ejecuta la continuación en un hilo del pool ForkJoin común o custom).
     * Esto es relevante para la planificación y optimización del backend.
     */
    @Test
    void testThenApplyVsThenApplyAsync() throws ExecutionException, InterruptedException {
        // thenApply se ejecuta en el mismo hilo que completó la etapa previa.
        CompletableFuture<String> cfThenApply = CompletableFuture.supplyAsync(() -> "dato")
                .thenApply(String::toUpperCase);

        // thenApplyAsync se ejecuta en otro hilo (generalmente del commonForkJoinPool)
        CompletableFuture<String> cfThenApplyAsync = CompletableFuture.supplyAsync(() -> "dato")
                .thenApplyAsync(String::toUpperCase);

        Assertions.assertEquals("DATO", cfThenApply.get());
        Assertions.assertEquals("DATO", cfThenApplyAsync.get());
        // La diferencia radica en el hilo que se utiliza para la operación.
    }

    /**
     * Test 12: Aplicación realista: Timeout de Futures
     * Concepto: Cómo usar timeout con get(timeout, unit) para evitar bloqueos indefinidos.
     * Esto es crítico en backend para evitar hilos colgados.
     */
    @Test
    void testFutureWithTimeout() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> {
            // Simulamos tarea lenta
            Thread.sleep(1000);
            return "Completado";
        });

        Assertions.assertThrows(TimeoutException.class, () -> {
            // Esperamos máximo 100 ms antes de tirar TimeoutException
            future.get(100, TimeUnit.MILLISECONDS);
        });

        executor.shutdown();
    }

    /**
     * Test 13: CompletableFuture con timeouts usando orTimeout() y completeOnTimeout()
     * Concepto: A partir de Java 9, CompletableFuture soporta métodos utilitarios
     * para timeouts sin necesidad de bloqueos explícitos en get().
     */
    @Test
    void testCompletableFutureTimeouts() {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
            return "Lento";
        });

        // orTimeout lanza TimeoutException si no se completa en el tiempo indicado
        cf.orTimeout(500, TimeUnit.MILLISECONDS);

        Assertions.assertThrows(ExecutionException.class, () -> cf.get());

        // Ejemplo con completeOnTimeout para retornar un valor por defecto
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
            return "Lento";
        }).completeOnTimeout("Por defecto", 500, TimeUnit.MILLISECONDS);

        // Aquí no lanzará excepción, simplemente devolverá "Por defecto"
        try {
            Assertions.assertEquals("Por defecto", cf2.get());
        } catch (Exception e) {
            Assertions.fail("No debería lanzar excepción, se esperaba un valor por defecto.");
        }
    }
}
