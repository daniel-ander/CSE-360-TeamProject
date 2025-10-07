package Controllers;

import Models.User;

import javafx.stage.Stage;

import java.sql.SQLException;

import Database.*;

import Database.Services.Interfaces.IUserService;
import Exceptions.DBExceptions.DataAccessException;
import UI.Pages.AdminSetupPage;
import UI.Pages.UserLoginPage;

import Utils.Debug;
import Utils.PasswordEvaluator;
import Utils.UserNameRecognizer;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */

// This is the page that the first User to login will be sent to. 
// 
public class AdminSetupPageController {
	
	private final DBConnectionStore dbConnectionStore;
    private final IUserService userService;
    private final Stage primaryStage;
    private final AdminSetupPage view;

    public AdminSetupPageController(DBConnectionStore dbConnectionStore, Stage primaryStage,  AdminSetupPage view) {
    	this.dbConnectionStore = dbConnectionStore;
    	this.userService = dbConnectionStore.getUserService();
        this.primaryStage = primaryStage;
        this.view = view;
        
        setInitialLayout();
        
        setSetupButtonAction();
        
        if(Debug.isEnabled) {
        	setupDebugEnvironment();
        }
    }
    
    // ===================
    // 		 SETTERS 
 	// ===================
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
    
    private void setSetupButtonAction() {
		view.getSetupButton().setOnAction(_ -> {
			// Retrieve user input
            String userName = view.getUserName();
            String password = view.getPassword();
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
            	User user=new User(userName, password, User.Role.admin);
                userService.register(user);
                
                System.out.println("Administrator setup completed.");
                
                // Navigate to the Welcome Login Page
                new UserLoginPageController(dbConnectionStore, primaryStage, new UserLoginPage());
            } catch (DataAccessException e) {
            	// TODO: - Handle the case where user couldn't be registered
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
		});
	}
    
    private void setupDebugEnvironment() {
		view.getAdminAutoFillButton().setText("AutoFill Admin Info (Debug)");
		view.getAdminAutoFillButton().setOnAction(_ -> {
	    	view.setUsername("admin");
	    	view.setPassword("Password1!");
		});
    }
}