package Controllers;
import Models.User;

import Sessions.UserSession;

import UI.Pages.ForumPage;
import UI.Pages.StudentHomePage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;

import Utils.Debug;

import databasePart1.*;

import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class StudentHomePageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final DBConnectionStore dBConnectionStore;
	private final IUserDBManager userDBManager;
	
	private final Stage primaryStage;
	private final StudentHomePage view;
	private final UserSession userSession;
	
	public StudentHomePageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, StudentHomePage view) {
		this.dBConnectionStore = dBConnectionStore;
        this.userDBManager = dBConnectionStore.getUserDatabaseManager();
        this.view = view;
        this.primaryStage = primaryStage;
        this.userSession = userSession;
        
        setInitialLayout();
        
        setLogoutButtonAction();
        setChangeRolesButtonAction();
        setEnterForumButtonAction();
        
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
    private void setEnterForumButtonAction() {
		view.getEnterForumButton().setOnAction(a -> {
	    	// Enter the forum page
	    	new ForumPageController(dBConnectionStore, primaryStage, currentUser, new ForumPage());

	    });
	}
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
    
    private final void setUpDebugEnvironment() {
    	view.addDebugLabel();
    }
}
