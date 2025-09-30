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
	
	private final RepositoryProvider repositoryProvider;
    private final IUserRepository userRepository;
    
    private final Stage primaryStage;
    private final UserLoginPage view = new UserLoginPage();
    private final ResetPasswordPopup resetPasswordPopupView = new ResetPasswordPopup();
    
 // --- Retrieve user inputs ---
    String userName = view.getUsernameField().getText();
    String password = view.getPasswordField().getText();

    public UserLoginPageController(RepositoryProvider repositoryProvider, Stage primaryStage) {
    	this.repositoryProvider = repositoryProvider;
        this.userRepository = repositoryProvider.getUserRepository();
        
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
	        boolean resetPasswordFlag = password.startsWith("R_") && password.length() > 2;
	        
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
	            User user = new User(userName, password, "");
	            new WelcomeLoginPageController(repositoryProvider, primaryStage, user);

	            String role = userRepository.getUserRole(userName);
	            if (role != null) {
	                user.setRole(role);
	                if (userRepository.login(user)) {
	                	if (resetPasswordFlag) {
	                	    new ResetPasswordPopup();
	                	} else {
	                		// TOCHANGE
	                		new WelcomeLoginPageController(repositoryProvider, primaryStage, user);
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
            	userRepository.updatePassword(userName, newPass);
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
