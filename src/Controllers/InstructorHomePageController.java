package Controllers;

import Models.User;
import UI.Pages.InstructorHomePage;
import Utils.Debug;
import javafx.stage.Stage;
import databasePart1.*;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class InstructorHomePageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final DatabaseHelper databaseHelper;
	private final Stage primaryStage;
	private final InstructorHomePage view = new InstructorHomePage();
	private final String currentUserName;
	
	public InstructorHomePageController(DatabaseHelper databaseHelper, Stage primaryStage, String currentUserName) {
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
        this.currentUserName = currentUserName;
        
        setInitialLayout();
        
        setLogoutButtonAction();
        setChangeRolesButtonAction();
        
        if(Debug.isEnabled) {
        	setUpDebugEnvironment();
        }
	}
	
	// ===================
	// Setters
	// ===================
    
    private void setLogoutButtonAction() {
    	view.getLogoutButton().setOnAction(a -> {
	        // Return to login page
	         new UserLoginPageController(databaseHelper, primaryStage);
	    });
	}
    
    private void setChangeRolesButtonAction() {
    	// TOCHANGE currentUser
    	view.getChangeRolesButton().setOnAction(a -> {
	    	// Return to welcome page
//	    	String role = databaseHelper.getUserRole(currentUser);
	    	User user = new User(currentUserName, "", ""); // Password can be blank if not needed
	    	new WelcomeLoginPageController(databaseHelper, primaryStage, user);

	    });
	}
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    	view.setUserLabel(currentUserName);
    }
    
    private void setUpDebugEnvironment() {
    	view.addDebugLabel();
    }
}
