package Controllers;

import Database.*;
import UI.Pages.AdminSetupPage;
import UI.Pages.FirstPage;
import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class FirstPageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final DBConnectionStore dBConnectionStore;
	private final Stage primaryStage;
	private final FirstPage view;
	
	public FirstPageController(DBConnectionStore dBConnectionStore, Stage primaryStage, FirstPage view) {
        this.dBConnectionStore = dBConnectionStore;
        this.primaryStage = primaryStage;
        this.view = view;
        
        setInitialLayout();
        
        setContinueButtonAction();
    }
	
	// ===================
    // 		 SETTERS 
 	// ===================
    
    private void setContinueButtonAction() {
		view.getContinueButton().setOnAction(_ -> {
			new AdminSetupPageController(dBConnectionStore, primaryStage, new AdminSetupPage());
		});
	}
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
}
