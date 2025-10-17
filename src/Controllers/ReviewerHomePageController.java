package Controllers;

import Database.*;

import Sessions.UserSession;

import UI.Pages.ReviewerHomePage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;

import Utils.Debug;
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
	private final UserSession userSession;
	
	private final Stage primaryStage;
	private final ReviewerHomePage view;
		
	public ReviewerHomePageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, ReviewerHomePage view) {
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
    // 		 SETTERS 
 	// ===================
	
	private void setLogoutButtonAction() {
		view.getLogoutButton().setOnAction(_ -> {
			// Return to login page
			// TODO: -- Need to empty UserSession
	        new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
		});
	}
	
	private void setChangeRolesButtonAction() {
		view.getChangeRolesButton().setOnAction(_ -> {
			// Return to welcome page
			// TODO: -- Needs to be DB call here along with UI update
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