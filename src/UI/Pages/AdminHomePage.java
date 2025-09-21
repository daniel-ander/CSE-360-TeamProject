package UI.Pages;

import java.util.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;


/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {
	
	private final DatabaseHelper databaseHelper;
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
	public AdminHomePage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}
	
    public void show(Stage primaryStage) {
    	VBox layout = new VBox();
    	HBox roleBox = new HBox(20);
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    roleBox.setStyle(null);

	  //Checkboxes for role selection
	    CheckBox adminCheck = new CheckBox("Admin");
	    CheckBox studentCheck = new CheckBox("Student");
	    CheckBox instructorCheck = new CheckBox("Instructor");
	    CheckBox staffCheck = new CheckBox("Staff");
	    CheckBox reviewerCheck = new CheckBox("Reviewer");
	    
	    // label to display the welcome message for the admin
	    Label adminLabel = new Label("Hello, Admin!\n Select a User, Select their roles, then save.");
	    
	    adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    // Create drop down w/ all users names
	    ComboBox<String> userDropDown = new ComboBox<>();
	    List<String> users = databaseHelper.getAllUsernames();
	    userDropDown.setPromptText("Select user");
	    userDropDown.getItems().addAll(users);
	    
	    // Save button
	    Button saveRoles = new Button("Save");
	    saveRoles.setOnAction(a -> {
	    	String selectedUser = userDropDown.getValue();
	    	if (selectedUser == null) {
	    		return;
	    	}
	    	List<String> roles = new ArrayList<>();
	    	if (adminCheck.isSelected()) roles.add("admin");
	        if (studentCheck.isSelected()) roles.add("student");
	        if (instructorCheck.isSelected()) roles.add("instructor");
	        if (staffCheck.isSelected()) roles.add("staff");
	        if (reviewerCheck.isSelected()) roles.add("reviewer");
	        
	        databaseHelper.updateUserRoles(selectedUser, roles);
	        System.out.println("Updates Roles for " + selectedUser + ": " + roles);
	        
	    });
	    
	    
	    
	    roleBox.getChildren().addAll(userDropDown, new Label("Choose Role to add"), adminCheck, studentCheck, instructorCheck, staffCheck, reviewerCheck, saveRoles);
	    
	    layout.getChildren().addAll(adminLabel, roleBox);
	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
    
    
}