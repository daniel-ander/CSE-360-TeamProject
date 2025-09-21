package UI.Pages;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */

// This is the page that the first User to login will be sent to. 
// 
public class AdminSetupPage {
	

	private boolean DEBUG = true; //toggle for debugging
	// Input fields for userName and password
    // Label to display error messages
    private final Label errorLabel = new Label(""); //creation of label, "blank" so its rendered early and nothing shifts around
	private final Label pageExplain = new Label("This page is where the first user will login\n they are automatically an admin\n Use Admin/Password1");
	
    private final TextField userNameField = new TextField();
    
    private final PasswordField passwordField = new PasswordField();
    
    private final Button setupButton = new Button("Setup");
    private final Button adminInfoAutoFillButton = new Button("Admin Login (TESTING)");

    
    // ===================
 	// Getters
 	// ===================
    
    public Button getSetupButton() {
    	return setupButton;
    }
    
    public Button getAdminAutoFillButton() {
    	return adminInfoAutoFillButton;
    }
    
    public TextField getUserNameField() {
    	return userNameField;
    }
    
    public PasswordField getPasswordField() {
    	return passwordField;
    }
    
    // ===================
    // Setters
 	// ===================
    
    public void setInitialLayout(Stage primaryStage) {
		// Set the scene to primary stage
    	errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
    	pageExplain.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");
    	userNameField.setPromptText("Enter Admin userName");
    	userNameField.setMaxWidth(250);
    	passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
    	
    	VBox layout = new VBox(10, userNameField, passwordField, setupButton, errorLabel,pageExplain, adminInfoAutoFillButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
        
        if (DEBUG) {
    		adminInfoAutoFillButton.setText("AutoFill Admin Info (Debug)");
    		adminInfoAutoFillButton.setOnAction(a -> {
		    	userNameField.setText("admin");
		    	passwordField.setText("Password1!");
    		});
        
        };
    }
    
    public void setErrorLabel(String text) {
    	errorLabel.setText(text);
    }
}
