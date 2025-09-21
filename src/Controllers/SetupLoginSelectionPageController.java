package Controllers;
import UI.Pages.SetupLoginSelectionPage;
import UI.Pages.UserLoginPage;
import databasePart1.*;
import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class SetupLoginSelectionPageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final DatabaseHelper databaseHelper;
	private final Stage primaryStage;
	private final SetupLoginSelectionPage view = new SetupLoginSelectionPage();
	
	public SetupLoginSelectionPageController(DatabaseHelper databaseHelper, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
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
			new SetupAccountPageController(databaseHelper, primaryStage);
		});
	}
    
    private void setLoginButtonAction() {
		view.getLoginButton().setOnAction(a -> {
			new UserLoginPageController(databaseHelper, primaryStage);
		});
	}
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
}