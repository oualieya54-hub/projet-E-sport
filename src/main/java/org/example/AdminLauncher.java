package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminLauncher {
    
    public static class AdminApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("/admin/AcademyMainView.fxml"));
            primaryStage.setTitle("Game Pilot - Administration");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(AdminApp.class, args);
    }
}
