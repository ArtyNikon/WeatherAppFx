package io.github.artynikon.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Wind {
    private double speed;
    private int deg;
    private double gust;

    public double getSpeed() {
        return speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getGust() {
        return gust;
    }
    public void setGust(double gust) {
        this.gust = gust;
    }

    public int getDeg() {
        return deg;
    }
    public void setDeg(int deg) {
        this.deg = deg;
    }

    public String getDirectionString() {
        String[] directions = {"С", "СВ", "В", "ЮВ", "Ю", "ЮЗ", "З", "СЗ"};

        int index = (int) Math.round((this.deg % 360) / 45.0);

        return directions[index % 8];
    }
}