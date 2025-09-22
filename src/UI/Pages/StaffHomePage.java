package UI.Pages;


import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user.
 */

public class StaffHomePage {
	private final VBox layout = new VBox();
	private boolean DEBUG = true; //toggle for debugging
	private final Label userLabel = new Label(); // "Hello, " + currentUser + "!"
	private final Button logoutButton = new Button("Logout");	
	private final Button changeRolesButton = new Button("Change Roles");
	
    
    // ===================
   	// Getters
   	// ===================
    
    public Button getLogoutButton() {
    	return logoutButton;
    }
    
    public Button getChangeRolesButton() {
    	return changeRolesButton;
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
	    
	 // Debugging Area	    
	    if (DEBUG) {
	        Label debugLabel = new Label("DEBUG MODE: No interactive elements on this page.");
	        debugLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 12px;");
	        layout.getChildren().add(debugLabel);
	    }
	    
	    
	    layout.getChildren().addAll(userLabel, logoutButton, changeRolesButton);
	    Scene userScene = new Scene(layout, 800, 400);
	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Staff Home Page");
    }
}
