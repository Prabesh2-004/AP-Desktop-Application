package com.project.apdesktopapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
<<<<<<< HEAD
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Resources");
=======
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("StaffManagment.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1300, 800);
        stage.setTitle("Staff Dashboard");
>>>>>>> 993f08dba35e802efe60e0a9ea7ba1abd32eec24
        stage.setScene(scene);
        stage.show();
    }
}
