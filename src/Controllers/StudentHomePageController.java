package Controllers;
import Models.User;

import UI.Pages.StudentHomePage;
import UI.Pages.UserLoginPage;

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
	private final String currentUser;
	
	public StudentHomePageController(DBConnectionStore dBConnectionStore, Stage primaryStage, String currentUser, StudentHomePage view) {
		this.dBConnectionStore = dBConnectionStore;
        this.userDBManager = dBConnectionStore.getUserDatabaseManager();
        this.view = view;
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        
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
	        new UserLoginPageController(dBConnectionStore, primaryStage, currentUser, new UserLoginPage());
	    });
    }
    
    private void setChangeRolesButtonAction() {
    	view.getChangeRolesButton().setOnAction(a -> {
	    	// Return to welcome page
	    	String role = userDBManager.getUserRole(currentUser);
	    	User user = new User(currentUser, "", role); // Password can be blank if not needed
	    	new WelcomeLoginPageController(dBConnectionStore, primaryStage, user);

	    });
    }
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
    
    private final void setUpDebugEnvironment() {
    	view.addDebugLabel();
    }
}
