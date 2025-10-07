package com.demo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class UrlLoader {

    public static List<String> loadUrlsFromFile() {
        List<String> urls = new ArrayList<>();

        try (InputStream inputStream = UrlLoader.class.getClassLoader().getResourceAsStream("urls.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    urls.add(line);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке URL-адресов: " + e.getMessage());
            // Возвращаем список по умолчанию, если файл не найден
            return getDefaultUrls();
        }

        return urls;
    }

    private static List<String> getDefaultUrls() {
        return List.of(
                "https://httpbin.org/get",
                "https://jsonplaceholder.typicode.com/posts/1",
                "https://api.github.com",
                "https://catfact.ninja/fact",
                "https://api.agify.io?name=alex",
                "https://api.coindesk.com/v1/bpi/currentprice.json",
                "https://dog.ceo/api/breeds/image/random",
                "https://api.spacexdata.com/v4/launches/latest",
                "https://api.nationalize.io?name=alex",
                "https://api.publicapis.org/entries",
                "https://api.zippopotam.us/us/90210",
                "https://api.weather.gov/points/38.8894,-77.0352",
                "https://api.kanye.rest",
                "https://baconipsum.com/api/?type=meat-and-filler",
                "https://api.chucknorris.io/jokes/random",
                "https://www.boredapi.com/api/activity",
                "https://datausa.io/api/data?drilldowns=Nation&measures=Population",
                "https://api.genderize.io?name=alex",
                "https://api.ukrainealarm.com/api/v3/alerts",
                "https://openlibrary.org/books/OL7353617M.json"
        );
    }
}