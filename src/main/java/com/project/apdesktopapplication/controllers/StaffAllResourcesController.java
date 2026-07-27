package com.project.apdesktopapplication.controllers;

import com.project.apdesktopapplication.models.Resource;
import com.project.apdesktopapplication.models.User;
import com.project.apdesktopapplication.services.ResourceService;
import com.project.apdesktopapplication.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class StaffAllResourcesController {
    @FXML private TableView<Resource> resourcesTable;
    @FXML private TableColumn<Resource, String> colId;
    @FXML private TableColumn<Resource, String> colName;
    @FXML private TableColumn<Resource, String> colType;
    @FXML private TableColumn<Resource, String> colLocation;
    @FXML private TableColumn<Resource, Integer> colCapacity;
    @FXML private TableColumn<Resource, String> colStatus;
    @FXML private TableColumn<Resource, String> colCreator;

    private User currentUser;
    private ResourceService resourceService = ResourceService.getInstance();
    private UserService userService = UserService.getInstance();
    private ObservableList<Resource> allResources;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadData();
    }

    @FXML
    public void initialize() {
        setupTable();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("resourceId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        colStatus.setCellFactory(column -> new TableCell<Resource, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "AVAILABLE":
                            setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold; -fx-background-color: #D1FAE5; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
                            break;
                        case "MAINTENANCE":
                            setStyle("-fx-text-fill: #EAB308; -fx-font-weight: bold; -fx-background-color: #FEF3C7; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
                            break;
                        case "BOOKED":
                            setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-background-color: #FEE2E2; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colCreator.setCellFactory(column -> new TableCell<Resource, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    User user = userService.getUserById(item);
                    setText(user != null ? user.getFullName() + " (" + user.getUsername() + ")" : item);
                }
            }
        });
        colCreator.setCellValueFactory(new PropertyValueFactory<>("creatorId"));
    }

    private void loadData() {
        allResources = FXCollections.observableArrayList(resourceService.getAllResources());
        resourcesTable.setItems(allResources);
    }
}