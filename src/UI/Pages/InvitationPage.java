package UI.Pages;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */

public class InvitationPage {

	/**
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
	
	private final VBox layout = new VBox();
	private final Label userLabel = new Label("Invite ");						  // Label to display the title of the page
	private final Button showCodeButton = new Button("Generate Invitation Code"); // Button to generate the invitation code
	private final Label inviteCodeLabel = new Label("");						  // Label to display the generated invitation code
	private final Scene inviteScene = new Scene(layout, 800, 400);
	
	private final HBox roleBox = new HBox(20);
    private final CheckBox adminCheckBox = new CheckBox("Admin");
    private final CheckBox studentCheckBox = new CheckBox("Student");
    private final CheckBox instructorCheckBox = new CheckBox("Instructor");
    private final CheckBox staffCheckBox = new CheckBox("Staff");
    private final CheckBox reviewerCheckBox = new CheckBox("Reviewer");
    
    // ===================
   	// Getters
   	// ===================
    
    public Button getShowCodeButton() {
    	return showCodeButton;
    }
    
    public Label getInviteCodeLabel() {
    	return inviteCodeLabel;
    }
    
    public boolean isAdminCheckBoxSelected() {
    	return adminCheckBox.isSelected();
    }
    
    public boolean isStudentCheckBoxSelected() {
    	return studentCheckBox.isSelected();
    }
    
    public boolean isInstructorCheckBoxSelected() {
    	return instructorCheckBox.isSelected();
    }
    
    public boolean isStaffCheckBoxSelected() {
    	return staffCheckBox.isSelected();
    }
    
    public boolean isReviewerCheckBoxSelected() {
    	return reviewerCheckBox.isSelected();
    }
    
    // ===================
   	// Setters
   	// ===================
    
    
    public void setInitialLayout(Stage primaryStage) {
    	
    	layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
    	userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    	inviteCodeLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
    	
    	roleBox.getChildren().addAll(adminCheckBox, studentCheckBox, instructorCheckBox, staffCheckBox, reviewerCheckBox);
    	roleBox.setStyle("-fx-alignment: center;");
    	
    	layout.getChildren().addAll(userLabel, showCodeButton, inviteCodeLabel, roleBox);
    	
    	// Set the scene to primary stage
	    primaryStage.setScene(inviteScene);
	    primaryStage.setTitle("Invite Page");
    }
}