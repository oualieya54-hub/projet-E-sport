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
            primaryStage.setMaximized(true);
            
            // Set the application icon in the title bar
            try {
                javafx.scene.image.Image appIcon = new javafx.scene.image.Image(App.class.getResourceAsStream("/images/logo.png"));
                primaryStage.getIcons().add(appIcon);
            } catch (Exception e) {
                System.out.println("Info: Could not load /images/logo.png. Please add the transparent logo there.");
            }

            // Load the Login interface initially
            switchScene("/commun/LoginView.fxml", "E-SPORT - Login");
            primaryStage.show();
        }

        public static void switchScene(String fxmlFile, String title) {
            try {
                Parent root = FXMLLoader.load(App.class.getResource(fxmlFile));
                
                // Just use the provided title directly to remove text branding
                primaryStage.setTitle(title);

                if (primaryStage.getScene() == null) {
                    Scene scene = new Scene(root, 1000, 700);
                    primaryStage.setScene(scene);
                } else {
                    // Preserve the current size if the user resized or maximized the window
                    double currentWidth = primaryStage.getWidth();
                    double currentHeight = primaryStage.getHeight();
                    boolean wasMaximized = primaryStage.isMaximized();

                    primaryStage.getScene().setRoot(root);
                    
                    // Root changes can sometimes trigger layout resets, so we re-apply dimensions if needed
                    if (!wasMaximized) {
                        primaryStage.setWidth(currentWidth);
                        primaryStage.setHeight(currentHeight);
                    }
                    primaryStage.setMaximized(wasMaximized);
                }

                primaryStage.setMinWidth(920);
                primaryStage.setMinHeight(680);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
