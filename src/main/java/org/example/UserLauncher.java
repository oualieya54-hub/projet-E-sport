package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UserLauncher {

    public static class UserApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("/user/UserDashboard.fxml"));
            primaryStage.setTitle("Game Pilot - E-sport Academy (Espace Joueur)");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(UserApp.class, args);
    }
}
