module org.example.javaweather {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires javafx.graphics;
    requires javafx.base;


    opens io.github.artynikon.weather.model to com.fasterxml.jackson.databind;
    opens io.github.artynikon.weather to javafx.fxml;

    exports io.github.artynikon.weather;
    exports io.github.artynikon.weather.controllers;
    opens io.github.artynikon.weather.controllers to javafx.fxml;
}