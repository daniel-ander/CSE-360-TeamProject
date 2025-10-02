package Controllers;

import Models.User;
import UI.Pages.InstructorHomePage;
import UI.Pages.UserLoginPage;
import Utils.Debug;
import javafx.stage.Stage;
import databasePart1.*;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class InstructorHomePageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final DBConnectionStore dBConnectionStore;
	private final IUserDBManager userDBManager;
	private final Stage primaryStage;
	private final InstructorHomePage view;
	private final String currentUserName;
	
	public InstructorHomePageController(DBConnectionStore dBConnectionStore, Stage primaryStage, String currentUserName, InstructorHomePage view) {
		this.dBConnectionStore = dBConnectionStore;
        this.userDBManager = dBConnectionStore.getUserDatabaseManager();
        this.view = view;
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
	         new UserLoginPageController(dBConnectionStore, primaryStage, currentUserName, new UserLoginPage());
	    });
	}
    
    private void setChangeRolesButtonAction() {
    	view.getChangeRolesButton().setOnAction(a -> {
	    	String roleString = userDBManager.getUserRole(currentUserName);
	    	User.Role role = User.roleFromString(roleString);
	    	
	    	User user = new User(currentUserName, "", role); // Password can be blank if not needed
	    	// Return to welcome page
	    	new WelcomeLoginPageController(dBConnectionStore, primaryStage, user);

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
