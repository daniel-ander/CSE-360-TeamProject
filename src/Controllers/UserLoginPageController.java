package Controllers;

import javafx.stage.Stage;

import java.sql.SQLException;

import Models.User;
import UI.Elements.ResetPasswordPopup;
import UI.Pages.UserLoginPage;
import Utils.PasswordEvaluator;
import Utils.UserNameRecognizer;
import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPageController {
	
    private final DatabaseHelper databaseHelper;
    private final Stage primaryStage;
    private final UserLoginPage view = new UserLoginPage();
    private boolean DEBUG = true;

    public UserLoginPageController(DatabaseHelper databaseHelper, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
        
        setInitialLayout();
        
        setLoginButtonAction();
        if(DEBUG) {
        	setDebugEnvironment();
        }
    }
    
    
    private void setLoginButtonAction() {
    	view.getLoginButton().setOnAction(a -> {
	        // --- Retrieve user inputs ---
	        String userName = view.getUsernameField().getText();
	        String password = view.getPasswordField().getText();

	        // --- Flags ---
	        boolean resetPasswordFlag = password.startsWith("R_") && password.length() > 2;

	        // --- Username validation ---
	        if (!UserNameRecognizer.checkForValidUserName(userName)) {
	            view.getErrorLabel().setText("Invalid Username format: " + UserNameRecognizer.userNameRecognizerErrorMessage);
	            return;
	        }

	        // --- Password validation ---
	        if (!PasswordEvaluator.evaluatePassword(password)) {
	            view.getErrorLabel().setText("Invalid Password format: " + PasswordEvaluator.passwordErrorMessage);
	            return;
	        }

	        // --- Database check ---
	        try {
	            User user = new User(userName, password, "");
	            new WelcomeLoginPageController(databaseHelper, primaryStage, user);

	            String role = databaseHelper.getUserRole(userName);
	            if (role != null) {
	                user.setRole(role);
	                if (databaseHelper.login(user)) {
	                	if (resetPasswordFlag) {
	                	    ResetPasswordPopup resetPasswordPopup = new ResetPasswordPopup(databaseHelper);
	                	    resetPasswordPopup.show(userName);
	                	} else {
	                		// TOCHANGE
	                	    //welcomeLoginPage.show(primaryStage, user);
	                	}
	                } else {
	                    view.getErrorLabel().setText("Error logging in");
	                }
	            } else {
	                view.getErrorLabel().setText("User account doesn't exist");
	            }
	        } catch (SQLException e) {
	            System.err.println("Database error: " + e.getMessage());
	            e.printStackTrace();
	        }
	    });
	}
    
    private void setDebugEnvironment() {
    	view.getAdminInfoAutoFillButton().setText("AutoFill Admin Info (Debug)");
 		view.getAdminInfoAutoFillButton().setOnAction(a -> {
 			view.getUsernameField().setText("admin");
 			view.getPasswordField().setText("Password1!");
 		});
    }
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }

}

