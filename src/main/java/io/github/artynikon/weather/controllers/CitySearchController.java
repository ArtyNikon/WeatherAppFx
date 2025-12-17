package io.github.artynikon.weather.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class CitySearchController {
    @FXML private TextField cityInput;
    @FXML private ImageView welcomeFrog;
    @FXML private Text statusText;

    @FXML private Button searchButton;

    @FXML
    public void showCity(ActionEvent actionEvent) throws IOException {
        String cityText = cityInput.getText().trim();

        if (cityText.isEmpty()) {
            updateUI("Пожалуйста, введите город", "/images/placeholders/frog-input-error.png");
            return;
        }

        setLoadingState(true);

        try {
            navigateToResultPage(actionEvent, cityText);
        } catch (Exception e) {
            handleError(e);
            setLoadingState(false);
        }
    }

    private void navigateToResultPage(ActionEvent e, String city) throws Exception {
        URL fxmlLocation = getClass().getResource("/fxml/forecast_view.fxml");
        if (fxmlLocation == null) {
            throw new IOException("FXML файл не найден");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        ForecastViewController controller = loader.getController();
        controller.showPageWithResult(city);

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void handleError(Exception e) {
        System.out.println("Ошибка поймана в контроллере: " + e.getMessage());

        if (e.getMessage() != null && e.getMessage().contains("404")) {
            updateUI("Нет такого города", "/images/placeholders/frog-city-not-found.png");
        } else {
            updateUI("Ошибка данных", "/images/placeholders/frog-error.png");
        }
    }

    private void updateUI(String message, String imagePath) {
        statusText.setText(message);

        URL imageUrl = getClass().getResource(imagePath);

        if (imageUrl != null) {
            welcomeFrog.setImage(new Image(imageUrl.toExternalForm()));
            welcomeFrog.setVisible(true);
        } else {
            System.out.println("Картинка не найдена: " + imagePath);
        }
    }

    private void setLoadingState(boolean isLoading) {
        if (searchButton != null) {
            searchButton.setDisable(isLoading);
        }
        cityInput.setDisable(isLoading);
    }

}