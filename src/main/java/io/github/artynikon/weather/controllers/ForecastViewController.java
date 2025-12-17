package io.github.artynikon.weather.controllers;

import io.github.artynikon.weather.model.Day;
import io.github.artynikon.weather.model.HourlyForecast;
import io.github.artynikon.weather.model.WeatherResponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import io.github.artynikon.weather.model.*;
import io.github.artynikon.weather.service.WeatherService;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ForecastViewController {
    private final WeatherService weatherService = new WeatherService();

    private List<HourlyForecast> allHourlyForecasts;
    private List<Day> groupedDays;
    private int currentStartIndex = 0;
    private int currentTimezoneOffset = 0;
    private final int FORECAST_COUNT = 5;

    @FXML private ImageView backgroundImageView;

    @FXML private Button nextHourButton;
    @FXML private Button prevHourButton;
    @FXML private Button hourlyButton;
    @FXML private Button dailyButton;

    @FXML private Text city;
    @FXML private Text conditionDescriptionText;
    @FXML private Text currentFullDatePlusTime;
    @FXML private Text currentMonth;
    @FXML private Text currentTemp;
    @FXML private Text feels_like;
    @FXML private Text wind;

    @FXML private HBox temperatureContainer;
    @FXML private HBox windContainer;

    public void showPageWithResult(String cityInput) throws Exception {

        WeatherResponse weatherResponse = weatherService.getWeatherForCity(cityInput);

        this.currentTimezoneOffset = weatherResponse.getCity().getTimezone();
        this.allHourlyForecasts = weatherResponse.getHourlyForecasts();
        this.groupedDays = weatherService.groupForecastsByDay(allHourlyForecasts, currentTimezoneOffset);
        this.currentStartIndex = 0;

        updateWeatherUpDisplay(groupedDays, cityInput, currentTimezoneOffset);

        drawDailyForecasts();
        switchButtonState(dailyButton, hourlyButton);
    }

    @FXML
    private void backToChooseCity(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/city_search.fxml"));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void showWeatherForDays() {
        if (dailyButton.isDisabled()) return;

        drawDailyForecasts();

        switchButtonState(dailyButton, hourlyButton);
    }

    @FXML
    private void updateWeatherDownDisplayForHours() {
        if (hourlyButton.isDisabled()) return;

        currentStartIndex = 0;

        drawHourlyForecasts();

        switchButtonState(hourlyButton, dailyButton);
    }

    private void drawDailyForecasts() {
        prevHourButton.setVisible(false);
        nextHourButton.setVisible(false);

        if (this.groupedDays == null || this.groupedDays.isEmpty()) return;

        LocalDateTime firstDate = groupedDays.get(0).getLocalDateTime();
        String monthName = getLocalizedMonthName(firstDate);
        currentMonth.setText(capitalize(monthName));

        temperatureContainer.getChildren().clear();
        windContainer.getChildren().clear();

        int daysLimit = Math.min(groupedDays.size(), 5);

        for (int i = 0; i < daysLimit; i++) {
            Day day = groupedDays.get(i);
            try {
                FXMLLoader loaderTemp = new FXMLLoader(getClass().getResource("/fxml/weather_day_card.fxml"));
                Node cardTempNode = loaderTemp.load();
                WeatherDayCardController ctrlTemp = loaderTemp.getController();
                ctrlTemp.setDayData(day);
                ctrlTemp.showOnlyTemperature();
                temperatureContainer.getChildren().add(cardTempNode);

                FXMLLoader loaderWind = new FXMLLoader(getClass().getResource("/fxml/weather_day_card.fxml"));
                Node cardWindNode = loaderWind.load();
                WeatherDayCardController ctrlWind = loaderWind.getController();
                ctrlWind.setDayData(day);
                ctrlWind.showOnlyWind();
                windContainer.getChildren().add(cardWindNode);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawHourlyForecasts() {
        if (allHourlyForecasts == null || allHourlyForecasts.isEmpty()) return;

        HourlyForecast firstVisible = allHourlyForecasts.get(currentStartIndex);
        LocalDateTime date = getDateFromForecast(firstVisible);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"));
        currentMonth.setText(capitalize(date.format(formatter)));

        temperatureContainer.getChildren().clear();
        windContainer.getChildren().clear();

        int endIndex = Math.min(currentStartIndex + FORECAST_COUNT, allHourlyForecasts.size());

        for (int i = currentStartIndex; i < endIndex; i++) {
            HourlyForecast forecast = allHourlyForecasts.get(i);
            try {
                FXMLLoader loaderTemp = new FXMLLoader(getClass().getResource("/fxml/weather_day_card.fxml"));
                Node cardTempNode = loaderTemp.load();
                WeatherDayCardController ctrlTemp = loaderTemp.getController();
                ctrlTemp.setHourlyData(forecast, currentTimezoneOffset);
                ctrlTemp.showOnlyTemperature();
                temperatureContainer.getChildren().add(cardTempNode);

                FXMLLoader loaderWind = new FXMLLoader(getClass().getResource("/fxml/weather_day_card.fxml"));
                Node cardWindNode = loaderWind.load();
                WeatherDayCardController ctrlWind = loaderWind.getController();
                ctrlWind.setHourlyData(forecast, currentTimezoneOffset);
                ctrlWind.showOnlyWind();
                windContainer.getChildren().add(cardWindNode);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updateNavigationButtons(endIndex);
    }

    @FXML
    private void showNextFiveHours() {
        if (currentStartIndex + FORECAST_COUNT < allHourlyForecasts.size()) {
            currentStartIndex += FORECAST_COUNT;
            drawHourlyForecasts();
        }
    }

    @FXML
    private void showPrevFiveHours() {
        if (currentStartIndex > 0) {
            currentStartIndex -= FORECAST_COUNT;
            if (currentStartIndex < 0) currentStartIndex = 0;
            drawHourlyForecasts();
        }
    }

    private void updateNavigationButtons(int endIndex) {
        prevHourButton.setVisible(true);
        nextHourButton.setVisible(true);
        prevHourButton.setDisable(currentStartIndex == 0);
        nextHourButton.setDisable(endIndex >= allHourlyForecasts.size());
    }

    public void updateWeatherUpDisplay(List<Day> days, String cityName, int timezoneOffset) {
        Day todayData = days.get(0);
        if (todayData.getHourlyForecast() == null || todayData.getHourlyForecast().isEmpty()) return;

        HourlyForecast nearestForecast = todayData.getHourlyForecast().get(0);
        double rawTemp = nearestForecast.getMain().getTemp();

        String imagePath = getBackroundImageForTemp(rawTemp);
        try {
            backgroundImageView.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        } catch (Exception e) {
            System.err.println("Фон не найден: " + imagePath);
        }

        city.setText(capitalize(cityName));
        currentFullDatePlusTime.setText(capitalize(getCurrentFormattedDateTime(timezoneOffset)));

        String monthName = getLocalizedMonthName(todayData.getLocalDateTime());
        currentMonth.setText(capitalize(monthName));

        String sign = rawTemp > 0 ? "+" : "";
        currentTemp.setText(sign + (int) rawTemp);

        double feelsLike = nearestForecast.getMain().getFeelsLike();
        String feelsSign = feelsLike > 0 ? "+" : "";
        feels_like.setText("По ощущению " + feelsSign + (int) feelsLike);

        if (!nearestForecast.getWeather().isEmpty()) {
            conditionDescriptionText.setText(nearestForecast.getWeather().get(0).getFormattedDescription());
        }

        double speed = nearestForecast.getWind().getSpeed();
        wind.setText("Ветер " + (int) speed + " м/с");
    }

    private LocalDateTime getDateFromForecast(HourlyForecast forecast) {
        long unixSeconds = forecast.getDt();
        return Instant.ofEpochSecond(unixSeconds)
                .atZone(ZoneOffset.ofTotalSeconds(currentTimezoneOffset))
                .toLocalDateTime();
    }

    private String getLocalizedMonthName(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("LLLL", Locale.forLanguageTag("ru")));
    }

    private static String getCurrentFormattedDateTime(int timezoneOffsetSeconds) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Instant now = Instant.now();
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(timezoneOffsetSeconds);
        ZonedDateTime cityTime = now.atZone(offset);
        String pattern = "E, d MMMM, HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.forLanguageTag("ru"));
        return cityTime.format(formatter);
    }

    private String getBackroundImageForTemp(double temp) {
        if (temp <= -35.0) return "/images/placeholders/-40.png";
        if (temp <= -25.0) return "/images/placeholders/-30.png";
        if (temp <= -15.0) return "/images/placeholders/-20.png";
        if (temp <= -5.0) return "/images/placeholders/-10.png";
        if (temp <= 5.0) return "/images/placeholders/0.png";
        if (temp <= 15.0) return "/images/placeholders/+10.png";
        if (temp <= 25.0) return "/images/placeholders/+20.png";
        if (temp <= 35.0) return "/images/placeholders/+30.png";
        return "/images/placeholders/+40.png";
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void switchButtonState(Button active, Button inactive) {
        active.setDisable(true);
        active.setStyle("-fx-background-radius: 7; -fx-background-color: #34515e;");

        inactive.setDisable(false);
        inactive.setStyle("-fx-background-radius: 7; -fx-background-color: #667880;");
    }
}
