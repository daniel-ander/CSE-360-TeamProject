package UI.Pages;


import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Controllers.UserLoginPageController;
import Controllers.WelcomeLoginPageController;
import Models.User;
import databasePart1.*;

/**
 * This page displays a simple welcome message for the user.
 */

public class InstructorHomePage {
	
	private boolean DEBUG = true; //toggle for debugging
	private final VBox layout = new VBox();

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
    
    public void setInitialLayout(Stage primaryStage) {
    	layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
    	userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    	
    	layout.getChildren().addAll(userLabel, logoutButton, changeRolesButton);
	    Scene userScene = new Scene(layout, 800, 400);
	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Instructor Home Page");
    	
    }
}