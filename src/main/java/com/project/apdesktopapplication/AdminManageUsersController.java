package com.project.apdesktopapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AdminManageUsersController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> colId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colStatus;
    @FXML private TableColumn<User, String> colActions;
    @FXML private Label totalUsersLabel;

    private ObservableList<User> users;

    @FXML
    public void initialize() {
        setupColumns();
        loadUsers();
    }

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("● " + status);
                    if (status.equalsIgnoreCase("Active")) {
                        setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold; -fx-font-size: 12px;");
                    } else if (status.equalsIgnoreCase("Inactive")) {
                        setStyle("-fx-text-fill: #9CA3AF; -fx-font-weight: bold; -fx-font-size: 12px;");
                    } else if (status.equalsIgnoreCase("Suspended")) {
                        setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-font-size: 12px;");
                    }
                }
            }
        });

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✎ Edit");
            private final Button deleteBtn = new Button("✕ Delete");
            {
                editBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;");
                editBtn.setOnMouseEntered(e -> editBtn.setStyle("-fx-background-color: #1F2937; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;"));
                editBtn.setOnMouseExited(e -> editBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;"));

                deleteBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;");
                deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: #B91C1C; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;"));
                deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 4 12 4 12; -fx-cursor: hand;"));

                editBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    showEditUserDialog(user);
                });

                deleteBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(8, editBtn, deleteBtn);
                    setGraphic(box);
                }
            }
        });
    }

    private void loadUsers() {
        users = FXCollections.observableArrayList(
                new User("U001", "James Smith", "james.smith@example.com", "Admin", "Active"),
                new User("U002", "Maya Wong", "maya.wong@example.com", "Faculty", "Active"),
                new User("U003", "Alex Rivera", "alex.rivera@example.com", "Researcher", "Active"),
                new User("U004", "Thomas Kim", "thomas.kim@example.com", "Student", "Inactive"),
                new User("U005", "Lisa Davis", "lisa.davis@example.com", "Staff", "Active"),
                new User("U006", "Robert Chen", "robert.chen@example.com", "Faculty", "Suspended"),
                new User("U007", "Emily Brown", "emily.brown@example.com", "Admin", "Active")
        );
        usersTable.setItems(users);
        totalUsersLabel.setText(String.valueOf(users.size()));
    }

    @FXML
    private void handleAddUser() {
        showAddUserDialog();
    }

    private void showAddUserDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter user details");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        TextField idField = new TextField();
        idField.setPromptText("User ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "Faculty", "Staff", "Researcher", "Student");
        roleBox.setPromptText("Role");
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Active", "Inactive", "Suspended");
        statusBox.setPromptText("Status");

        content.getChildren().addAll(
                new Label("User ID:"), idField,
                new Label("Full Name:"), nameField,
                new Label("Email:"), emailField,
                new Label("Role:"), roleBox,
                new Label("Status:"), statusBox
        );

        dialog.getDialogPane().setContent(content);

        Button addButton = new Button("Add");
        addButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        addButton.setOnAction(e -> {
            if (!idField.getText().isEmpty() && !nameField.getText().isEmpty()) {
                User newUser = new User(
                        idField.getText(),
                        nameField.getText(),
                        emailField.getText(),
                        roleBox.getValue() != null ? roleBox.getValue() : "User",
                        statusBox.getValue() != null ? statusBox.getValue() : "Active"
                );
                users.add(newUser);
                totalUsersLabel.setText(String.valueOf(users.size()));
                dialog.close();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #E5E7EB; -fx-text-fill: #374151; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10, addButton, cancelButton);
        content.getChildren().add(buttonBox);

        dialog.showAndWait();
    }

    private void showEditUserDialog(User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Update user details for: " + user.getName());

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        TextField nameField = new TextField(user.getName());
        TextField emailField = new TextField(user.getEmail());
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "Faculty", "Staff", "Researcher", "Student");
        roleBox.setValue(user.getRole());
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Active", "Inactive", "Suspended");
        statusBox.setValue(user.getStatus());

        content.getChildren().addAll(
                new Label("Full Name:"), nameField,
                new Label("Email:"), emailField,
                new Label("Role:"), roleBox,
                new Label("Status:"), statusBox
        );

        dialog.getDialogPane().setContent(content);

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        saveButton.setOnAction(e -> {
            user.setName(nameField.getText());
            user.setEmail(emailField.getText());
            user.setRole(roleBox.getValue());
            user.setStatus(statusBox.getValue());
            usersTable.refresh();
            dialog.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #E5E7EB; -fx-text-fill: #374151; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        content.getChildren().add(buttonBox);

        dialog.showAndWait();
    }

    private void deleteUser(User user) {
        users.remove(user);
        totalUsersLabel.setText(String.valueOf(users.size()));
    }

    // User model class
    public static class User {
        private String userId;
        private String name;
        private String email;
        private String role;
        private String status;

        public User(String userId, String name, String email, String role, String status) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.role = role;
            this.status = status;
        }

        public String getUserId() { return userId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public String getStatus() { return status; }

        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setRole(String role) { this.role = role; }
        public void setStatus(String status) { this.status = status; }
    }
}
