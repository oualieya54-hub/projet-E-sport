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
            // Load FormationView
            URL formationUrl = getClass().getResource("/FormationView.fxml");
            
            if (formationUrl == null) {
                System.err.println("ERREUR : Impossible de trouver le fichier FormationView.fxml !");
                System.exit(1);
            }

            Parent formationRoot = FXMLLoader.load(formationUrl);
            Scene formationScene = new Scene(formationRoot);
            primaryStage.setTitle("eSports Academy & Coaching - Gestion Formation");
            primaryStage.setScene(formationScene);
            primaryStage.show();

            // Load SessionView
            URL sessionUrl = getClass().getResource("/SessionView.fxml");
            
            if (sessionUrl == null) {
                System.err.println("ERREUR : Impossible de trouver le fichier SessionView.fxml !");
            } else {
                Parent sessionRoot = FXMLLoader.load(sessionUrl);
                Scene sessionScene = new Scene(sessionRoot);
                Stage sessionStage = new Stage();
                sessionStage.setTitle("eSports Academy & Coaching - Gestion Session");
                sessionStage.setScene(sessionScene);
                sessionStage.show();
            }
        }
    }//

    public static void main(String[] args) {
        // Launch the JavaFX application directly from this main class
        Application.launch(EsportApp.class, args);
    }
}
