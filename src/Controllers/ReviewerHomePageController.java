package Controllers;


import Models.User;
import UI.Pages.ReviewerHomePage;

import UI.Pages.UserLoginPage;
import Utils.Debug;
import databasePart1.*;
import javafx.stage.Stage;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */

public class ReviewerHomePageController {

	/**
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
	
	// Reference to the DatabaseHelper for database interactions
	private final DBConnectionStore dBConnectionStore;
	private final IUserDBManager userDBManager;
	
	private final Stage primaryStage;
	private final ReviewerHomePage view;
	private final String currentUser;
		
	public ReviewerHomePageController(DBConnectionStore dBConnectionStore, Stage primaryStage, String currentUser, ReviewerHomePage view) {
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
		view.getLogoutButton().setOnAction(a -> {
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