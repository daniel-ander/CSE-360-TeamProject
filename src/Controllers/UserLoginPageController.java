package Controllers;

import javafx.stage.Stage;

import java.sql.SQLException;

import Models.User;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;
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

    public UserLoginPageController(DatabaseHelper databaseHelper, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
        
        setInitialLayout();
        
        setAdminAutoFillButtonAction();
        setLoginButtonAction();
    }
    
    private void setAdminAutoFillButtonAction() {
    	view.getAdminInfoAutoFillButton().setOnAction(a -> {
	    	view.getUsernameField().setText("admin");
	    	view.getPasswordField().setText("Password1!");
	    });
	}
    
    private void setLoginButtonAction() {
    	view.getLoginButton().setOnAction(a -> {																	// this is where we're going to check credentials against our FSM classes
        	// Retrieve user inputs
        	String userName = view.getUsernameField().getText();
        	String password = view.getPasswordField().getText();

        	// validate against our FSM classes
        	if (!UserNameRecognizer.checkForValidUserName(userName)) {
        	    view.getErrorLabel().setText("Invalid Username format: " + UserNameRecognizer.userNameRecognizerErrorMessage);
        	    return;
        	}
        	if (!PasswordEvaluator.evaluatePassword(password)) {
        	    view.getErrorLabel().setText("Password issues: " + PasswordEvaluator.passwordErrorMessage);
        	    return;
        	}

        	// if FSM validates then compare against the database
        	try {
        	    User user = new User(userName, password, "");
        	    WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);

        	    String role = databaseHelper.getUserRole(userName);
        	    if (role != null) {
        	        user.setRole(role);
        	        if (databaseHelper.login(user)) {
        	            welcomeLoginPage.show(primaryStage, user);
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
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }

    
}