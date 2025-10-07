package com.demo;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

public class HttpTask implements Callable<HttpTaskResult> {

    private final String url;

    public HttpTask(String url) {
        this.url = url;
    }

    @Override
    public HttpTaskResult call() {
        Instant start = Instant.now();
        int statusCode = -1;
        String errorMessage = null;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "ThreadPoolDemo/1.0");

            statusCode = connection.getResponseCode();
            connection.disconnect();

        } catch (Exception e) {
            errorMessage = e.getMessage();
        }

        Instant end = Instant.now();
        long responseTime = Duration.between(start, end).toMillis();

        return new HttpTaskResult(url, statusCode, responseTime, errorMessage);
    }

    public class Result {
        public Object errorMessage;
    }
}

// Выносим Result в отдельный public класс
class HttpTaskResult {
    public final String url;
    public final int statusCode;
    public final long responseTime;
    public final String errorMessage;

    public HttpTaskResult(String url, int statusCode, long responseTime, String errorMessage) {
        this.url = url;
        this.statusCode = statusCode;
        this.responseTime = responseTime;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        if (errorMessage != null) {
            return String.format("URL: %-50s | ERROR: %s", url, errorMessage);
        }
        return String.format("URL: %-50s | Status: %d | Time: %dms", url, statusCode, responseTime);
    }
}