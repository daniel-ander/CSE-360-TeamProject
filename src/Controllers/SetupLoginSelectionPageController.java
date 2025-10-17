package Controllers;

import Database.*;

import UI.Pages.SetupAccountPage;
import UI.Pages.SetupLoginSelectionPage;
import UI.Pages.UserLoginPage;

import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class SetupLoginSelectionPageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final DBConnectionStore dBConnectionStore;
	
	private final Stage primaryStage;
	private final SetupLoginSelectionPage view;
	
	public SetupLoginSelectionPageController(DBConnectionStore dBConnectionStore, Stage primaryStage, SetupLoginSelectionPage view) {
		this.dBConnectionStore = dBConnectionStore;
        this.primaryStage = primaryStage;
        this.view = view;
        
        setInitialLayout();
        
        setSetupButtonAction();
        
        setLoginButtonAction();
    }
	
	// ===================
    // 		 SETTERS 
 	// ===================
    
    private void setSetupButtonAction() {
		view.getSetupButton().setOnAction(_ -> {
			new SetupAccountPageController(dBConnectionStore, primaryStage, new SetupAccountPage());
		});
	}
    
    // TODO: -- Fix how user works
    private void setLoginButtonAction() {
		view.getLoginButton().setOnAction(_ -> {
			
			new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
		});
	}
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
}
