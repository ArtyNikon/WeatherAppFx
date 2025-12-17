package io.github.artynikon.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HourlyForecast {
    private MainData main;
    private Wind wind;
    private List<Weather> weather;

    private long dt;

    @JsonProperty("pop")
    private double probabilityOfPrecipitation;

    public MainData getMain() {
        return main;
    }
    public void setMain(MainData main) {
        this.main = main;
    }

    public Wind getWind() {
        return wind;
    }
    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public List<Weather> getWeather() {
        return weather;
    }
    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public long getDt() {
        return dt;
    }
    public void setDt(long dt) {
        this.dt = dt;
    }

    public double getProbabilityOfPrecipitation() {
        return probabilityOfPrecipitation;
    }
    public void setProbabilityOfPrecipitation(double probabilityOfPrecipitation) {
        this.probabilityOfPrecipitation = probabilityOfPrecipitation;
    }
}
