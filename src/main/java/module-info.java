module com.project.apdesktopapplication {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.project.apdesktopapplication to javafx.fxml;
    exports com.project.apdesktopapplication;
}