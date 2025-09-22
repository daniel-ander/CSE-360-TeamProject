package UI.Pages;


import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * This page displays a simple welcome message for the user.
 */

public class StudentHomePage {
	private final VBox layout = new VBox();
	private final Label userLabel = new Label(); // "Hello, " + currentUser + "!"
	private final Button logoutButton = new Button("Logout");
	private final Button changeRolesButton = new Button("Change Roles");
	private final Label debugLabel = new Label("DEBUG MODE: No interactive elements on this page.");
		
    // ===================
   	// Getters
   	// ===================
    
    public VBox getLayout() {
    	return layout;
    }
    
    public Button getLogoutButton() {
    	return logoutButton;
    }
    
    public Button getChangeRolesButton() {
    	return changeRolesButton;
    }
    
    public Label getDebugLabel() {
    	return debugLabel;
    }
    
    // ===================
   	// Setters
   	// ===================
    
    public void setUserLabel(String text) {
    	userLabel.setText("Hello, " + text + "!");
    }
    
    public void setInitialLayout(Stage primaryStage) {
    	layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
    	// Label to display Hello user
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    debugLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 12px;");
	    
	    
	    // Add all elements to layout

	    layout.getChildren().addAll(userLabel, logoutButton, changeRolesButton);
	    Scene userScene = new Scene(layout, 800, 400);
	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Student Home Page");

    }
}