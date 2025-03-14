import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class KillSwitch {

    public static boolean isBlocked() {
        LocalDate targetDate = LocalDate.parse("2025-05-15", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Get the current date
        LocalDate currentDate = LocalDate.now();

        if (currentDate.isAfter(targetDate)) {
            try {
                URL url = new URL("https://raw.githubusercontent.com/porfskylord/Query_Manager/master/status.txt"); // Correct URL
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String status = reader.readLine();
                reader.close();
                System.out.println(status.trim());
                return "BLOCKED".equalsIgnoreCase(status.trim());
            } catch (Exception e) {
                e.printStackTrace();
                return false; // If check fails, allow app to run
            }
        }
        else {
            return false;
        }
    }


    public static void showBlockedAlertAndExit() {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Access Denied");
            alert.setHeaderText("Application Blocked!");
            alert.setContentText("This application has been disabled by the AZAD.");
            alert.showAndWait();

            Platform.exit(); // Close the JavaFX application properly
            System.exit(0); // Ensure process terminates
        });
    }
}
