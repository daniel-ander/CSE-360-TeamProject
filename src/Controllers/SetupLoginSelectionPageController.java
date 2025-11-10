package Controllers;

import Database.*;

import UI.Pages.SetupAccountPage;
import UI.Pages.SetupLoginSelectionPage;
import UI.Pages.UserLoginPage;

import javafx.stage.Stage;

/**
 * The SetupLoginSelectionPageController class manages the setup/login selection page.
 * It allows the user to either set up a new account or navigate to the login page.
 * It also initializes the layout for the page.
 */
public class SetupLoginSelectionPageController {
	
	/**
	 *  Reference to the DatabaseHelper for database interactions
	 */
	private final DBConnectionStore dBConnectionStore;
	
	private final Stage primaryStage;
	private final SetupLoginSelectionPage view;
	
    /**
     * Creates a new SetupLoginSelectionPageController.
     * Initializes the layout and sets up button actions for setup and login.
     */
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
    
    /**
     * Configures the setup button action.
     * Navigates to the account setup page.
     */
    private void setSetupButtonAction() {
		view.getSetupButton().setOnAction(_ -> {
			new SetupAccountPageController(dBConnectionStore, primaryStage, new SetupAccountPage());
		});
	}
    
    /**
     *  TODO: -- Fix how user works
     *  Configures the login button action.
     *  Navigates to the user login page.
     */
    private void setLoginButtonAction() {
		view.getLoginButton().setOnAction(_ -> {
			
			new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
		});
	}
    
    /**
     * Sets up the initial layout of the setup/login selection page.
     */
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
}
