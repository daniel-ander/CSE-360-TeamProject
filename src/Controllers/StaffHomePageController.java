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
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class StaffHomePageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final DBConnectionStore dBConnectionStore;
	
	private final Stage primaryStage;
	private final StaffHomePage view;
	private final UserSession userSession;
	
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
    
    private void setLogoutButtonAction() {
    	view.getLogoutButton().setOnAction(_ -> {
	        // Return to login page
	        new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
	    });
    }
    
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
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    	view.setUserLabel(userSession.getCurrentUserName());
    }
    
    private final void setUpDebugEnvironment() {
    	view.addDebugLabel();
    }
}