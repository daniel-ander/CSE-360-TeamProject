package Controllers;

import javafx.stage.Stage;
import java.sql.SQLException;

import Models.User;
import UI.Pages.SetupAccountPage;
import UI.Pages.WelcomeLoginPage;
import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPageController {

    private final DatabaseHelper databaseHelper;
    private final Stage primaryStage;
    private final SetupAccountPage view = new SetupAccountPage();
    
    // DatabaseHelper to handle database operations.
    public SetupAccountPageController(DatabaseHelper databaseHelper, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
        
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
    }
    
    private void setSetupButtonAction() {
    	view.getSetupButton().setOnAction(a -> {
        	// Retrieve user input
            String userName = view.getUserNameField().getText();
            String password = view.getPasswordField().getText();
            String code = view.getInvitationCodeField().getText();
            
            try {
            	// Check if the user already exists
            	if(!databaseHelper.doesUserExist(userName)) {
            		
            		// Validate the invitation code
            		if(databaseHelper.validateInvitationCode(code)) {
            			
            			// Create a new user and register them in the database
		            	User user=new User(userName, password, "user");
		                databaseHelper.register(user);
		                
		             // Navigate to the Welcome Login Page
		                new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
            		}
            		else {
            			view.getErrorLabel().setText("Please enter a valid invitation code");
            		}
            	}
            	else {
            		view.getErrorLabel().setText("This useruserName is taken!!.. Please use another to setup an account");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });
	}
}
