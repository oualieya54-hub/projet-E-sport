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
        @Override
        public void start(Stage primaryStage) throws Exception {
            // Load the UserView interface we created
            Parent root = FXMLLoader.load(getClass().getResource("/UserView.fxml"));
            
            Scene scene = new Scene(root);
            
            // Setup the primary stage
            primaryStage.setTitle("E-SPORT User Management");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }
}
