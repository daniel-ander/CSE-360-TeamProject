package UI.Elements;  // or your package name

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ResetPasswordPopup {
	Stage resetStage = new Stage();
	
	VBox layout = new VBox();
	Scene scene = new Scene(layout, 250, 120);
	
    Label passwordPromptLabel = new Label("Enter your new password:");
    Label feedbackLabel = new Label();
    
    PasswordField newPasswordField = new PasswordField();
    
    Button submitButton = new Button("Submit");
    
    // ===================
  	// Getters
  	// ===================
     
    public Button getSubmitButton() {
    	return submitButton;
    }	
     
    public String getPasswordPromptLabelText() {
    	return passwordPromptLabel.getText();
    }
     
    // ===================
    // Setters
  	// ===================
     
    public void setFeedbackLabelText(String text) {
    	feedbackLabel.setText(text);
    }
     
    public void setInitialLayout() {
    	resetStage.setTitle("Reset Password");
    	
    	layout.getChildren().addAll(passwordPromptLabel, newPasswordField, submitButton, feedbackLabel);

        resetStage.setScene(scene);
        resetStage.show();
    }
    
    public void closeStage() {
    	resetStage.close();
    }
		
}
