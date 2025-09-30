package Controllers;


import Models.User;
import UI.Pages.ReviewerHomePage;
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
	private final RepositoryProvider repositoryProvider;
	private final IUserRepository userRepository;
	
	private final Stage primaryStage;
	private final ReviewerHomePage view = new ReviewerHomePage();
	private final String currentUser;
		
	public ReviewerHomePageController(RepositoryProvider repositoryProvider, Stage primaryStage, String currentUser) {
		this.repositoryProvider = repositoryProvider;
		this.userRepository = repositoryProvider.getUserRepository();
		
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
	        new UserLoginPageController(repositoryProvider, primaryStage);
		});
	}
	
	private void setChangeRolesButtonAction() {
		view.getLogoutButton().setOnAction(a -> {
			// Return to welcome page
	    	String role = userRepository.getUserRole(currentUser);
	    	User user = new User(currentUser, "", role); // Password can be blank if not needed
	    	new WelcomeLoginPageController(repositoryProvider, primaryStage, user);
		});
	}
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
    
    private final void setUpDebugEnvironment() {
    	view.addDebugLabel();
    }
}