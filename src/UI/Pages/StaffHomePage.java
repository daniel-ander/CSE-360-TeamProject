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
	private final Scene scene = new Scene(layout, 800, 400);
	private final Label userLabel = new Label(); // "Hello, " + currentUser + "!"
	private final Button logoutButton = new Button("Logout");	
	private final Button changeRolesButton = new Button("Change Roles");
	
	private final Label debugLabel = new Label("DEBUG MODE: No interactive elements on this page.");
	
    
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
    	layout.getStyleClass().add("layout");
    	// Label to display Hello user
	    userLabel.getStyleClass().add("main-label");
	    debugLabel.getStyleClass().addAll("auxilary-label", "info-label");

	    
	    layout.getChildren().addAll(userLabel, logoutButton, changeRolesButton);
	    // Set the scene to primary stage
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("Staff Home Page");
    }
    
    public void addDebugLabel() {
    	layout.getChildren().add(debugLabel);
    }
}
