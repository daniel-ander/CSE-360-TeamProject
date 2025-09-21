package Controllers;

import UI.Pages.UserHomePage;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user.
 */

public class UserHomePageController {
	
	private final Stage primaryStage;
	private UserHomePage view = new UserHomePage();
	
	public UserHomePageController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        setInitialLayout();
    }
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
}