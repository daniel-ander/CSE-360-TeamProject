package Controllers;

import Models.User;
import UI.Pages.StaffHomePage;
import UI.Pages.UserLoginPage;
import Utils.Debug;
import databasePart1.*;
import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class StaffHomePageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final DBConnectionStore dBConnectionStore;
	private final IUserDBManager userDBManager;
	
	private final Stage primaryStage;
	private final StaffHomePage view;
	private final String currentUser;
	
	public StaffHomePageController(DBConnectionStore dBConnectionStore, Stage primaryStage, String currentUser, StaffHomePage view) {
		this.dBConnectionStore = dBConnectionStore;
        this.userDBManager = dBConnectionStore.getUserDatabaseManager();
        this.view = view;
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        
        setInitialLayout();
        
        setLogoutButtonAction();
        setChangeToRolesButtonAction();
        
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
	        new UserLoginPageController(dBConnectionStore, primaryStage, currentUser, new UserLoginPage());
	    });
    }
    
    private void setChangeToRolesButtonAction() {
    	view.getChangeRolesButton().setOnAction(a -> {
	    	String roleString = userDBManager.getUserRole(currentUser);
	    	User.Role role = User.roleFromString(roleString);
	    	
	    	User user = new User(currentUser, "", role); // Password can be blank if not needed
	    	// Return to welcome page
	    	new WelcomeLoginPageController(dBConnectionStore, primaryStage, user);
	    });
    }
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    	view.setUserLabel(currentUser);
    }
    
    private final void setUpDebugEnvironment() {
    	view.addDebugLabel();
    }
}