import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import javafx.scene.control.Tooltip;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class SaveQueryController {
    @FXML public CheckBox isTempQuery;
    @FXML public ListView<String> queryListView;
    public ListView queryListViewt;
    public ListView queryListViewd;
    public TextField searchDeployed;
    public TextField searchPending;
    public ImageView appIcon;
    @FXML private TextField taskNameField;
    @FXML private TextField databaseNameField;
    @FXML private Tab viewQueriesTab;
    @FXML private VBox queryContainer;
    @FXML private Button minimizeBtn;
    @FXML private HBox titleBar;
    private double xOffset = 0;
    private double yOffset = 0;



    private CodeEditor codeEditor;
    private static final String TEMP_QUERY_FILE = "Temp.json";

    public void initialize() {
        codeEditor = new CodeEditor(); // Initialize CodeEditor
        
        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/data-analysis_icon-icons.com_52842.png")));
            appIcon.setImage(icon);
        } catch (NullPointerException e) {
            System.err.println("Warning: Application icon not found!");
        }

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


        if (queryContainer != null) {
            queryContainer.getChildren().add(codeEditor);
        }

        if (viewQueriesTab != null) {
            viewQueriesTab.setOnSelectionChanged(event -> {
                if (viewQueriesTab.isSelected()) {
                    isViewList();
                }
            });
        }

        // Check if list views are initialized before using them
        if (queryListView != null) setupQueryListView(queryListView, true);
        if (queryListViewd != null) setupQueryListView(queryListViewd, false);
        if (queryListViewt != null) setupQueryListView(queryListViewt, false);
    }

    private void setupQueryListView(ListView<String> listView, boolean isPendingList) {
        listView.setCellFactory(param -> new ListCell<>() {
            private final HBox container = new HBox(10);
            private final Label queryLabel = new Label();
            private final Hyperlink openLink = new Hyperlink("Open");
            private final Hyperlink deleteLink = new Hyperlink("Delete");
            private final Hyperlink moveLink = new Hyperlink("Move to Deployed");
            private final Tooltip tooltip = new Tooltip();
            private final HBox buttonsContainer = new HBox(10);
            private final Region spacer = new Region(); // Spacer to push buttons to the right

            {
                // Set label properties
                queryLabel.setMaxWidth(400);
                queryLabel.setStyle("-fx-text-overrun: ellipsis;");
                openLink.setStyle("-fx-text-fill: #2196F3; -fx-underline: true;");
                deleteLink.setStyle("-fx-text-fill: #FF4444; -fx-underline: true;"); // Red for Delete
                moveLink.setStyle("-fx-text-fill: #4CAF50; -fx-underline: true;"); // Green for Move
                queryLabel.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(queryLabel, Priority.ALWAYS);

                // Tooltip for full filename on hover
                queryLabel.setOnMouseEntered(event -> {
                    if (getItem() != null) {
                        tooltip.setText(getItem());
                        tooltip.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                        queryLabel.setTooltip(tooltip);
                    }
                });

                // Spacer to ensure buttons stay right-aligned
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // Button alignment
                buttonsContainer.getChildren().addAll(spacer, openLink, deleteLink);
                if (isPendingList) {
                    buttonsContainer.getChildren().add(moveLink);
                }
                buttonsContainer.setAlignment(Pos.CENTER_RIGHT);

                // Add elements to container
                container.getChildren().addAll(queryLabel, buttonsContainer);
                container.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(container, Priority.ALWAYS);

                // Event handlers
                openLink.setOnAction(event -> openQuery(getItem(), getQueryType(listView)));
                deleteLink.setOnAction(event -> deleteQuery(getItem(), listView, getQueryType(listView)));
                moveLink.setOnAction(event -> moveToDeployed(getItem()));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    queryLabel.setText(item.length() > 20 ? item.substring(0, 20) + "..." : item);
                    setGraphic(container);
                }
            }
        });
    }

    /**
     * Helper method to determine query type based on ListView
     */
    private String getQueryType(ListView<String> listView) {
        if (listView == queryListView) return "Pending";
        if (listView == queryListViewd) return "Deployed";
        if (listView == queryListViewt) return "Temp.json";
        return "Unknown";
    }

    public String getQueryText() {
        return codeEditor.getQueryText();
    }

    private void deleteQuery(String item, ListView<String> listView, String location) {
        if (location.equals("Pending") || location.equals("Deployed")) {
            // Delete from the respective folder
            deleteFile(item, location, listView);
        } else if (location.equals("Temp.json")) {
            // Remove from Temp.json
            deleteFromTempJson(item, listView);
        }
    }

    private void deleteFile(String item, String folder, ListView<String> listView) {
        File file = new File(folder, item);

        if (file.exists() && file.delete()) {
            listView.getItems().remove(item);
        } else {
            CustomAlert.show("Failed to delete: " + item, "Error");
        }
    }

    private void deleteFromTempJson(String item, ListView<String> listView) {
        File tempFile = new File("Temp.json");

        try {
            String content = Files.readString(tempFile.toPath()).trim();
            if (content.isEmpty() || !content.startsWith("[")) {
                CustomAlert.show("Warning: Temp.json is empty or invalid.", "Error");
                return;
            }

            JSONArray jsonArray = new JSONArray(content);
            JSONArray updatedArray = new JSONArray();

            boolean itemRemoved = false;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject queryObject = jsonArray.getJSONObject(i);
                String title = queryObject.getString("title"); // Extract title

                if (title.equals(item)) {
                    itemRemoved = true; // Found item to remove
                } else {
                    updatedArray.put(queryObject); // Keep other items
                }
            }

            if (itemRemoved) {
                Files.writeString(tempFile.toPath(), updatedArray.toString(4), StandardCharsets.UTF_8);
                listView.getItems().remove(item);
            } else {
                CustomAlert.show("Item not found in Temp.json: " + item, "Error");
            }
        } catch (Exception e) {
            CustomAlert.show("Error removing from Temp.json: " + e.getMessage(), "Error");
        }
    }

    private void moveToDeployed(String item) {
        File sourceFile = new File("Pending" + File.separator + item);
        File destinationFile = new File("Deployed" + File.separator + item);

        if (sourceFile.exists()) {
            if (sourceFile.renameTo(destinationFile)) { // Move file
                queryListView.getItems().remove(item);  // Remove from Pending list
                queryListViewd.getItems().add(item);    // Add to Deployed list
            } else {
                CustomAlert.show("Failed to move: " + item, "Error");
            }
        } else {
            CustomAlert.show("File not found: " + item, "Error");
        }
    }

    private void openQuery(String item, String queryType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/queryeditor.fxml"));
            Parent root = loader.load();

            QueryEditorController editorController = loader.getController();
            String queryContent = loadQueryContent(item, queryType);
            editorController.setQueryData(item, queryContent, queryType);

            Stage stage = new Stage();
            stage.setTitle("Query Editor - " + item);
            stage.setScene(new Scene(root, 1200, 700));
            // Prevent full screen
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setFullScreen(false);

            // Disable maximize button
            stage.setResizable(false);
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("data-analysis_icon-icons.com_52842.png"))));
            stage.show();  // This ensures a new window opens every time
        } catch (IOException e) {
            CustomAlert.show("Error opening query editor: " + e.getMessage(), "Error");
        }
    }

    private String loadQueryContent(String item, String queryType) {
        File file;
        if (queryType.equals("Pending")) {
            file = new File("Pending/" + item);
        } else if (queryType.equals("Deployed")) {
            file = new File("Deployed/" + item);
        } else { // Temp.json case
            return loadTempQuery(item);
        }

        if (!file.exists()) {
            return "Error: File not found.";
        }

        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            return "Error loading file.";
        }
    }

    private String loadTempQuery(String item) {
        File file = new File("Temp.json");

        if (!file.exists()) {
            return "Error: Temp.json not found.";
        }

        try {
            String content = Files.readString(file.toPath());
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject queryObj = jsonArray.getJSONObject(i);
                if (queryObj.getString("title").equals(item)) {
                    return queryObj.getString("query");
                }
            }
        } catch (Exception e) {
            return "Error loading temp query.";
        }
        return "Query not found.";
    }

    private void isViewList() {
        loadQueryList(queryListView, "Pending");
        loadQueryList(queryListViewd, "Deployed");
        loadQueryListFromJson(queryListViewt, "Temp.json");
    }

    private void loadQueryList(ListView<String> listView, String folderName) {
        listView.getItems().clear();
        File directory = new File(folderName);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File[] queryFiles = directory.listFiles((dir, name) -> name.endsWith(".txt"));
        if (queryFiles != null) {
            Arrays.sort(queryFiles, Comparator.comparingLong(File::lastModified).reversed());
            for (File file : queryFiles) {
                listView.getItems().add(file.getName());
            }
        }
    }

    private void loadQueryListFromJson(ListView<String> listView, String jsonFileName) {
        listView.getItems().clear();
        File jsonFile = new File(jsonFileName);

        if (jsonFile.exists()) {
            try {
                String content = Files.readString(jsonFile.toPath()).trim();

                if (content.isEmpty() || !content.startsWith("[")) {
                    CustomAlert.show("Warning: Temp.json is empty or invalid. Resetting.", "Error");
                    return; // Don't try to parse empty or invalid JSON
                }

                JSONArray jsonArray = new JSONArray(content);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject queryObject = jsonArray.getJSONObject(i);  // ✅ Extract JSON object
                    String title = queryObject.getString("title");  // ✅ Get title
                    listView.getItems().add(title); // ✅ Add title to list
                }
            } catch (Exception e) {
                CustomAlert.show("Error loading Temp.json: " + e.getMessage(), "Error");
            }
        }
    }

    public void saveQuery() {
        String taskName = taskNameField.getText().trim();
        String databaseName = databaseNameField.getText().trim();
        String query = getQueryText();
        if (KillSwitch.isBlocked()) {
            KillSwitch.showBlockedAlertAndExit();
        }

        if (taskName.isEmpty() || databaseName.isEmpty() || query.isEmpty()) {
            CustomAlert.show("Please Enter All Details", "Error");
            return;
        }

        if (isTempQuery.isSelected()) {
            saveTempQuery(taskName, query);
        } else {
            saveLongQuery(taskName, databaseName, query);
        }

        CustomAlert.show("Query saved successfully!", "Success");
        clearFields();
    }

    private void saveLongQuery(String taskName, String databaseName, String query) {
        File dir = new File("Pending");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, taskName + "_" + databaseName + ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(query);
        } catch (IOException e) {
            e.printStackTrace();
            CustomAlert.show("Failed to save the query!", "Error");
        }
    }

    private void saveTempQuery(String taskName, String query) {
        File file = new File(TEMP_QUERY_FILE);
        JSONArray queriesArray = new JSONArray();



        // Check if file exists and has valid JSON content
        if (file.exists()) {
            try {
                String content = Files.readString(file.toPath()).trim();

                // If file is empty or invalid, reset to an empty array
                if (content.isEmpty() || !content.startsWith("[")) {
                    queriesArray = new JSONArray();
                } else {
                    queriesArray = new JSONArray(content);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                queriesArray = new JSONArray(); // Reset to empty array if reading/parsing fails
            }
        }

        // Create a new JSON object for the query
        JSONObject newQuery = new JSONObject();
        newQuery.put("id", queriesArray.length() + 1);
        newQuery.put("title", taskName);
        newQuery.put("query", query);

        // Add new query to array
        queriesArray.put(newQuery);

        // Write updated data back to the file
        try (FileWriter writer = new FileWriter(file, false)) {  // 'false' to overwrite file
            writer.write(queriesArray.toString(4)); // 4 = pretty print indentation
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            CustomAlert.show("Failed to save temp query!", "Error");
        }
    }

    public void clearFields() {
        taskNameField.clear();
        databaseNameField.clear();
        codeEditor.setQueryText(""); // Clear the CodeEditor content
    }

    public void clearDate(ActionEvent actionEvent) {
        clearFields();
    }

    /*private void showPopup(String message, String popTitle) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(popTitle);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Get the dialog pane and apply dark theme
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        dialogPane.getStyleClass().add("dark-alert");

        alert.showAndWait();
    }
*/

    public void refreshList(ActionEvent actionEvent) {
        isViewList();
    }

    public void filterQueries(KeyEvent keyEvent) {
        String filter = ((TextField) keyEvent.getSource()).getText().toLowerCase();

        if (keyEvent.getSource() == searchPending) {
            filterList(queryListView, filter);
        } else if (keyEvent.getSource() == searchDeployed) {
            filterList(queryListViewd, filter);
        }
    }

    private void filterList(ListView<String> listView, String filter) {
        listView.getItems().clear();
        File directory = new File(listView == queryListView ? "Pending" : "Deployed");

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File[] queryFiles = directory.listFiles((dir, name) -> name.toLowerCase().contains(filter));
        if (queryFiles != null) {
            Arrays.sort(queryFiles, Comparator.comparingLong(File::lastModified).reversed());
            for (File file : queryFiles) {
                listView.getItems().add(file.getName());
            }
        }
    }

 // Ensure this matches the FXML button fx:id

    @FXML
    public void closeWindow() {
        // Closes the application safely
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void minimizeWindow() {
        Stage stage = (Stage) minimizeBtn.getScene().getWindow();
        stage.setIconified(true); // This minimizes the window
    }

}