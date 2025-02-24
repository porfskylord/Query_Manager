import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class SceneManager {
    private final Stage primaryStage;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void switchScene(String fxmlFile, int width, int height) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));
            Scene scene = new Scene(root, width, height);
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();  // Adjusts to fit new content properly
            // Prevent full screen
            primaryStage.setFullScreen(false);

            // Disable maximize button
            primaryStage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
