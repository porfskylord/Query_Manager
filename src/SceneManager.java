import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class SceneManager {
    private final Stage primaryStage;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void switchScene(String fxmlFile, int width, int height) {
        try {
            // Load the FXML file
            URL fxmlUrl = getClass().getResource(fxmlFile);
            if (fxmlUrl == null) {
                throw new IOException("FXML file not found: " + fxmlFile);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Create Scene
            Scene scene = new Scene(root, width, height);

            // Load and apply the stylesheet
            URL cssUrl = getClass().getResource("style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Warning: dark-theme.css not found!");
            }

            // Set the new scene on the stage
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();  // Adjust size based on content
            primaryStage.initStyle(StageStyle.UNDECORATED);

            // UI Constraints
            primaryStage.setFullScreen(false);  // Prevent full screen
            primaryStage.setResizable(false);   // Disable maximize button

        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
