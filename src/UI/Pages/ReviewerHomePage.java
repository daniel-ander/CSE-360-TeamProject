package UI.Pages;


import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user.
 */

public class ReviewerHomePage {
	private boolean DEBUG = true; //toggle for debugging
	private final VBox layout = new VBox();
	private final Label userLabel = new Label();
	private final Label debugLabel = new Label("DEBUG MODE: No interactive elements on this page.");
	private final Button logoutButton = new Button("Logout");
	private final Button changeRolesButton = new Button("Change Roles");
	
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
    	userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    	debugLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 12px;");    	


	    layout.getChildren().addAll(userLabel, logoutButton, changeRolesButton);
	    Scene userScene = new Scene(layout, 800, 400);
	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Reviewer Home Page");

    }
}