package Controllers;

import Models.User;

import javafx.stage.Stage;
import java.sql.SQLException;

import Sessions.UserSession;

import UI.Pages.SetupAccountPage;
import UI.Pages.WelcomeLoginPage;

import Utils.Debug;
import Utils.PasswordEvaluator;
import Utils.UserNameRecognizer;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPageController {

	private final DBConnectionStore dBConnectionStore;
	private final IInvitationRepository invitationRepository;
    private final IUserRepository userRepository;
    private final Stage primaryStage;
    private final SetupAccountPage view;
    
    // DatabaseHelper to handle database operations.
    public SetupAccountPageController(DBConnectionStore dBConnectionStore, Stage primaryStage, SetupAccountPage view) {
    	this.dBConnectionStore = dBConnectionStore;
        this.invitationRepository = dBConnectionStore.getInvitationRepository();
        this.userRepository = dBConnectionStore.getUserRepository();
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
  	// Setters
  	// ===================
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    	
    	if(Debug.isEnabled) {
    		invitationRepository.printInvitationCodesTable();
    	}
    }
    
    private void setSetupButtonAction() {
    	view.getSetupButton().setOnAction(a -> {
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
            	if(!userRepository.doesUserExist(userName)) {
            		
            		// Validate the invitation code
            		if(invitationRepository.validateInvitationCode(code)) {
            			// Create a new user and register them in the database
		            	User user = new User(userName, password, User.Role.student);
		            	UserSession userSession = new UserSession(user);
                  
		            	// TODO: - One of these doesn't belong.
		            	//User user = new User(userName, password, databaseHelper.getRoleFromInviteCode(code));
		            	userRepository.register(user);
		                
		             // Navigate to the Welcome Login Page
		                new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());
            		}
            		else {
            			view.setErrorMessage("Please enter a valid invitation code");
            		}
            	}
            	else {
            		view.setErrorMessage("This useruserName is taken!!.. Please use another to setup an account");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });
	}
}