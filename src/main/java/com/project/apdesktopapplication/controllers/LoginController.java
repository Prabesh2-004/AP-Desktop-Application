package com.project.apdesktopapplication.controllers;

import com.project.apdesktopapplication.models.User;
import com.project.apdesktopapplication.services.UserService;
import com.project.apdesktopapplication.utils.SessionManager;
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

public class LoginController {
    @FXML private TextField emailOrUsernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label messageLabel;
    @FXML private Hyperlink toSignupLink;

    private UserService userService = UserService.getInstance();

    @FXML
    private void handleLogin() {
        String username = emailOrUsernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password");
            return;
        }

        User user = userService.authenticate(username, password);
        if (user != null) {
            SessionManager.getInstance().setCurrentUser(user);
            navigateToDashboard(user);
        } else {
            messageLabel.setText("Invalid username or password");
        }
    }

    private void navigateToDashboard(User user) {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            String fxmlPath;
            switch (user.getRole()) {
                case "ADMIN":
                    fxmlPath = "/com/project/apdesktopapplication/admin-main-view.fxml";
                    break;
                case "STAFF":
                    fxmlPath = "/com/project/apdesktopapplication/staff-main-view.fxml";
                    break;
                case "STUDENT":
                default:
                    fxmlPath = "/com/project/apdesktopapplication/main-view.fxml";
                    break;
            }
            System.out.println("Loading: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                System.err.println("FXML not found: " + fxmlPath);
                messageLabel.setText("Error loading dashboard");
                return;
            }
            Parent root = loader.load();

            // Pass user info to controller based on type
            Object controller = loader.getController();
            if (controller instanceof AdminNavigationController) {
                ((AdminNavigationController) controller).setUserInfo(user);
            } else if (controller instanceof StaffNavigationController) {
                ((StaffNavigationController) controller).setUserInfo(user);
            } else if (controller instanceof NavigationController) {
                ((NavigationController) controller).setUserInfo(user);
            }

            Scene scene = new Scene(root, 1200, 750);
            stage.setScene(scene);
            stage.setTitle("Resource Management System - " + user.getRole());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void goToSignup() {
        try {
            Stage stage = (Stage) toSignupLink.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/apdesktopapplication/signup-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 400, 500);
            stage.setScene(scene);
            stage.setTitle("Sign Up");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}