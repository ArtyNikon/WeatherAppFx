package io.github.artynikon.weather.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import io.github.artynikon.weather.model.Day;
import io.github.artynikon.weather.model.HourlyForecast;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class WeatherDayCardController {
    private static final String[] DIRECTIONS = {"С", "СВ", "В", "ЮВ", "Ю", "ЮЗ", "З", "СЗ"};

    @FXML private Text dateText;
    @FXML private ImageView weatherIcon;
    @FXML private Text maxTempText;
    @FXML private Text minTempText;
    @FXML private Text windSpeedText;
    @FXML private Text windDirText;

    public void setDayData(Day day) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E. d", Locale.forLanguageTag("ru"));
        dateText.setText(capitalize(day.getLocalDateTime().format(formatter)));

        maxTempText.setText(String.valueOf(Math.round(day.calculateMaxTempOfDay())));
        minTempText.setText(String.valueOf(Math.round(day.calculateMinTempOfDay())));

        String windInfo = Math.round(day.calculateMaxWindSpeedOfDay()) + "-" + Math.round(day.calculateMaxWindGustOfDay());
        windSpeedText.setText(windInfo);

        if (!day.getHourlyForecast().isEmpty()) {
            double degrees = day.getHourlyForecast().get(0).getWind().getDeg();
            windDirText.setText(getWindDirection(degrees));

            loadIcon(day.getHourlyForecast().get(0).getWeather().get(0).getIcon());
        }
    }

    public void setHourlyData(HourlyForecast forecast, int timezoneOffset) {
        long unixSeconds = forecast.getDt();
        LocalDateTime time = java.time.Instant.ofEpochSecond(unixSeconds)
                .atZone(ZoneOffset.ofTotalSeconds(timezoneOffset))
                .toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        dateText.setText(time.format(formatter));

        maxTempText.setText(String.valueOf(Math.round(forecast.getMain().getTemp())));
        minTempText.setText("");

        String windInfo = Math.round(forecast.getWind().getSpeed()) + "-" + Math.round(forecast.getWind().getGust());
        windSpeedText.setText(windInfo);

        double degrees = forecast.getWind().getDeg();
        windDirText.setText(getWindDirection(degrees));

        if (!forecast.getWeather().isEmpty()) {
            loadIcon(forecast.getWeather().get(0).getIcon());
        }
    }

    public void showOnlyTemperature() {
        windSpeedText.setVisible(false);
        windSpeedText.setManaged(false);
        windDirText.setVisible(false);
        windDirText.setManaged(false);

        dateText.setVisible(true);
        dateText.setManaged(true);
        weatherIcon.setVisible(true);
        weatherIcon.setManaged(true);
        maxTempText.setVisible(true);
        maxTempText.setManaged(true);
        minTempText.setVisible(true);
        minTempText.setManaged(true);
    }

    public void showOnlyWind() {
        dateText.setVisible(false);
        dateText.setManaged(false);
        weatherIcon.setVisible(false);
        weatherIcon.setManaged(false);
        maxTempText.setVisible(false);
        maxTempText.setManaged(false);
        minTempText.setVisible(false);
        minTempText.setManaged(false);

        windSpeedText.setVisible(true);
        windSpeedText.setManaged(true);
        windDirText.setVisible(true);
        windDirText.setManaged(true);
    }

    private void loadIcon(String code) {
        if (code == null || code.isEmpty()) return;
        String path = "/icons/" + code + ".png";
        URL url = getClass().getResource(path);
        if (url != null) {
            weatherIcon.setImage(new Image(url.toExternalForm()));
        } else {
            URL def = getClass().getResource("/icons/default.png");
            if (def != null) weatherIcon.setImage(new Image(def.toExternalForm()));
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String getWindDirection(double degrees) {
        int index = (int) Math.round(((degrees % 360) / 45)) % 8;
        return DIRECTIONS[index];
    }
}