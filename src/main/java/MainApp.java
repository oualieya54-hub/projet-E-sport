import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp {

    public static void main(String[] args) {
        // Launch the internal JavaFX App class
        Application.launch(App.class, args);
    }

    // The actual JavaFX Application logic is kept inside this static inner class
    public static class App extends Application {
        private static Stage primaryStage;

        @Override
        public void start(Stage stage) throws Exception {
            primaryStage = stage;
            // Load the Login interface initially
            switchScene("/LoginView.fxml", "E-SPORT - Login");
            primaryStage.show();
        }

        public static void switchScene(String fxmlFile, String title) {
            try {
                Parent root = FXMLLoader.load(App.class.getResource(fxmlFile));
                Scene scene = new Scene(root);
                primaryStage.setTitle(title);
                primaryStage.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
