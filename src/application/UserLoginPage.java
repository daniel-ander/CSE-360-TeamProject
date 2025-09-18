package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
	
    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input field for the user's userName, password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");


        Button loginButton = new Button("Login");
	    Button adminInfoAutoFill = new Button("Admin Login (TESTING)");
	    adminInfoAutoFill.setOnAction(a -> {
	    	userNameField.setText("admin");
	    	passwordField.setText("Password1!");
	    });
        
	    loginButton.setOnAction(a -> {
	        // --- Retrieve user inputs ---
	        String userName = userNameField.getText();
	        String password = passwordField.getText();

	        // --- Flags ---
	        boolean resetPasswordFlag = password.startsWith("R_") && password.length() > 2;

	        // --- Username validation ---
	        if (!UserNameRecognizer.checkForValidUserName(userName)) {
	            errorLabel.setText("Invalid Username format: " + UserNameRecognizer.userNameRecognizerErrorMessage);
	            return;
	        }

	        // --- Password validation ---
	        if (!PasswordEvaluator.evaluatePassword(password)) {
	            errorLabel.setText("Invalid Password format: " + PasswordEvaluator.passwordErrorMessage);
	            return;
	        }

	        // --- Database check ---
	        try {
	            User user = new User(userName, password, "");
	            WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);

	            String role = databaseHelper.getUserRole(userName);
	            if (role != null) {
	                user.setRole(role);
	                if (databaseHelper.login(user)) {
	                	if (resetPasswordFlag) {
	                	    ResetPasswordPopup resetPasswordPopup = new ResetPasswordPopup(databaseHelper);
	                	    resetPasswordPopup.show(userName);
	                	} else {
	                	    welcomeLoginPage.show(primaryStage, user);
	                	}
	                } else {
	                    errorLabel.setText("Error logging in");
	                }
	            } else {
	                errorLabel.setText("User account doesn't exist");
	            }
	        } catch (SQLException e) {
	            System.err.println("Database error: " + e.getMessage());
	            e.printStackTrace();
	        }
	    });

	    

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel, adminInfoAutoFill);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}
