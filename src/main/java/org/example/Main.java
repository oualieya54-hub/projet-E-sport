package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main {

    public static class GlobalApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            // Open Admin Window
            Stage adminStage = new Stage();
            Parent adminRoot = FXMLLoader.load(getClass().getResource("/admin/AcademyMainView.fxml"));
            adminStage.setTitle("Game Pilot - Administration");
            adminStage.setScene(new Scene(adminRoot));
            adminStage.show();

            // Open User Window
            Stage userStage = new Stage();
            Parent userRoot = FXMLLoader.load(getClass().getResource("/user/UserDashboard.fxml"));
            userStage.setTitle("Game Pilot - E-sport Academy (Espace Joueur)");
            userStage.setScene(new Scene(userRoot));
            userStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(GlobalApp.class, args);
    }
}
