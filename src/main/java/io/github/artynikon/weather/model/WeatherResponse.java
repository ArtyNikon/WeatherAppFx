package io.github.artynikon.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {

    private City city;

    @JsonProperty("list") private List<HourlyForecast> hourlyForecasts;

    public City getCity() {
        return city;
    }
    public void setCity(City city) {
        this.city = city;
    }

    public List<HourlyForecast> getHourlyForecasts() {
        return hourlyForecasts;
    }
    public void setHourlyForecasts(List<HourlyForecast> hourlyForecasts) {
        this.hourlyForecasts = hourlyForecasts;
    }
}