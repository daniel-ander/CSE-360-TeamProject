package UI.Pages;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */

// This is the page that the first User to login will be sent to. 
// 
public class AdminSetupPage {

	// Input fields for userName and password
    // Label to display error messages
    private final Label errorLabel = new Label(); //creation of label, "blank" so its rendered early and nothing shifts around
	private final Label pageExplainLabel = new Label("This Account Will Be The Administrator Account");
	
    private final TextField userNameField = new TextField();
    
    private final PasswordField passwordField = new PasswordField();
    
    private final Button setupButton = new Button("Setup");
    private final Button adminInfoAutoFillButton = new Button("Admin Login (TESTING)");
    
    private final VBox layout = new VBox(10, userNameField, passwordField, setupButton, errorLabel,pageExplainLabel, adminInfoAutoFillButton);
    private final Scene scene = new Scene(layout, 800, 400);

    
    // ===================
 	// Getters
 	// ===================
    
    public Button getSetupButton() {
    	return setupButton;
    }
    
    public Button getAdminAutoFillButton() {
    	return adminInfoAutoFillButton;
    }
    
    public String getUserName() {
    	return userNameField.getText();
    }  
    
    public String getPassword() {
    	return passwordField.getText();
    }
    
    // ===================
    // Setters
 	// ===================
    
    public void setUsername(String text) {
    	userNameField.setText(text);
    }
    
    public void setPassword(String text) {
    	passwordField.setText(text);
    }
    
    public void setInitialLayout(Stage primaryStage) {		
    	layout.getStyleClass().add("layout");
    	errorLabel.getStyleClass().addAll("auxilary-label", "error-label");
    	pageExplainLabel.getStyleClass().addAll("auxilary-label", "info-label-secondary");
    	
    	userNameField.setPromptText("Enter Admin userName");
    	userNameField.setMaxWidth(250);
    	
    	passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
    	
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }
    
    public void setErrorLabel(String text) {
    	errorLabel.setText(text);
    }
}