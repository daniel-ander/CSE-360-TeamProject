package Controllers;

import javafx.stage.Stage;

import java.sql.SQLException;

import Models.User;
import UI.Elements.ResetPasswordPopup;
import UI.Pages.UserLoginPage;
import Utils.Debug;
import Utils.PasswordEvaluator;
import Utils.UserNameRecognizer;
import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPageController {
	
	private final DBConnectionStore dBConnectionStore;
    private final IUserDBManager userDBManager;
    
    private final Stage primaryStage;
    private final UserLoginPage view;
    private final ResetPasswordPopup resetPasswordPopupView = new ResetPasswordPopup();
    
 // --- Retrieve user inputs ---
    private String userName = "";
    private String password = "";

    public UserLoginPageController(DBConnectionStore dBConnectionStore, Stage primaryStage, String userName, UserLoginPage view) {
    	this.dBConnectionStore = dBConnectionStore;
        this.userDBManager = dBConnectionStore.getUserDatabaseManager();
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
    
    
    private void setLoginButtonAction() {
    	view.getLoginButton().setOnAction(a -> {
	        // --- Flags ---
	        boolean resetPasswordFlag = password.length() > 2 && password.startsWith("R_");
	        
	        userName = view.getUsernameField().getText();
	        password = view.getPasswordField().getText();

	        // --- Username validation ---
	        if (!UserNameRecognizer.checkForValidUserName(userName)) {
	        	view.setErrorLabelText("Invalid Username format: " + UserNameRecognizer.userNameRecognizerErrorMessage);
	            return;
	        }

	        // --- Password validation ---
	        if (!PasswordEvaluator.evaluatePassword(password)) {
	            view.setErrorLabelText("Invalid Password format: " + PasswordEvaluator.passwordErrorMessage);
	            return;
	        }

	        // --- Database check ---
	        try {
	            User user = new User(userName, password, User.Role.student);
	            new WelcomeLoginPageController(dBConnectionStore, primaryStage, user);

	            String roleString = userDBManager.getUserRole(userName);
	            User.Role role = User.roleFromString(roleString);
	            
	            if (role != null) {
	                user.setRole(role);
	                if (userDBManager.login(user)) {
	                	if (resetPasswordFlag) {
	                	    new ResetPasswordPopup();
	                	} else {
	                		// TOCHANGE
	                		new WelcomeLoginPageController(dBConnectionStore, primaryStage, user);
//	                	    welcomeLoginPage.show(primaryStage, user);
	                	}
	                } else {
	                    view.setErrorLabelText("Error logging in");
	                }
	            } else {
	            	view.setErrorLabelText("User account doesn't exist");
	            }
	        } catch (SQLException e) {
	            System.err.println("Database error: " + e.getMessage());
	            e.printStackTrace();
	        }
	    });
	}
    
    private void setSubmitButtonResetPasswordPopupAction() {
    	resetPasswordPopupView.getSubmitButton().setOnAction(ev -> {
    		userName = view.getUsernameField().getText();
	        password = view.getPasswordField().getText();
	        
            String newPass = resetPasswordPopupView.getPasswordPromptLabelText();
            if (!PasswordEvaluator.evaluatePassword(newPass)) {
            	resetPasswordPopupView.setFeedbackLabelText("Password issues: " + PasswordEvaluator.passwordErrorMessage);
                return;
            }
            try {
            	userDBManager.updatePassword(userName, newPass);
                resetPasswordPopupView.setFeedbackLabelText("Password updated. Please log in again.");
                resetPasswordPopupView.closeStage();
            } catch (SQLException ex) {
            	resetPasswordPopupView.setFeedbackLabelText("Database error: " + ex.getMessage());
            }
        });
    }
    
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
//    	resetPasswordPopupView.setInitialLayout();
    }
    
    private void setDebugEnvironment() {
    	view.getAdminInfoAutoFillButton().setText("AutoFill Admin Info (Debug)");
 		view.getAdminInfoAutoFillButton().setOnAction(a -> {
 			view.getUsernameField().setText("admin");
 			view.getPasswordField().setText("Password1!");
 		});
    }

}
