package Controllers;

import Models.User;
import UI.Pages.StaffHomePage;
import databasePart1.*;
import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class StaffHomePageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final DatabaseHelper databaseHelper;
	private final Stage primaryStage;
	private final StaffHomePage view = new StaffHomePage();
	private final String currentUser;
	
	public StaffHomePageController(DatabaseHelper databaseHelper, Stage primaryStage, String currentUser) {
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        
        setInitialLayout();
        
        setLogoutButtonAction();
        setChangeToRolesButtonAction();
    }
	
	// ===================
	// Setters
	// ===================
    
    private void setLogoutButtonAction() {
    	view.getLogoutButton().setOnAction(a -> {
	        // Return to login page
	        UserLoginPageController loginPage = new UserLoginPageController(databaseHelper, primaryStage);
	    });
    }
    
    private void setChangeToRolesButtonAction() {
    	view.getChangeRolesButton().setOnAction(a -> {
	    	// Return to welcome page
	    	String role = databaseHelper.getUserRole(currentUser);
	    	User user = new User(currentUser, "", role); // Password can be blank if not needed
	    	new WelcomeLoginPageController(databaseHelper, primaryStage, user);

	    });
    }
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    	view.setUserLabel(currentUser);
    }
}