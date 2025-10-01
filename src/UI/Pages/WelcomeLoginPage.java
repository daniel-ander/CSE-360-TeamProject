package UI.Pages;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 *
 * 🔑 KEY FEATURES:
 * - Pulls user roles from the database.
 * - Shows a "Continue" button → navigates based on role.
 * - If user has multiple roles → pops up a ChoiceDialog to select one.
 * - Adds "Invite" button for admins.
 * - Quit button closes DB + exits app.
 */
public class WelcomeLoginPage {
	private final VBox layout = new VBox(5);
	private final Scene scene = new Scene(layout, 800, 400);
	// Page title / greeting
    private final Label welcomeLabel = new Label("Welcome!!");
    private final Button continueButton = new Button("Continue to your Page");
    private final Button quitButton = new Button("Quit");
    private final Button inviteButton = new Button("Invite");
    /**
     * Display the Welcome screen after login.
     * @param primaryStage The main JavaFX stage.
     * @param user The logged-in user object.
     */
    
    public WelcomeLoginPage() {
    	// import stylesheet
    	scene.getStylesheets().add(
    		getClass().getResource("/Styles/styles.css").toExternalForm()
    	);
    }

    // ===================
   	// Getters
   	// ===================
    
    public VBox getLayout() {
    	return layout;
    }
    
    public Button getContinueButton() {
    	return continueButton;
    }
    
    public Button getQuitButton() {
    	return quitButton;
    }
    
    public Button getInviteButton() {
    	return inviteButton;
    }
    
    // ===================
   	// Setters
   	// ===================
    
    public void setInitialLayout(Stage primaryStage) {    	
    	// Base layout container
        layout.getStyleClass().add("layout");
        welcomeLabel.getStyleClass().add("main-label");
        
        layout.getChildren().addAll(welcomeLabel, continueButton, quitButton, inviteButton);

        // Show the scene
        primaryStage.setScene(scene);
        primaryStage.setTitle("Welcome Page");
    }
}
