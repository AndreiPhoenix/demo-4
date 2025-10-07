package com.demo;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolManager {

    private final List<String> urls;

    public ThreadPoolManager(List<String> urls) {
        this.urls = urls;
    }

    public void executeWithFixedThreadPool(int poolSize) throws InterruptedException, ExecutionException {
        System.out.println("\n=== FixedThreadPool (Size: " + poolSize + ") ===");

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        executeTasks(executor, "FixedThreadPool");
    }

    public void executeWithCachedThreadPool() throws InterruptedException, ExecutionException {
        System.out.println("\n=== CachedThreadPool ===");

        ExecutorService executor = Executors.newCachedThreadPool();
        executeTasks(executor, "CachedThreadPool");
    }

    public void executeWithSingleThreadExecutor() throws InterruptedException, ExecutionException {
        System.out.println("\n=== SingleThreadExecutor ===");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executeTasks(executor, "SingleThreadExecutor");
    }

    public void executeWithCustomThreadPool(int corePoolSize, int maxPoolSize, int queueCapacity)
            throws InterruptedException, ExecutionException {
        System.out.println("\n=== Custom ThreadPool (Core: " + corePoolSize +
                ", Max: " + maxPoolSize + ", Queue: " + queueCapacity + ") ===");

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        executeTasks(executor, "CustomThreadPool");
        executor.shutdown();
    }

    private void executeTasks(ExecutorService executor, String poolType)
            throws InterruptedException, ExecutionException {

        List<Future<HttpTaskResult>> futures = new ArrayList<>();
        Instant startTime = Instant.now();

        // Отправляем все задачи
        for (String url : urls) {
            Future<HttpTaskResult> future = executor.submit(new HttpTask(url));
            futures.add(future);
        }

        // Собираем результаты
        List<HttpTaskResult> results = new ArrayList<>();
        for (Future<HttpTaskResult> future : futures) {
            results.add(future.get());
        }

        Instant endTime = Instant.now();
        long totalTime = Duration.between(startTime, endTime).toMillis();

        // Выводим результаты
        printResults(results, totalTime, poolType);

        if (!(executor instanceof ThreadPoolExecutor)) {
            executor.shutdown();
        }
    }

    private void printResults(List<HttpTaskResult> results, long totalTime, String poolType) {
        System.out.println("=== РЕЗУЛЬТАТЫ (" + poolType + ") ===");

        int successCount = 0;
        int errorCount = 0;
        long totalResponseTime = 0;

        for (HttpTaskResult result : results) {
            System.out.println(result);

            if (result.errorMessage == null && result.statusCode == 200) {
                successCount++;
                totalResponseTime += result.responseTime;
            } else {
                errorCount++;
            }
        }

        double avgResponseTime = successCount > 0 ? (double) totalResponseTime / successCount : 0;

        System.out.println("\n=== СВОДКА (" + poolType + ") ===");
        System.out.println("Общее время выполнения: " + totalTime + "ms");
        System.out.println("Успешные запросы: " + successCount);
        System.out.println("Запросы с ошибками: " + errorCount);
        System.out.println("Среднее время ответа: " + String.format("%.2f", avgResponseTime) + "ms");
        System.out.println("Всего URL: " + results.size());
    }

    public void monitorThreadPool() throws InterruptedException {
        System.out.println("\n=== МОНИТОРИНГ ThreadPoolExecutor ===");

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,  // core pool size
                5,  // maximum pool size
                30, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10)
        );

        // Мониторинг в отдельном потоке
        Thread monitorThread = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    System.out.printf("Монитор - Активные потоки: %d, Задачи в очереди: %d, Выполнено задач: %d%n",
                            executor.getActiveCount(),
                            executor.getQueue().size(),
                            executor.getCompletedTaskCount());
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        monitorThread.start();

        // Выполняем задачи
        List<Future<HttpTaskResult>> futures = new ArrayList<>();
        for (String url : urls.subList(0, Math.min(15, urls.size()))) {
            futures.add(executor.submit(new HttpTask(url)));
        }

        // Ждем завершения мониторинга
        monitorThread.join();

        // Завершаем executor
        executor.shutdown();
        System.out.println("Мониторинг завершен");
    }
}