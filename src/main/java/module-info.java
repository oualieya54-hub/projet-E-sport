module com.esport {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.esport to javafx.fxml;
    opens com.esport.controllers to javafx.fxml;

    exports com.esport;
    exports com.esport.controllers;
    exports com.esport.models;
}
