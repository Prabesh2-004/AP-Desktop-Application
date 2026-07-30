package com.project.apdesktopapplication.controllers;

import com.project.apdesktopapplication.models.User;
import com.project.apdesktopapplication.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.geometry.Insets;
import com.project.apdesktopapplication.utils.PasswordHasher;

import java.util.Optional;

public class AdminManageUsersController {
    @FXML private Label totalUsersLabel;
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> colId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colStatus;
    @FXML private TableColumn<User, Void> colActions;

    private UserService userService = UserService.getInstance();
    private ObservableList<User> users;

    @FXML
    public void initialize() {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        colStatus.setCellFactory(column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(item.equals("ACTIVE") ? "-fx-text-fill: #16A34A; -fx-font-weight: bold;" :
                            "-fx-text-fill: #DC2626; -fx-font-weight: bold;");
                }
            }
        });

        colActions.setCellFactory(column -> new TableCell<User, Void>() {
            private final Button editBtn = new Button("✎ Edit");
            private final Button deleteBtn = new Button("✕ Delete");

            {
                editBtn.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 4 10 4 10; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 4 10 4 10; -fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                User user = getTableView().getItems().get(getIndex());
                boolean isAdmin = "ADMIN".equals(user.getRole());
                deleteBtn.setDisable(isAdmin);

                editBtn.setOnAction(e -> handleEditUser(user));
                deleteBtn.setOnAction(e -> handleDeleteUser(user));
                HBox box = new HBox(6, editBtn, deleteBtn);
                setGraphic(box);
            }
        });
    }

    private void loadData() {
        users = FXCollections.observableArrayList(userService.getAllUsers());
        usersTable.setItems(users);
        totalUsersLabel.setText(String.valueOf(users.size()));
    }

    @FXML
    private void handleAddUser() {
        showUserDialog(null);
    }

    private void handleEditUser(User user) {
        showUserDialog(user);
    }

    private void handleDeleteUser(User user) {
        if ("ADMIN".equals(user.getRole())) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Cannot Delete");
            dialog.setContentText("Cannot delete admin users.");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Delete User");
        dialog.setContentText("Are you sure you want to delete " + user.getFullName() + "?");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            userService.deleteUser(user.getUserId());
            loadData();
        }
    }

    private void showUserDialog(User existingUser) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle(existingUser == null ? "Add User" : "Edit User");
        dialog.setHeaderText(existingUser == null ? "Create a new user" : "Update user information");

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (leave blank to keep current)");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("STUDENT", "STAFF", "ADMIN");
        roleCombo.setPromptText("Role");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("ACTIVE", "INACTIVE");
        statusCombo.setPromptText("Status");

        if (existingUser != null) {
            fullNameField.setText(existingUser.getFullName());
            usernameField.setText(existingUser.getUsername());
            usernameField.setDisable(true);
            roleCombo.setValue(existingUser.getRole());
            statusCombo.setValue(existingUser.getStatus());
            passwordField.setPromptText("Enter new password to change");
        }

        form.getChildren().addAll(
                new Label("Full Name:"), fullNameField,
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                new Label("Role:"), roleCombo,
                new Label("Status:"), statusCombo
        );

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String fullName = fullNameField.getText().trim();
                String username = usernameField.getText().trim();
                String role = roleCombo.getValue();
                String status = statusCombo.getValue();

                if (fullName.isEmpty() || username.isEmpty() || role == null || status == null) {
                    return null;
                }

                User user;
                if (existingUser != null) {
                    user = existingUser;
                    user.setFullName(fullName);
                    user.setRole(role);
                    user.setStatus(status);
                    String newPassword = passwordField.getText().trim();
                    if (!newPassword.isEmpty()) {
                        user.setPassword(PasswordHasher.hash(newPassword));
                    }
                } else {
                    String password = passwordField.getText().trim();
                    if (password.isEmpty()) {
                        password = "password123";
                    }
                    user = User.create(null, username, fullName, PasswordHasher.hash(password), role, status);
                }
                return user;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            if (existingUser != null) {
                userService.updateUser(user);
            } else {
                userService.addUser(user);
            }
            loadData();
        });
    }
}