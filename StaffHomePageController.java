package Controllers;

import Database.*;

import Exceptions.DBExceptions.DataAccessException;

import Sessions.UserSession;

import UI.Pages.StaffHomePage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;

import Utils.Debug;

import javafx.stage.Stage;

/**
 * The StaffHomePageController class manages the staff home page.
 * It handles navigation actions such as logging out and changing roles,
 * initializes the layout, and provides debug options when enabled.
 */
public class StaffHomePageController {
	
	/**
	 *  Reference to the DatabaseHelper for database interactions
	 */
	private final DBConnectionStore dBConnectionStore;
	
	private final Stage primaryStage;
	private final StaffHomePage view;
	private final UserSession userSession;
	
    /**
     * Creates a new StaffHomePageController.
     * Initializes the layout, sets up button actions, and configures debug environment if enabled.
     */
	public StaffHomePageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, StaffHomePage view) {
		this.dBConnectionStore = dBConnectionStore;
        this.view = view;
        this.primaryStage = primaryStage;
        this.userSession = userSession;
        
        setInitialLayout();
        
        setLogoutButtonAction();
        setChangeToRolesButtonAction();
        
        if(Debug.isEnabled) {
        	setUpDebugEnvironment();
        }
    }
	
	// ===================
    // 		 SETTERS 
 	// ===================
    
    /**
     * Configures the logout button action.
     * Navigates back to the login page.
     */
    private void setLogoutButtonAction() {
    	view.getLogoutButton().setOnAction(_ -> {
	        // Return to login page
	        new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
	    });
    }
    
    /**
     * Configures the change roles button action.
     * Intended to update user roles in the database and return to the welcome page.
     * Currently includes placeholder logic for future SQL and UI updates.
     * TODO: implement this!!!
     */
    private void setChangeToRolesButtonAction() {
    	view.getChangeRolesButton().setOnAction(_ -> {
    		try {
//		    	User.Role role = userService.getUserRole(userSession.getCurrentUserName());
//		    	User user = new User(userSession.getCurrentUserName(), "", role); // Password can be blank if not needed
		    	
		    	// TODO: - SQL + Model + (UI Logic?)
    		} catch (DataAccessException e) {
	            System.err.println(e.getMessage());
	            e.printStackTrace();
	        }
	    	// Return to welcome page
	    	new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());
	    });
    }
    
    /**
     * Sets up the initial layout of the staff home page.
     */
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    	view.setUserLabel(userSession.getCurrentUserName());
    }
    
    private final void setUpDebugEnvironment() {
    	view.addDebugLabel();
    }
}