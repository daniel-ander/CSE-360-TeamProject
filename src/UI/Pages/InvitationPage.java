package UI.Pages;


import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */

public class InvitationPage {

	/**
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
	
	private final VBox layout = new VBox();
	private final Label userLabel = new Label("Invite ");						  // Label to display the title of the page
	private final Button showCodeButton = new Button("Generate Invitation Code"); // Button to generate the invitation code
	private final Label inviteCodeLabel = new Label("");						  // Label to display the generated invitation code
	private final Scene inviteScene = new Scene(layout, 800, 400);
    
    // ===================
   	// Getters
   	// ===================
     
    public Button getShowCodeButton() {
    	return showCodeButton;
    }
    
    public Label getInviteCodeLabel() {
    	return inviteCodeLabel;
    }
    
    // ===================
   	// Setters
   	// ===================
    
    public void setInitialLayout(Stage primaryStage) {
    	layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
    	userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    	inviteCodeLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
    	
    	layout.getChildren().addAll(userLabel, showCodeButton, inviteCodeLabel);
    	
    	// Set the scene to primary stage
	    primaryStage.setScene(inviteScene);
	    primaryStage.setTitle("Invite Page");
    }
    
    
}