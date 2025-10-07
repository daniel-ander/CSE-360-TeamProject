package Controllers;

import Models.User;

import javafx.stage.Stage;

import Database.*;

import Database.Services.Interfaces.IInvitationService;
import Database.Services.Interfaces.IUserService;

import Exceptions.DBExceptions.DataAccessException;

import Sessions.UserSession;

import UI.Pages.SetupAccountPage;
import UI.Pages.WelcomeLoginPage;

import Utils.Debug;
import Utils.PasswordEvaluator;
import Utils.UserNameRecognizer;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPageController {

	private final DBConnectionStore dBConnectionStore;
    private final IUserService userService;
    private final IInvitationService invitationService;
    private final Stage primaryStage;
    private final SetupAccountPage view;
    
    // DatabaseHelper to handle database operations.
    public SetupAccountPageController(DBConnectionStore dBConnectionStore, Stage primaryStage, SetupAccountPage view) {
    	this.dBConnectionStore = dBConnectionStore;
        this.userService = dBConnectionStore.getUserService();
        this.invitationService = dBConnectionStore.getInvitationService();
        this.primaryStage = primaryStage;
        this.view = view;
        
        setInitialLayout();
        
        setSetupButtonAction();
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    
    // ===================
    // 		 SETTERS 
 	// ===================
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    	
    	if(Debug.isEnabled) {
    		try {
    			invitationService.printInvitationCodesTable();
    		} catch(DataAccessException e) {
    			// TODO: - Handle the case where invitation codes table can't be printed
    			System.err.println(e.getMessage());
                e.printStackTrace();
    		}
    	}
    }
    
    private void setSetupButtonAction() {
    	view.getSetupButton().setOnAction(_ -> {
    		System.out.println("Button was pressed");
        	// Retrieve user input
            String userName = view.getUserName();
            String password = view.getPassword();
            String code = view.getInvitationCodeField().getText();
            
            // Validate userName and password using FSM classes
            if (!UserNameRecognizer.checkForValidUserName(userName)) {
			    view.setErrorMessage("Invalid Username format: " + UserNameRecognizer.userNameRecognizerErrorMessage);
			    return;
			}
            
            if (!PasswordEvaluator.evaluatePassword(password)) {
            	view.setErrorMessage("Password issues: " + PasswordEvaluator.passwordErrorMessage);
				return;
			}
            
            try {
            	// Check if the user already exists
            	if(!userService.doesUserExistByName(userName)) {
            		
            		// Validate the invitation code
            		if(invitationService.validateInvitationCode(code)) {
            			// Create a new user and register them in the database
		            	User user = new User(userName, password, User.Role.student);
		            	UserSession userSession = new UserSession(user);
                  
		            	// TODO: - One of these doesn't belong.
		            	//User user = new User(userName, password, databaseHelper.getRoleFromInviteCode(code));
		            	userService.register(user);
		                
		             // Navigate to the Welcome Login Page
		                new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());
            		}
            		else {
            			view.setErrorMessage("Please enter a valid invitation code");
            		}
            	}
            	else {
            		view.setErrorMessage("This user name is taken!!.. Please use another to setup an account");
            	}
            	
            } catch (DataAccessException e) {
            	// TODO: - Handle the several potential DB call failures
                System.err.println(e.getMessage());
                e.printStackTrace();
            } 
        });
	}
}