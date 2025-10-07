package UI.Pages;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import UI.Interfaces.IInitializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class FirstPage implements IInitializable {
	VBox layout = new VBox(5);
	Label userLabel = new Label("Welcome!\n This is the special Admin page\n Only the first user sees this!");
	Button continueButton = new Button("Continue");
	Scene scene = new Scene(layout, 800, 400);
	
	public FirstPage() {
    	// import stylesheet
    	scene.getStylesheets().add(
    		getClass().getResource("/Styles/styles.css").toExternalForm()
    	);
    }
    
	// ===================
    // 		 GETTERS 
 	// ===================
    
    public Button getContinueButton() {
    	return continueButton;
    }
    
    // ===================
    // 		 SETTERS 
 	// ===================
    
    public void setInitialLayout(Stage primaryStage) {
    	layout.getStyleClass().add("layout");
    	userLabel.getStyleClass().add("main-label");
    	
    	layout.getChildren().addAll(userLabel, continueButton);

	    // Set the scene to primary stage
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("First Page");
    	primaryStage.show();
	}
}
