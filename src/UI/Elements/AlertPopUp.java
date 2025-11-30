package UI.Elements;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class AlertPopUp {

    // Simple info alert
    public static void showInfo(String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);          // no header
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Example confirm dialog (OK / Cancel)
    public static boolean showConfirm(String msg) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText(null);
        alert.setContentText(msg);

        return alert.showAndWait()
                    .filter(button -> button == ButtonType.OK)
                    .isPresent();
    }
}