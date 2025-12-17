package io.github.artynikon.weather;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class WeatherApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/city_search.fxml"));
        Parent root = loader.load();

        stage.setScene(new Scene(root));
        stage.setTitle("FrogCast");
        stage.setResizable(false);

        loadAppIcon(stage);

        stage.show();
    }

    private void loadAppIcon(Stage stage) {
        URL iconUrl = getClass().getResource("/icons/app-icon.png");

        if (iconUrl != null) {
            stage.getIcons().add(new Image(iconUrl.toExternalForm()));
        } else {
            System.err.println("Не удалось найти иконку приложения");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
