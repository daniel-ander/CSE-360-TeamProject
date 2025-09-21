package Controllers;

import java.util.ArrayList;
import java.util.List;

import Controllers.AdminSetupPageController;
import UI.Pages.FirstPage;
import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
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
        
        setInitialLayout(primaryStage);
        
        setContinueButtonAction();
    }
    
    private void setContinueButtonAction() {
		view.getContinueButton().setOnAction(a -> {
			new AdminSetupPageController(databaseHelper, primaryStage);
		});
	}
    
    private void setInitialLayout(Stage primaryStage) {
    	view.setInitialLayout(primaryStage);
    }
}