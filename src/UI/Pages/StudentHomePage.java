package UI.Pages;

import UI.Interfaces.IInitializable;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * This page displays a simple welcome message for the user.
 */

public class StudentHomePage implements IInitializable {
	private final VBox layout = new VBox();
	private final Scene scene = new Scene(layout, 800, 400);
	private final Label userLabel = new Label(); // "Hello, " + currentUser + "!"
	private final Button logoutButton = new Button("Logout");
	private final Button changeRolesButton = new Button("Change Roles");
	private final Label debugLabel = new Label("DEBUG MODE: No interactive elements on this page.");
	private final Button enterForumButton = new Button("Enter Forum");
	private final Button requestReviewerButton = new Button("Request to Become a Reviewer");
	
	public StudentHomePage() {
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

    public Button getEnterForumButton() {
        return enterForumButton;
    }

    public Button getRequestReviewerButton() {
        return requestReviewerButton;  // request reviewer button
    }
    
    // ===================
    // 		 SETTERS 
 	// ===================
    
    public void setUserLabel(String text) {
    	userLabel.setText("Hello, " + text + "!");
    }
    
    public void setInitialLayout(Stage primaryStage) {
    	layout.getStyleClass().add("layout");
    	// Label to display Hello user
	    userLabel.getStyleClass().add("main-label");
	   
	    // Add all elements to layout

	    layout.getChildren().addAll(userLabel, logoutButton, changeRolesButton, enterForumButton, requestReviewerButton);
	    
	    // Set the scene to primary stage
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("Student Home Page");

    }
    
    //request reviewer button error and success messages
    public void addDebugLabel() {
    	debugLabel.getStyleClass().addAll("auxilary-label", "info-label");
    	layout.getChildren().add(debugLabel);
    }

    public void showSuccessMessage(String message) {
    // Show success message (simple alert or dialog)
    showAlert("Success", message);
}

    public void showErrorMessage(String message) {
    // Show error message (simple alert or dialog)
    showAlert("Error", message);
}

    private void showAlert(String title, String message) {
    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
    }
}