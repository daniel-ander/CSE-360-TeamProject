package Controllers;

import UI.Pages.UserHomePage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user.
 */

public class UserHomePageController {
	
	private final Stage primaryStage;
	private UserHomePage view = new UserHomePage();
	
	public UserHomePageController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        setInitialLayout();
    }

    public void show(Stage primaryStage) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello, User!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    layout.getChildren().add(userLabel);
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Page");
    	
    }
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
}