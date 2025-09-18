package application;


import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.*;

/**
 * This page displays a simple welcome message for the user.
 */

public class StudentHomePage {

	private final DatabaseHelper databaseHelper;
	private final String currentUser; // logged-in username
	
	public StudentHomePage(DatabaseHelper databaseHelper, String currentUser) {
		this.databaseHelper = databaseHelper;
		this.currentUser = currentUser;
	}
	
	
    public void show(Stage primaryStage) {
    	
    	
    	
    	final boolean DEBUG = true; //toggle for debugging
    	
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello, " + currentUser + "!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    
	    Button logoutButton = new Button("Logout");
	    logoutButton.setOnAction(a -> {
	        // Return to login page
	        UserLoginPage loginPage = new UserLoginPage(databaseHelper);
	        loginPage.show(primaryStage);
	    });
	    Button changeRolesButton = new Button("Change Roles");
	    changeRolesButton.setOnAction(a -> {
	    	// Return to welcome page
	    	String role = databaseHelper.getUserRole(currentUser);
	    	User user = new User(currentUser, "", role); // Password can be blank if not needed
	    	new WelcomeLoginPage(databaseHelper).show(primaryStage, user);

	    });
	    
	    // Debugging Area	    
	    if (DEBUG) {
	        Label debugLabel = new Label("DEBUG MODE: No interactive elements on this page.");
	        debugLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 12px;");
	        layout.getChildren().add(debugLabel);
	    }
	    // Add all elements to layout

	    layout.getChildren().addAll(userLabel, logoutButton, changeRolesButton);
	    Scene userScene = new Scene(layout, 800, 400);
	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Student Home Page");
    	
    }
}