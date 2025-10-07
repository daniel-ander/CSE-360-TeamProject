package UI.Pages;


import UI.Interfaces.IPage;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user.
 */

public class ReviewerHomePage implements IPage {
	private final VBox layout = new VBox();
	private final Scene scene = new Scene(layout, 800, 400);
	private final Label userLabel = new Label();
	private final Label debugLabel = new Label("DEBUG MODE: No interactive elements on this page.");
	private final Button logoutButton = new Button("Logout");
	private final Button changeRolesButton = new Button("Change Roles");
	
	public ReviewerHomePage() {
    	// import stylesheet
    	scene.getStylesheets().add(
    		getClass().getResource("/Styles/styles.css").toExternalForm()
    	);
    }
	
	// ===================
    // 		 GETTERS 
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
    // 		 SETTERS 
 	// ===================
    
    public void setUserLabel(String text) {
    	userLabel.setText("Hello, " + text + "!");
    }
    
    public void setInitialLayout(Stage primaryStage) {
    	layout.getStyleClass().add("layout");
    	userLabel.getStyleClass().add("main-label");

	    layout.getChildren().addAll(userLabel, logoutButton, changeRolesButton);
	  
	    // Set the scene to primary stage
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("Reviewer Home Page");
    }
    
    public void addDebugLabel() {
    	debugLabel.getStyleClass().addAll("auxilary-label", "info-label");	
    	layout.getChildren().add(debugLabel);
    }
}