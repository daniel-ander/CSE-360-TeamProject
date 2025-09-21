package Controllers;


import Models.User;
import UI.Pages.ReviewerHomePage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;
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
	private final DatabaseHelper databaseHelper;
	private final Stage primaryStage;
	private final ReviewerHomePage view = new ReviewerHomePage();
	private final String currentUser;
		
	public ReviewerHomePageController(DatabaseHelper databaseHelper, Stage primaryStage, String currentUser) {
		this.databaseHelper = databaseHelper;
	    this.primaryStage = primaryStage;
	    this.currentUser = currentUser;
	        
	    setInitialLayout();
	    
	    setLogoutButtonAction();
	    setChangeRolesButtonAction();
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
	
	private void setChangeRolesButtonAction() {
		view.getLogoutButton().setOnAction(a -> {
			// Return to welcome page
	    	String role = databaseHelper.getUserRole(currentUser);
	    	User user = new User(currentUser, "", role); // Password can be blank if not needed
	    	new WelcomeLoginPageController(databaseHelper, primaryStage, user);
		});
	}
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
    
}