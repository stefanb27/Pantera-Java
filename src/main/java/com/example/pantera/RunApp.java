package com.example.pantera;

import com.example.pantera.controller.LogInController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class RunApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/login.fxml"));

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Log In");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(loader.load());
        dialogStage.setScene(scene);

        LogInController logInController = loader.getController();
        logInController.setService(dialogStage);
        dialogStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
