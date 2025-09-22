package UI.Elements;  // or your package name

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

import Utils.PasswordEvaluator;
import databasePart1.*;

public class ResetPasswordPopup {
    private final DatabaseHelper databaseHelper;

    public ResetPasswordPopup(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(String userName) {
        Stage resetStage = new Stage();
        resetStage.setTitle("Reset Password");

        VBox layout = new VBox();
        Label label = new Label("Enter your new password:");
        PasswordField newPasswordField = new PasswordField();
        Label feedback = new Label();
        Button submitButton = new Button("Submit");

        submitButton.setOnAction(ev -> {
            String newPass = newPasswordField.getText();
            if (!PasswordEvaluator.evaluatePassword(newPass)) {
                feedback.setText("Password issues: " + PasswordEvaluator.passwordErrorMessage);
                return;
            }
            try {
                databaseHelper.updatePassword(userName, newPass);
                feedback.setText("Password updated. Please log in again.");
                resetStage.close();
            } catch (SQLException ex) {
                feedback.setText("Database error: " + ex.getMessage());
            }
        });

        layout.getChildren().addAll(label, newPasswordField, submitButton, feedback);

        Scene scene = new Scene(layout, 250, 120);
        resetStage.setScene(scene);
        resetStage.show();
    }
}
