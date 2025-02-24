import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.Objects;

public class SaveQuerys extends Application {

    @Override
    public void start(Stage stage) {
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.switchScene("/fxml/savequery.fxml",1280,720);  // Start with login screen
        stage.setTitle("Save Query App");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("data-analysis_icon-icons.com_52842.png"))));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
