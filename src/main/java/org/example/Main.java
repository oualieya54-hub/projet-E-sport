package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main {
    
    public static class EsportApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            URL fxmlUrl = getClass().getResource("/AcademyMainView.fxml");
            
            if (fxmlUrl == null) {
                System.err.println("ERREUR : Impossible de trouver le fichier AcademyMainView.fxml !");
                System.exit(1);
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root);
            primaryStage.setTitle("Game - Pilot Backoffice Admin");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }//

    public static void main(String[] args) {
        // Launch the JavaFX application directly from this main class
        Application.launch(EsportApp.class, args);
    }
}
