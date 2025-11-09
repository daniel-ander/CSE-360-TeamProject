package Controllers;

import Models.User;

import Database.*;
import Database.Services.Interfaces.IUserService;

import Exceptions.DBExceptions.DataAccessException;

import Sessions.UserSession;

import UI.Pages.InstructorHomePage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;
import UI.Pages.InstructorRequestsPage;

import Utils.Debug;

import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class InstructorHomePageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final DBConnectionStore dBConnectionStore;
	private final IUserService userService;
	private final Stage primaryStage;
	private final InstructorHomePage view;
	private final UserSession userSession;
	
	public InstructorHomePageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, InstructorHomePage view) {
		this.dBConnectionStore = dBConnectionStore;
        this.userService = dBConnectionStore.getUserService();
        this.view = view;
        this.primaryStage = primaryStage;
        this.userSession = userSession;
        
        setInitialLayout();
        
        setLogoutButtonAction();
        setChangeRolesButtonAction();
        setManageRequestsButtonAction();
        
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
    
    private void setChangeRolesButtonAction() {
    	view.getChangeRolesButton().setOnAction(_ -> {
    		
    		try {
    			User.Role role = userService.getUserRole(userSession.getCurrentUserName());
    			// TODO: -- Change role in DB 
    		} catch (DataAccessException e) {
	            System.err.println(e.getMessage());
	            e.printStackTrace();
	        }
	    	
	    	// Return to welcome page
	    	new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());

	    });
	}

    // Add action to open the Manage Reviewer Requests page
    private void setManageRequestsButtonAction() {
        view.getManageRequestsButton().setOnAction(_ -> {
            // Create controller for the requests page and show it
            InstructorRequestsPageController reqController = new InstructorRequestsPageController(dBConnectionStore, primaryStage, userSession, new InstructorRequestsPage());
            reqController.show();
        });
    }
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    	view.setUserLabel(userSession.getCurrentUserName());
    }
    
    private void setUpDebugEnvironment() {
    	view.addDebugLabel();
    }
}