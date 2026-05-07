module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Permet à JavaFX FXML d'injecter les @FXML dans les contrôleurs
    opens org.example.controller to javafx.fxml;

    // Permet à JavaFX (PropertyValueFactory) de lire les propriétés de tes modèles
    opens org.example.Model to javafx.base;

    exports org.example;
}
