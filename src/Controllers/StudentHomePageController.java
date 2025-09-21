package Controllers;
import Models.User;
import UI.Pages.StudentHomePage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;
import databasePart1.*;
import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class StudentHomePageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final DatabaseHelper databaseHelper;
	private final Stage primaryStage;
	private final StudentHomePage view = new StudentHomePage();
	private final String currentUser;
	private boolean DEBUG = true;
	
	public StudentHomePageController(DatabaseHelper databaseHelper, Stage primaryStage, String currentUser) {
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
    	view.getChangeRolesButton().setOnAction(a -> {
	    	// Return to welcome page
	    	String role = databaseHelper.getUserRole(currentUser);
	    	User user = new User(currentUser, "", role); // Password can be blank if not needed
	    	new WelcomeLoginPageController(databaseHelper, primaryStage, user);

	    });
    }
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    	
    	if(DEBUG) {
    		view.getLayout().getChildren().add(view.getDebugLabel());
    	}
    }
}