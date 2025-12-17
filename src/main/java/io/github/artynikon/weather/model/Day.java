package io.github.artynikon.weather.model;

import java.time.LocalDateTime;
import java.util.List;

public class Day {
    private LocalDateTime localDateTime;
    private List<HourlyForecast> hourlyForecast;

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public List<HourlyForecast> getHourlyForecast() {
        return hourlyForecast;
    }
    public void setHourlyForecast(List<HourlyForecast> hourlyForecast) {
        this.hourlyForecast = hourlyForecast;
    }

    public double calculateMaxTempOfDay() {
        if (hourlyForecast == null || hourlyForecast.isEmpty()) return 0.0;

        return hourlyForecast.stream()
                .mapToDouble(f -> f.getMain().getTemp_max())
                .max()
                .orElse(Double.NEGATIVE_INFINITY);
    }

    public double calculateMinTempOfDay() {
        if (hourlyForecast == null || hourlyForecast.isEmpty()) return 0.0;

        return hourlyForecast.stream()
                .mapToDouble(f -> f.getMain().getTemp_min())
                .min()
                .orElse(Double.POSITIVE_INFINITY);
    }

    public double calculateMaxWindGustOfDay() {
        if (hourlyForecast == null || hourlyForecast.isEmpty()) return 0.0;

        return hourlyForecast.stream()
                .mapToDouble(f -> f.getWind().getGust())
                .max()
                .orElse(0.0);
    }

    public double calculateMaxWindSpeedOfDay() {
        if (hourlyForecast == null || hourlyForecast.isEmpty()) return 0.0;

        return hourlyForecast.stream()
                .mapToDouble(f -> f.getWind().getSpeed())
                .max()
                .orElse(0.0);
    }
}
