package Controllers;

import javafx.stage.Stage;

import java.sql.SQLException;

import Models.User;
import UI.Pages.AdminSetupPage;
import UI.Pages.UserLoginPage;
import Utils.PasswordEvaluator;
import Utils.UserNameRecognizer;
import databasePart1.*;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */

// This is the page that the first User to login will be sent to. 
// 
public class AdminSetupPageController {
	
    private final DatabaseHelper databaseHelper;
    private final Stage primaryStage;
    private final AdminSetupPage view;

    public AdminSetupPageController(DatabaseHelper databaseHelper, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
        
        view = new AdminSetupPage();
        
        setInitialLayout(primaryStage);
        
        setSetupButtonAction();
    }
    
    // ===================
    // Setters
 	// ===================
    
    private void setInitialLayout(Stage primaryStage) {
    	view.setInitialLayout(primaryStage);
    }
    
    private void setSetupButtonAction() {
		view.getSetupButton().setOnAction(a -> {
			// Retrieve user input
            String userName = view.getUserNameField().getText();
            String password = view.getPasswordField().getText();
            // validate against our FSM classes
            // validate against our FSM classes
        	if (!UserNameRecognizer.checkForValidUserName(userName)) {
        	    view.setErrorLabel("Invalid Username format: " + UserNameRecognizer.userNameRecognizerErrorMessage);
        	    return;
        	}
        	if (!PasswordEvaluator.evaluatePassword(password)) {
        	    view.setErrorLabel("Password issues: " + PasswordEvaluator.passwordErrorMessage);
        	    return;
        	}
            
            try {
            	// Create a new User object with admin role and register in the database
            	User user=new User(userName, password, "admin");
                databaseHelper.register(user);
                System.out.println("Administrator setup completed.");
                
                // Navigate to the Welcome Login Page
                new UserLoginPage(databaseHelper).show(primaryStage);
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
		});
	}
}
