package com.project.apdesktopapplication.controllers;

import com.project.apdesktopapplication.models.User;
import com.project.apdesktopapplication.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField fullNameField;
    @FXML private TextField universityIdField;
    @FXML private PasswordField passwordField;
    @FXML private Label roleLabel;
    @FXML private Button signupButton;
    @FXML private Label messageLabel;
    @FXML private Hyperlink toLoginLink;

    private UserService userService = UserService.getInstance();

    @FXML
    public void initialize() {
        roleLabel.setText("Role: STUDENT");
    }

    @FXML
    private void handleSignup() {
        String fullName = fullNameField.getText().trim();
        String username = universityIdField.getText().trim();
        String password = passwordField.getText().trim();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill all fields");
            return;
        }

        if (password.length() < 4) {
            messageLabel.setText("Password must be at least 4 characters");
            return;
        }

        if (userService.getUserByUsername(username) != null) {
            messageLabel.setText("Username already exists");
            return;
        }

        User newUser = new User(
                "USR" + System.currentTimeMillis(),
                username,
                fullName,
                password,
                "STUDENT",
                "ACTIVE"
        );

        if (userService.addUser(newUser)) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Sign up successful! Please login.");
            clearFields();
        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Error creating account");
        }
    }

    private void clearFields() {
        fullNameField.clear();
        universityIdField.clear();
        passwordField.clear();
    }

    @FXML
    private void goToLogin() {
        try {
            Stage stage = (Stage) toLoginLink.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/apdesktopapplication/login-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 400, 500);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}