

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.Objects;

public class CustomAlert {

    public Button cancelBtn;
    @FXML private VBox root;
    @FXML private HBox titleBar;
    @FXML private Label alertTitle;
    @FXML private Label alertMessage;
    @FXML private Button closeBtn;
    @FXML private Button okBtn;
    @FXML private ImageView appIcon;

    private static Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        // Close button actions
        closeBtn.setOnAction(event -> stage.close());
        okBtn.setOnAction(event -> {

            stage.close();
        });

        // Load app icon
        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/data-analysis_icon-icons.com_52842.png")));
            appIcon.setImage(icon);
        } catch (NullPointerException e) {
            System.err.println("Warning: Application icon not found!");
        }

        // Enable window dragging
        titleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        titleBar.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    public static void show(String message, String title) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(CustomAlert.class.getResource("/fxml/CustomAlert.fxml"));
            Parent root = loader.load();

            // Get controller instance
            CustomAlert controller = loader.getController();
            controller.alertTitle.setText(title);
            controller.alertMessage.setText(message);


            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root, 530, 180));
            stage.showAndWait();



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void closeWindow() {
        if (stage != null) {
            stage.close();
        }
    }
}
