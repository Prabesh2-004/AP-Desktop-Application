module com.project.apdesktopapplication {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.project.apdesktopapplication to javafx.fxml;
    opens com.project.apdesktopapplication.controllers to javafx.fxml;
    opens com.project.apdesktopapplication.models to javafx.base;

    exports com.project.apdesktopapplication;
    opens com.project.apdesktopapplication.utils to javafx.fxml;
}