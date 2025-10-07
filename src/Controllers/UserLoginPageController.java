package Controllers;

import Models.User;

import javafx.stage.Stage;

import Database.*;
import Database.Services.Interfaces.IUserService;

import Exceptions.DBExceptions.DataAccessException;

import Sessions.UserSession;

import UI.Elements.ResetPasswordPopupView;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;

import Utils.Debug;
import Utils.PasswordEvaluator;
import Utils.UserNameRecognizer;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPageController {
	
	private final DBConnectionStore dBConnectionStore;
    private final IUserService userService;
    
    private final Stage primaryStage;
    private final UserLoginPage view;
    private final ResetPasswordPopupView resetPasswordPopupView = new ResetPasswordPopupView();
    
 // --- Retrieve user inputs ---
    private String userNameFieldText = "";
    private String passwordFieldText = "";

    public UserLoginPageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserLoginPage view) {
    	this.dBConnectionStore = dBConnectionStore;
        this.userService = dBConnectionStore.getUserService();
        this.view = view;
        this.primaryStage = primaryStage;
        
        setInitialLayout();
        
        setLoginButtonAction();
        setSubmitButtonResetPasswordPopupAction();
        
        if(Debug.isEnabled) {
        	setDebugEnvironment();
        }
    }
    
    // TODO: set it up so each stroke updates field variables
    
    // ===================
    // 		 SETTERS 
 	// ===================
    
    private void setLoginButtonAction() {
    	view.getLoginButton().setOnAction(_ -> {
	        // --- Flags ---
	        boolean resetPasswordFlag = passwordFieldText.length() > 2 && passwordFieldText.startsWith("R_");
	        
	        userNameFieldText = view.getUsernameField().getText();
	        passwordFieldText = view.getPasswordField().getText();

	        // --- Username validation ---
	        if (!UserNameRecognizer.checkForValidUserName(userNameFieldText)) {
	        	view.setErrorLabelText("Invalid Username format: " + UserNameRecognizer.userNameRecognizerErrorMessage);
	            return;
	        }

	        // --- Password validation ---
	        if (!PasswordEvaluator.evaluatePassword(passwordFieldText)) {
	            view.setErrorLabelText("Invalid Password format: " + PasswordEvaluator.passwordErrorMessage);
	            return;
	        }

	        // --- Database check ---
	        try {
	            User user = new User(userNameFieldText, passwordFieldText, User.Role.student);
	            UserSession userSession = new UserSession(user);
	            userSession.setLoggedStatus(true);
	            
	            //new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());

	            User.Role role = userService.getUserRole(user.getUserName());
	            
	            if (role != null) {
	                user.setRole(role);
	                if (userService.doesUserExistByName(user.getUserName())) {
	                	if (resetPasswordFlag) {
	                	    new ResetPasswordPopupView();
	                	} else {
	                		new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());
	                	}
	                } else {
	                    view.setErrorLabelText("Error logging in");
	                }
	            } else {
	            	view.setErrorLabelText("User account doesn't exist");
	            }
	        } catch (DataAccessException e) {
	        	// TODO: - Handle the potential DB failures in the try clause
	            System.err.println(e.getMessage());
	            e.printStackTrace();
	        }
	    });
	}
    
    private void setSubmitButtonResetPasswordPopupAction() {
    	resetPasswordPopupView.getSubmitButton().setOnAction(_ -> {
    		userNameFieldText = view.getUsernameField().getText();
    		passwordFieldText = view.getPasswordField().getText();
	        
            String newPass = resetPasswordPopupView.getPasswordPromptLabelText();
            if (!PasswordEvaluator.evaluatePassword(newPass)) {
            	resetPasswordPopupView.setFeedbackLabelText("Password issues: " + PasswordEvaluator.passwordErrorMessage);
                return;
            }
            try {
            	userService.updatePasswordForUser(userNameFieldText);
                resetPasswordPopupView.setFeedbackLabelText("Password updated. Please log in again.");
                resetPasswordPopupView.closeStage();
            } catch (DataAccessException e) {
            	// TODO: - Handle the case where the user password could not be updated
            	resetPasswordPopupView.setFeedbackLabelText(e.getMessage());
            }
        });
    }
    
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
//    	resetPasswordPopupView.setInitialLayout();
    }
    
    private void setDebugEnvironment() {
    	view.getAdminInfoAutoFillButton().setText("AutoFill Admin Info (Debug)");
 		view.getAdminInfoAutoFillButton().setOnAction(_ -> {
 			view.getUsernameField().setText("admin");
 			view.getPasswordField().setText("Password1!");
 		});
    }

}
