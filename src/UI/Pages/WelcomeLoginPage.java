package UI.Pages;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import Controllers.AdminHomePageController;
import Controllers.InvitationPageController;
import Controllers.UserHomePageController;
import Models.User;
import databasePart1.*;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
	private final VBox layout = new VBox(5);
	private final Label welcomeLabel = new Label("Welcome!!");
	private final Button continueButton = new Button("Continue to your Page");
	private final Button quitButton = new Button("Quit");
	private final Button inviteButton = new Button("Invite");
    
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
    	layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    layout.getChildren().addAll(welcomeLabel,continueButton,quitButton);
	    Scene welcomeScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
    }
}