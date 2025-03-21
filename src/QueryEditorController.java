
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class QueryEditorController {
    @FXML
    private VBox queryContainer; // Container for the editor
    private CodeEditor codeEditor; // Custom code editor component
    @FXML private HBox titleBar;

    private String queryTitle;
    private String queryType;
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        // Enable dragging functionality
        titleBar.setOnMousePressed(event -> {
            Stage stage = (Stage) titleBar.getScene().getWindow();
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        titleBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) titleBar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        codeEditor = new CodeEditor();
        queryContainer.getChildren().add(codeEditor); // Add editor to UI
    }

    public String getQueryText() {
        return codeEditor.getQueryText();
    }

    public void setQueryText(String text) {
        codeEditor.setQueryText(text);
    }

    public void setQueryData(String title, String content, String type) {
        this.queryTitle = title;
        this.queryType = type;
        setQueryText(content); // Use CodeEditor to set text
    }

    @FXML
    private void saveQueryEdit() {
        String newContent = getQueryText();

        if (KillSwitch.isBlocked()) {
            KillSwitch.showBlockedAlertAndExit();
        }

        try {
            if (queryType.equals("Pending")) {
                Files.writeString(Paths.get("Pending/" + queryTitle), newContent, StandardCharsets.UTF_8);
            } else if (queryType.equals("Deployed")) {
                Files.writeString(Paths.get("Deployed/" + queryTitle), newContent, StandardCharsets.UTF_8);
            } else { // Temp.json case
                updateTempQuery(queryTitle, newContent);
            }
            CustomAlert.show("Query saved successfully!", "Success");

        } catch (IOException e) {
            CustomAlert.show("Error saving query: " + e.getMessage(), "Error");

        }
    }

    private void updateTempQuery(String title, String newContent) {
        File file = new File("Temp.json");
        try {
            String content = Files.readString(file.toPath());
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject queryObj = jsonArray.getJSONObject(i);
                if (queryObj.getString("title").equals(title)) {
                    queryObj.put("query", newContent);
                    Files.writeString(file.toPath(), jsonArray.toString(4), StandardCharsets.UTF_8);
                    return;
                }
            }
        } catch (Exception e) {
            CustomAlert.show("Failed to save temp query!", "Error");
        }
    }

    @FXML
    private void closeEditor() {
        Stage stage = (Stage) queryContainer.getScene().getWindow();
        stage.close();
    }

//    private void showPopup(String message, String popTitle) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle(popTitle);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }

    public void copyQuery() {
        String newContent = getQueryText(); // Get the query text

        if (newContent != null && !newContent.isEmpty()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(newContent);
            clipboard.setContent(content);
        }
    }


}
