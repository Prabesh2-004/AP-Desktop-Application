package com.project.apdesktopapplication.controllers;

import com.project.apdesktopapplication.models.Resource;
import com.project.apdesktopapplication.models.User;
import com.project.apdesktopapplication.services.ResourceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ResourceController {
    @FXML private TableView<Resource> resourceTable;
    @FXML private TableColumn<Resource, String> colId;
    @FXML private TableColumn<Resource, String> colName;
    @FXML private TableColumn<Resource, String> colType;
    @FXML private TableColumn<Resource, String> colLocation;
    @FXML private TableColumn<Resource, Integer> colCapacity;
    @FXML private TableColumn<Resource, String> colStatus;
    @FXML private TableColumn<Resource, Void> colActions;
    @FXML private ComboBox<String> typeFilter;
    @FXML private ComboBox<String> locationFilter;
    @FXML private ComboBox<String> capacityFilter;
    @FXML private ToggleButton availableNowToggle;
    @FXML private Button filterIconButton;
    @FXML private Label showingLabel;
    @FXML private Label totalResourcesLabel;

    private ResourceService resourceService = ResourceService.getInstance();
    private ObservableList<Resource> allResources;
    private ObservableList<Resource> filteredResources;
    private User currentUser;

    @FXML
    public void initialize() {
        setupTable();
        setupFilters();
        loadData();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("resourceId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        // This line was missing - without it the column has no value to show,
        // so the cell factory below always renders an empty/null cell.
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Status column with color coding
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

        // Actions column with Book button
        colActions.setCellFactory(column -> new TableCell<Resource, Void>() {
            private final Button bookBtn = new Button("📝 Book");

            {
                bookBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 4 12 4 12; -fx-cursor: hand; -fx-font-weight: bold;");
                bookBtn.setOnAction(e -> {
                    Resource resource = getTableView().getItems().get(getIndex());
                    if (resource != null && "AVAILABLE".equals(resource.getStatus())) {
                        openBookingDialog(resource);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Resource resource = getTableView().getItems().get(getIndex());
                if (resource != null && "AVAILABLE".equals(resource.getStatus())) {
                    setGraphic(bookBtn);
                } else {
                    setGraphic(null);
                }
            }
        });
    }

    private void setupFilters() {
        typeFilter.getItems().add("All Types");
        typeFilter.getItems().addAll(resourceService.getAllTypes());
        typeFilter.setValue("All Types");

        locationFilter.getItems().add("All Locations");
        locationFilter.getItems().addAll(resourceService.getAllLocations());
        locationFilter.setValue("All Locations");

        capacityFilter.getItems().addAll("Any Size", "1-5", "6-10", "11-20", "21-50", "50+");
        capacityFilter.setValue("Any Size");
    }

    private void loadData() {
        allResources = FXCollections.observableArrayList(resourceService.getAllResources());
        applyFilters();
        totalResourcesLabel.setText("Total: " + allResources.size() + " resources");
    }

    @FXML
    private void applyFilters() {
        filteredResources = FXCollections.observableArrayList(allResources);

        String type = typeFilter.getValue();
        if (type != null && !type.equals("All Types")) {
            filteredResources.removeIf(r -> !r.getType().equals(type));
        }

        String location = locationFilter.getValue();
        if (location != null && !location.equals("All Locations")) {
            filteredResources.removeIf(r -> !r.getLocation().equals(location));
        }

        String capacity = capacityFilter.getValue();
        if (capacity != null && !capacity.equals("Any Size")) {
            filteredResources.removeIf(r -> {
                int cap = r.getCapacity();
                switch (capacity) {
                    case "1-5": return cap < 1 || cap > 5;
                    case "6-10": return cap < 6 || cap > 10;
                    case "11-20": return cap < 11 || cap > 20;
                    case "21-50": return cap < 21 || cap > 50;
                    case "50+": return cap <= 50;
                    default: return false;
                }
            });
        }

        if (availableNowToggle.isSelected()) {
            filteredResources.removeIf(r -> !r.getStatus().equals("AVAILABLE"));
        }

        resourceTable.setItems(filteredResources);
        updateShowingLabel();
    }

    private void updateShowingLabel() {
        int size = filteredResources.size();
        int total = allResources.size();
        showingLabel.setText("Showing " + size + " of " + total + " resource" + (total != 1 ? "s" : ""));
    }

    private void openBookingDialog(Resource resource) {
        try {
            if (currentUser == null) {
                showError("Please login first to book resources.");
                return;
            }

            System.out.println("Opening booking dialog for: " + resource.getName());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/apdesktopapplication/booking-form.fxml"));
            if (loader.getLocation() == null) {
                System.err.println("booking-form.fxml not found!");
                showError("Booking form not found.");
                return;
            }

            Parent root = loader.load();

            BookingFormController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setResource(resource);
            controller.setOnBookingComplete(() -> loadData());

            Stage stage = new Stage();
            stage.setTitle("Book Resource - " + resource.getName());
            stage.setScene(new Scene(root, 540, 650));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error opening booking form: " + e.getMessage());
        }
    }

    private void showError(String message) {
        System.err.println("Error: " + message);
    }
}