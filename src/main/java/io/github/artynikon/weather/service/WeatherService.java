package io.github.artynikon.weather.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.artynikon.weather.Config;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import io.github.artynikon.weather.model.Day;
import io.github.artynikon.weather.model.HourlyForecast;
import io.github.artynikon.weather.model.WeatherResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WeatherService {
    private static final String API_KEY = Config.API_KEY;
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/forecast";

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public WeatherResponse getWeatherForCity(String city) throws Exception {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("Город не может быть пустым");
        }

        String json = getJSONOutput(city);
        return mapper.readValue(json, WeatherResponse.class);
    }

    private String getJSONOutput(String city) throws IOException {
        String url = makeURL(city);

        Request request = new Request.Builder().url(url).build();

        try (Response response = this.client.newCall(request).execute()) {
            int responseCode = response.code();

            if (responseCode == 404) {
                throw new RuntimeException("404");
            }

            if (!response.isSuccessful()) {
                throw new IOException("Ошибка сервера: " + responseCode);
            }

            if (response.body() == null) {
                throw new IOException("Пришел пустой ответ от сервера");
            }

            return response.body().string();
        }
    }

    private String makeURL(String city) {
        String encodedCity = URLEncoder.encode(city.trim(), StandardCharsets.UTF_8);
        return BASE_URL + "?q=" + encodedCity + "&appid=" + API_KEY + "&units=metric&lang=ru";
    }

    public List<Day> groupForecastsByDay(List<HourlyForecast> allForecasts, int timezoneOffset) {

        ZoneOffset offset = ZoneOffset.ofTotalSeconds(timezoneOffset);

        Map<LocalDate, List<HourlyForecast>> groupedBy = allForecasts.stream()
                .collect(
                        Collectors.groupingBy(forecast -> {
                            long unixSeconds = forecast.getDt();
                            Instant instant = Instant.ofEpochSecond(unixSeconds);
                            return instant.atZone(offset).toLocalDate();
                        })
                );

        return groupedBy.entrySet().stream()
                .map(e -> {
                    Day day = new Day();
                    HourlyForecast firstForecast = e.getValue().get(0);

                    long unixSeconds = firstForecast.getDt();

                    LocalDateTime firstForecastTime = java.time.Instant.ofEpochSecond(unixSeconds)
                            .atZone(offset)
                            .toLocalDateTime();

                    day.setLocalDateTime(firstForecastTime);
                    day.setHourlyForecast(e.getValue());
                    return day;
                })
                .sorted((d1, d2) -> d1.getLocalDateTime().compareTo(d2.getLocalDateTime()))
                .collect(Collectors.toList());
    }

}
