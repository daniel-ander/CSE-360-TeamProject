package Controllers;

import Models.User;
import UI.Pages.StaffHomePage;
import Utils.Debug;
import databasePart1.*;
import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class StaffHomePageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final RepositoryProvider repositoryProvider;
	private final IUserRepository userRepository;
	
	private final Stage primaryStage;
	private final StaffHomePage view = new StaffHomePage();
	private final String currentUser;
	
	public StaffHomePageController(RepositoryProvider repositoryProvider, Stage primaryStage, String currentUser) {
		this.repositoryProvider = repositoryProvider;
        this.userRepository = repositoryProvider.getUserRepository();
        
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        
        setInitialLayout();
        
        setLogoutButtonAction();
        setChangeToRolesButtonAction();
        
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
    
    private void setChangeToRolesButtonAction() {
    	view.getChangeRolesButton().setOnAction(a -> {
	    	// Return to welcome page
	    	String role = userRepository.getUserRole(currentUser);
	    	User user = new User(currentUser, "", role); // Password can be blank if not needed
	    	new WelcomeLoginPageController(repositoryProvider, primaryStage, user);

	    });
    }
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    	view.setUserLabel(currentUser);
    }
    
    private final void setUpDebugEnvironment() {
    	view.addDebugLabel();
    }
}