package Controllers;
import UI.Pages.SetupLoginSelectionPage;
import databasePart1.*;
import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class SetupLoginSelectionPageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final RepositoryProvider repositoryProvider;
	
	private final Stage primaryStage;
	private final SetupLoginSelectionPage view = new SetupLoginSelectionPage();
	
	public SetupLoginSelectionPageController(RepositoryProvider repositoryProvider, Stage primaryStage) {
		this.repositoryProvider = repositoryProvider;

        this.primaryStage = primaryStage;
        
        setInitialLayout();
        
        setSetupButtonAction();
        
        setLoginButtonAction();
    }
	
	// ===================
	// Setters
	// ===================
    
    private void setSetupButtonAction() {
		view.getSetupButton().setOnAction(a -> {
			new SetupAccountPageController(repositoryProvider, primaryStage);
		});
	}
    
    private void setLoginButtonAction() {
		view.getLoginButton().setOnAction(a -> {
			new UserLoginPageController(repositoryProvider, primaryStage);
		});
	}
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
}
