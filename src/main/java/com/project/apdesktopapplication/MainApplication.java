package com.project.apdesktopapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Use getResource with leading slash for absolute path
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/apdesktopapplication/login-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 400, 500);
        primaryStage.setTitle("Resource Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}