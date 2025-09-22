package UI.Pages;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The SetupLoginSelectionPage class allows users to choose between setting up a new account
 * or logging into an existing account. It provides two buttons for navigation to the respective pages.
 */
public class SetupLoginSelectionPage {
	// Buttons to select Login / Setup options that redirect to respective pages
    private final Button setupButton = new Button("Don't have an account? Setup here");
    private final Button loginButton = new Button("Login");
    
    // ===================
   	// Getters
   	// ===================
    
    public Button getSetupButton() {
    	return setupButton;
    }
    
    public Button getLoginButton() {
    	return loginButton;
    }
    
    // ===================
   	// Setters
   	// ===================
    
    public void setInitialLayout(Stage primaryStage) {
    	VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(setupButton, loginButton);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
