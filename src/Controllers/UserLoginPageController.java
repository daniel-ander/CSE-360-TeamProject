package Controllers;

import javafx.stage.Stage;

import java.sql.SQLException;

import Models.User;

import Sessions.UserSession;

import UI.Elements.ResetPasswordPopup;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;
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
    private final IUserRepository userRepository;
    
    private final Stage primaryStage;
    private final UserLoginPage view;
    private final ResetPasswordPopup resetPasswordPopupView = new ResetPasswordPopup();
    
 // --- Retrieve user inputs ---
    private String userNameFieldText = "";
    private String passwordFieldText = "";

    public UserLoginPageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserLoginPage view) {
    	this.dBConnectionStore = dBConnectionStore;
        this.userRepository = dBConnectionStore.getUserRepository();
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

	            String roleString = userRepository.getUserRole(userNameFieldText);
	            User.Role role = User.roleFromString(roleString);
	            
	            if (role != null) {
	                user.setRole(role);
	                if (userRepository.login(user)) {
	                	if (resetPasswordFlag) {
	                	    new ResetPasswordPopup();
	                	} else {
	                		new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());
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
    		userNameFieldText = view.getUsernameField().getText();
    		passwordFieldText = view.getPasswordField().getText();
	        
            String newPass = resetPasswordPopupView.getPasswordPromptLabelText();
            if (!PasswordEvaluator.evaluatePassword(newPass)) {
            	resetPasswordPopupView.setFeedbackLabelText("Password issues: " + PasswordEvaluator.passwordErrorMessage);
                return;
            }
            try {
            	userRepository.updatePassword(userNameFieldText, newPass);
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
