package UI.Pages;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	private final TextField userNameField = new TextField();
	private final PasswordField passwordField = new PasswordField();
	private final TextField inviteCodeField = new TextField();
	private final Label errorLabel = new Label();
	private final Button setupButton = new Button("Setup");
	    
    // ===================
   	// Getters
   	// ===================
    
    public Button getSetupButton() {
    	return setupButton;
    }
    
    public String getUserName() {
    	return userNameField.getText();
    }
    
    public String getPassword() {
    	return passwordField.getText();
    }
    
    public TextField getInvitationCodeField() {
    	return inviteCodeField;
    }
    
    // ===================
   	// Setters
   	// ===================
    
    public void setErrorMessage(String text) {
    	errorLabel.setText(text);
    }
    
    public void setInitialLayout(Stage primaryStage) {
    	userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
    	
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField,inviteCodeField, setupButton, errorLabel);
        

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}