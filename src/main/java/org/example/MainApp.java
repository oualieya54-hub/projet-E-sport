package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        URL fxmlUrl = getClass().getResource("/org/example/gui/view/FormationView.fxml");
        
        if (fxmlUrl == null) {
            System.err.println("ERREUR : Impossible de trouver le fichier FormationView.fxml !");
            System.err.println("Vérifiez qu'il se trouve bien dans src/main/resources/org/example/gui/view/");
            System.exit(1);
        }

        Parent root = FXMLLoader.load(fxmlUrl);

        // Set up the scene and stage
        Scene scene = new Scene(root);
        primaryStage.setTitle("eSports Academy & Coaching - Gestion");
        primaryStage.setScene(scene);
        
        // Show the window
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}
