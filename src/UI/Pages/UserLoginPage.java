package UI.Pages;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
    private final TextField userNameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Label errorLabel = new Label();
    private final Button loginButton = new Button("Login");
    private final Button adminInfoAutoFillButton = new Button("Admin Login (TESTING)");
    private final VBox layout = new VBox(10);
    
    // ===================
   	// Getters
   	// ===================
    
    public Button getAdminInfoAutoFillButton() {
    	return adminInfoAutoFillButton;
    }
    
    public Button getLoginButton() {
    	return loginButton;
    }
    
    public TextField getUsernameField() {
    	return userNameField;
    }
    
    public PasswordField getPasswordField() {
    	return passwordField;
    }
    
    // ===================
   	// Setters
   	// ===================
    
    public void setErrorLabelText(String text) {
    	errorLabel.setText(text);
    }
    
    public void setInitialLayout(Stage primaryStage) {
    	// Input field for the user's userName, password
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        // Label to display error messages
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel, adminInfoAutoFillButton);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}


