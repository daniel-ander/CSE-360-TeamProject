package Controllers;

import Models.User;

import javafx.stage.Stage;

import Config.PreLoader;
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

public class UserLoginPageController {
	
	private final DBConnectionStore dBConnectionStore;
    private final IUserService userService;
    
    private final Stage primaryStage;
    private final UserLoginPage view;
    private final ResetPasswordPopupView resetPasswordPopupView = new ResetPasswordPopupView();
    
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
        
        if (Debug.isEnabled) {
        	setDebugEnvironment();
        }
    }

    private void setLoginButtonAction() {
    	view.getLoginButton().setOnAction(_ -> {
	        userNameFieldText = view.getUsernameField().getText();
	        passwordFieldText = view.getPasswordField().getText();
	        
	        boolean resetPasswordFlag = passwordFieldText.length() > 2 && passwordFieldText.startsWith("R_");

	        // --- Validate username ---
	        if (!UserNameRecognizer.checkForValidUserName(userNameFieldText)) {
	        	view.setErrorLabelText("Invalid Username format: " + UserNameRecognizer.userNameRecognizerErrorMessage);
	            return;
	        }
	        
	        
	        // --- Validate password ---
	        if (!PasswordEvaluator.evaluatePassword(passwordFieldText)) {
	            view.setErrorLabelText("Invalid Password format: " + PasswordEvaluator.passwordErrorMessage);
	            return;
	        }

	        try {
	            // Pull the actual user from the DB
	        	System.out.println(String.format("%s", userNameFieldText));
	        	System.out.println(String.format("%s", passwordFieldText));

	        	User user = userService.getUserByNameAndPassword(userNameFieldText, passwordFieldText);
	            if (user == null) {
	                view.setErrorLabelText("Invalid username or password.");
	                return;
	            }

	            // Ensure we have the DB ID
	            if (Debug.isEnabled) {
	                System.out.println("Login successful for userId: " + user.getId() + " (" + user.getUserName() + ")");
	            }

	            // --- Initialize user session ---
	            UserSession userSession = new UserSession(user);
	            PreLoader.getInstance(dBConnectionStore, userSession).preload();
	            userSession.setLoggedStatus(true);

	            // --- Handle reset or normal login flow ---
	            if (resetPasswordFlag) {
	                new ResetPasswordPopupView();
	            } else {
	                new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());
	            }

	        } catch (DataAccessException e) {
	            System.err.println("Database access error during login: " + e.getMessage());
	            e.printStackTrace();
	            view.setErrorLabelText("Database error. Please try again later.");
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
            	resetPasswordPopupView.setFeedbackLabelText("Error updating password: " + e.getMessage());
            }
        });
    }

    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }

    private void setDebugEnvironment() {
    	view.studentInfoAutoFillButton().setText("AutoFill Student Info (Debug)");
    	view.studentInfoAutoFillButton().setOnAction(_ -> {
    		view.getUsernameField().setText("student");
    		view.getPasswordField().setText("Password1!");
    	});

    	view.getAdminInfoAutoFillButton().setText("AutoFill Admin Info (Debug)");
 		view.getAdminInfoAutoFillButton().setOnAction(_ -> {
 			view.getUsernameField().setText("admin");
 			view.getPasswordField().setText("Password1!");
 		});
 		
 		view.getReviewerInfoAutoFillButton().setText("AutoFill Reviewer Info (Debug)");
 		view.getReviewerInfoAutoFillButton().setOnAction(_ -> {
 			view.getUsernameField().setText("reviewer");
 			view.getPasswordField().setText("Password1!");
 		});
    }
}
