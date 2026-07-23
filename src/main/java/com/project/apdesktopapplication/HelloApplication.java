package com.project.apdesktopapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("StudentManagment.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 850);
        stage.setTitle("Student Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
