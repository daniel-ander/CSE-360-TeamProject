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
	private final RepositoryProvider repositoryProvider;
	private final Stage primaryStage;
	private final FirstPage view = new FirstPage();
	
	public FirstPageController(RepositoryProvider repositoryProvider, Stage primaryStage) {
        this.repositoryProvider = repositoryProvider;
        this.primaryStage = primaryStage;
        
        setInitialLayout();
        
        setContinueButtonAction();
    }
	
	// ===================
	// Setters
	// ===================
    
    private void setContinueButtonAction() {
		view.getContinueButton().setOnAction(a -> {
			new AdminSetupPageController(repositoryProvider, primaryStage);
		});
	}
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
}
