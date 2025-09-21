package application;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
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
	
	private final DatabaseHelper databaseHelper;
    private final String currentAdmin;
    
    public InvitationPage(DatabaseHelper databaseHelper, String currentAdmin) {
		this.databaseHelper = databaseHelper;
		this.currentAdmin = currentAdmin;
	}
    
    public void show(DatabaseHelper databaseHelper,Stage primaryStage) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display the title of the page
	    Label userLabel = new Label("Invite ");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    //Box to select roles for the invite code
	    HBox roleBox = new HBox(20);
	    roleBox.setStyle("-fx-alignment: center;");
	    CheckBox adminCheck = new CheckBox("Admin");
        CheckBox studentCheck = new CheckBox("Student");
        CheckBox instructorCheck = new CheckBox("Instructor");
        CheckBox staffCheck = new CheckBox("Staff");
        CheckBox reviewerCheck = new CheckBox("Reviewer");
		roleBox.getChildren().addAll(adminCheck, studentCheck, instructorCheck, staffCheck, reviewerCheck);
		
		
        
	    
	    
	    // Button to generate the invitation code
	    Button showCodeButton = new Button("Generate Invitation Code");
	    
	    // Label to display the generated invitation code
	    Label inviteCodeLabel = new Label(""); ;
        inviteCodeLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
        
        showCodeButton.setOnAction(a -> {
        	// Generate the invitation code using the databaseHelper and set it to the label
        	// Gather selected roles
        	// Format: 5-bit binary string (Admin, Student, Instructor, Staff, Reviewer)
        	StringBuilder selectedRoles = new StringBuilder();
        	if (adminCheck.isSelected()) selectedRoles.append("1"); else selectedRoles.append("0");
        	if (studentCheck.isSelected()) selectedRoles.append("1"); else selectedRoles.append("0");
        	if (instructorCheck.isSelected()) selectedRoles.append("1"); else selectedRoles.append("0");
        	if (staffCheck.isSelected()) selectedRoles.append("1"); else selectedRoles.append("0");
        	if (reviewerCheck.isSelected()) selectedRoles.append("1"); else selectedRoles.append("0");
        	
        	
            String invitationCode = databaseHelper.generateInvitationCode(selectedRoles.toString());
            inviteCodeLabel.setText(invitationCode);
        });
	    

        layout.getChildren().addAll(userLabel, showCodeButton, inviteCodeLabel, roleBox);
	    Scene inviteScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(inviteScene);
	    primaryStage.setTitle("Invite Page");
    	
    }
}