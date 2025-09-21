package UI.Pages;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user.
 */

public class UserHomePage {
	
	VBox layout = new VBox();
	Label userLabel = new Label("Hello, User!");
	Scene userScene = new Scene(layout, 800, 400);
    
    public void setInitialLayout(Stage primaryStage) {
    	layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    layout.getChildren().add(userLabel);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Page");
    }
}