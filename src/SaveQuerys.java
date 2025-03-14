import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.Objects;

public class SaveQuerys extends Application {

    @Override
    public void start(Stage stage) {
        // Check if the application is blocked before initializing UI
        if (KillSwitch.isBlocked()) {
            KillSwitch.showBlockedAlertAndExit();
            return;
        }

        // Create and initialize SceneManager
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.switchScene("/fxml/savequery.fxml", 1280, 745);  // Load the main UI

        // Set window title
        stage.setTitle("Query Manager");

        // Load and set the application icon safely
        try {
            Image appIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("data-analysis_icon-icons.com_52842.png")));
            stage.getIcons().add(appIcon);
        } catch (NullPointerException e) {
            System.err.println("Warning: Application icon not found!");
        }

        // Show the application window
        stage.show();
    }

    public static void main(String[] args) {
        launch(args); // Start JavaFX application
    }
}
