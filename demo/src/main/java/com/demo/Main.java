package com.demo;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== ДЕМОНСТРАЦИЯ РАБОТЫ С ПУЛАМИ ПОТОКОВ ===\n");

            // Загружаем URL-адреса
            List<String> urls = UrlLoader.loadUrlsFromFile();
            System.out.println("Загружено URL-адресов: " + urls.size());

            ThreadPoolManager manager = new ThreadPoolManager(urls);

            // Демонстрация различных пулов потоков
            manager.executeWithFixedThreadPool(3);
            Thread.sleep(2000); // Пауза между тестами

            manager.executeWithCachedThreadPool();
            Thread.sleep(2000);

            manager.executeWithSingleThreadExecutor();
            Thread.sleep(2000);

            manager.executeWithCustomThreadPool(2, 6, 10);
            Thread.sleep(2000);

            // Демонстрация мониторинга
            manager.monitorThreadPool();

        } catch (Exception e) {
            System.err.println("Ошибка в приложении: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
