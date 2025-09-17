package application;

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
	    
	    // admins cant unadmin so disable the checkbox functionality, still shows role
	    adminCheck.setDisable(true);
	    	
	    // label to display the welcome message for the admin
	    Label adminLabel = new Label("Hello, Admin!\n Select a User, Select their roles, then save.");
	    
	    adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    // Create drop down w/ all users names
	    ComboBox<String> userDropDown = new ComboBox<>();
	    List<String> users = databaseHelper.getAllUsernames();
	    userDropDown.setPromptText("Select user");
	    userDropDown.getItems().addAll(users);
	    
	    // Dropdown onAction
	    userDropDown.setOnAction(a ->{
	    	String selectedUser = userDropDown.getValue();
	    	if (selectedUser == null) return;
	    	
	    	String roleStr = databaseHelper.getUserRole(selectedUser);
	    	List <String> roles = new ArrayList<>();
	    	if (roleStr != null && !roleStr.isEmpty()) {
	    		roles = Arrays.asList(roleStr.split(","));
	    	}
	    	// Reset them first, 
	        adminCheck.setSelected(false);
	        studentCheck.setSelected(false);
	        instructorCheck.setSelected(false);
	        staffCheck.setSelected(false);
	        reviewerCheck.setSelected(false);

	        // then enable the ones the user has
	        if (roles.contains("admin")) adminCheck.setSelected(true);
	        if (roles.contains("student")) studentCheck.setSelected(true);
	        if (roles.contains("instructor")) instructorCheck.setSelected(true);
	        if (roles.contains("staff")) staffCheck.setSelected(true);
	        if (roles.contains("reviewer")) reviewerCheck.setSelected(true);
	    });
	    
	    // Save button
	    Button saveRoles = new Button("Save");
	    saveRoles.setOnAction(a -> {
	    	String selectedUser = userDropDown.getValue();
	    	if (selectedUser == null) {
	    		return;
	    	}
	    	List<String> roles = new ArrayList<>();
	    	String currentRolesStr = databaseHelper.getUserRole(selectedUser);
	    	List<String> currentRoles = Arrays.asList(currentRolesStr.split(","));
	    	if (adminCheck.isSelected()) {
	    	    if (currentRoles.contains("admin")) {
	    	        System.out.printf("Admin role already assigned to %s%n", selectedUser);
	    	        System.out.printf("Role string: %s%n", currentRoles);
	    	    } else {
	    	        roles.add("admin");
	    	    }
	    	}

	    	if (studentCheck.isSelected()) {
	    	    if (currentRoles.contains("student")) {
	    	        System.out.printf("Student role already assigned to %s%n", selectedUser);
	    	        System.out.printf("Role string: %s%n", currentRoles);
	    	    } else {
	    	        roles.add("student");
	    	    }
	    	}

	    	if (instructorCheck.isSelected()) {
	    	    if (currentRoles.contains("instructor")) {
	    	        System.out.printf("Instructor role already assigned to %s%n", selectedUser);
	    	        System.out.printf("Role string: %s%n", currentRoles);
	    	    } else {
	    	        roles.add("instructor");
	    	    }
	    	}

	    	if (staffCheck.isSelected()) {
	    	    if (currentRoles.contains("staff")) {
	    	        System.out.printf("Staff role already assigned to %s%n", selectedUser);
	    	        System.out.printf("Role string: %s%n", currentRoles);
	    	    } else {
	    	        roles.add("staff");
	    	    }
	    	}

	    	if (reviewerCheck.isSelected()) {
	    	    if (currentRoles.contains("reviewer")) {
	    	        System.out.printf("Reviewer role already assigned to %s%n", selectedUser);
	    	        System.out.printf("Role string: %s%n", currentRoles);
	    	    } else {
	    	        roles.add("reviewer");
	    	    }
	    	}

	        
	        databaseHelper.updateUserRoles(selectedUser, roles);	
	        System.out.println("Updates Roles for " + selectedUser + ": " + roles);
	        databaseHelper.printUsersTable();
	        
	    });
	   
	    
	    roleBox.getChildren().addAll(userDropDown, new Label("Choose Role to add"), adminCheck, studentCheck, instructorCheck, staffCheck, reviewerCheck, saveRoles);
	    
	    layout.getChildren().addAll(adminLabel, roleBox);
	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
    
    
}