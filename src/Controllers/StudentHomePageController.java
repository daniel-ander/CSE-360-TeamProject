package Controllers;

import Database.*;
import Sessions.UserSession;

import UI.Pages.StudentHomePage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;

import Utils.Debug;
import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class StudentHomePageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final DBConnectionStore dBConnectionStore;
	
	private final Stage primaryStage;
	private final StudentHomePage view;
	private final UserSession userSession;
	
	public StudentHomePageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, StudentHomePage view) {
		this.dBConnectionStore = dBConnectionStore;
        this.view = view;
        this.primaryStage = primaryStage;
        this.userSession = userSession;
        
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
    		userSession.setLoggedStatus(false);
	        new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
	    });
    }
    
    private void setChangeRolesButtonAction() {
    	view.getChangeRolesButton().setOnAction(a -> {
    		// TODO: -- Something weird happening here. Might need to make a DB call to update roles
	    	// Return to welcome page
//	    	String roleString = userDBManager.getUserRole(userSession.getUsername());
//	    	User.Role role = User.roleFromString(roleString);
//	    	
//	    	User user = new User(currentUser, "", role); // Password can be blank if not needed
	    	new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());

	    });
    }
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
    
    private final void setUpDebugEnvironment() {
    	view.addDebugLabel();
    }
}
