package Controllers;

import Models.User;
import UI.Pages.FirstPage;
import UI.Pages.InstructorHomePage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;
import databasePart1.*;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class InstructorHomePageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final DatabaseHelper databaseHelper;
	private final Stage primaryStage;
	private final InstructorHomePage view = new InstructorHomePage();
	
	public InstructorHomePageController(DatabaseHelper databaseHelper, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
        
        setInitialLayout()
        
        setLogoutButtonAction();
        
        setChangeRolesButtonAction();
	}
	
	// ===================
	// Setters
	// ===================
	
//	if (DEBUG) {
//        Label debugLabel = new Label("DEBUG MODE: No interactive elements on this page.");
//        debugLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 12px;");
//        layout.getChildren().add(debugLabel);
//    }   
    
    private void setLogoutButtonAction() {
    	view.getLogoutButton().setOnAction(a -> {
	        // Return to login page
	         new UserLoginPageController(databaseHelper, primaryStage);
	    });
	}
    
    private void setChangeRolesButtonAction() {
    	// TOCHANGE currentUser
    	view.getChangeRolesButton().setOnAction(a -> {
	    	// Return to welcome page
//	    	String role = databaseHelper.getUserRole(currentUser);
	    	User user = new User("", "", ""); // Password can be blank if not needed
	    	new WelcomeLoginPageController(databaseHelper, primaryStage, user);

	    });
	}
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
}
