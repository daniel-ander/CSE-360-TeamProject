package UI.Pages;

import UI.Interfaces.IPage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage implements IPage{
	private final VBox layout = new VBox(10);
	private final Scene scene = new Scene(layout, 800, 400);
	private final TextField userNameField = new TextField();
	private final PasswordField passwordField = new PasswordField();
	private final TextField inviteCodeField = new TextField();
	private final Label errorLabel = new Label();
	private final Button setupButton = new Button("Setup");
	
	public SetupAccountPage() {
    	// import stylesheet
    	scene.getStylesheets().add(
    		getClass().getResource("/Styles/styles.css").toExternalForm()
    	);
    }
	    
	// ===================
    // 		 GETTERS 
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
    // 		 SETTERS 
 	// ===================
    
    public void setErrorMessage(String text) {
    	errorLabel.setText(text);
    }
    
    public void setInitialLayout(Stage primaryStage) {
    	errorLabel.getStyleClass().addAll("auxilary-label", "error-label");
    	
    	userNameField.setPromptText("Enter User Name");
        userNameField.setMaxWidth(250);

        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        inviteCodeField.setPromptText("Enter Invitation Code");
        inviteCodeField.setMaxWidth(250);
        
    	
        layout.getStyleClass().add("layout");
        layout.getChildren().addAll(userNameField, passwordField,inviteCodeField, setupButton, errorLabel);
        

        primaryStage.setScene(scene);
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}