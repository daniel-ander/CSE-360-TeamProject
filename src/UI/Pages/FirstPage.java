package UI.Pages;

import Controllers.AdminSetupPageController;
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
public class FirstPage {
	VBox layout = new VBox(5);
	Label userLabel = new Label("Welcome!\n This is the special Admin page\n Only the first user sees this!");
	Button continueButton = new Button("Continue");
	Scene firstPageScene = new Scene(layout, 800, 400);
    
	// ===================
  	// Getters
  	// ===================
    
    public Button getContinueButton() {
    	return continueButton;
    }
    
    // ===================
    // Setters
 	// ===================
    
    public void setInitialLayout(Stage primaryStage) {
    	layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
    	userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    	
    	layout.getChildren().addAll(userLabel, continueButton);

	    // Set the scene to primary stage
	    primaryStage.setScene(firstPageScene);
	    primaryStage.setTitle("First Page");
    	primaryStage.show();
	}
}