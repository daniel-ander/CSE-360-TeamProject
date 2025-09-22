package Controllers;

import UI.Pages.FirstPage;
import databasePart1.*;
import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class FirstPageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final DatabaseHelper databaseHelper;
	private final Stage primaryStage;
	private final FirstPage view = new FirstPage();
	
	public FirstPageController(DatabaseHelper databaseHelper, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
        
        setInitialLayout();
        
        setContinueButtonAction();
    }
	
	// ===================
	// Setters
	// ===================
    
    private void setContinueButtonAction() {
		view.getContinueButton().setOnAction(a -> {
			new AdminSetupPageController(databaseHelper, primaryStage);
		});
	}
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
}
