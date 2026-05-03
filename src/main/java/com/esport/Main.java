package com.esport;

import com.esport.utile.MyDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Test DB connection
        try {
            MyDatabase.getConnection();
        } catch (Exception e) {
            System.err.println("DB Connection failed: " + e.getMessage());
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esport/views/MainView.fxml"));
        Parent root = loader.load();
        
        primaryStage.setTitle("Esport Manager - Perfect CRUD & Advanced Logic");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        MyDatabase.closeConnection();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
