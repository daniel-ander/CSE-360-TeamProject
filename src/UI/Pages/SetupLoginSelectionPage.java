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
    private final VBox layout = new VBox(10);
    private final Scene scene = new Scene(layout, 800, 400);
    
    public SetupLoginSelectionPage() {
    	// import stylesheet
    	scene.getStylesheets().add(
    		getClass().getResource("/Styles/styles.css").toExternalForm()
    	);
    }
    
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
        layout.getStyleClass().add("layout");
        layout.getChildren().addAll(setupButton, loginButton);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
