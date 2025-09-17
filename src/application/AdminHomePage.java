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
	    userDropDown.setOnAction(a -> {
	        String selectedUser = userDropDown.getValue();
	        if (selectedUser == null) return;

	        String roleStr = databaseHelper.getUserRole(selectedUser);

	        // Reset all first
	        adminCheck.setSelected(false);
	        studentCheck.setSelected(false);
	        instructorCheck.setSelected(false);
	        staffCheck.setSelected(false);
	        reviewerCheck.setSelected(false);

	        if (roleStr != null && !roleStr.isEmpty()) {
	            Set<String> roles = new HashSet<>();
	            for (String r : roleStr.split(",")) {
	                roles.add(r.trim().toLowerCase(Locale.ROOT));
	            }
	            if (roles.contains("admin")) adminCheck.setSelected(true);
	            if (roles.contains("student")) studentCheck.setSelected(true);
	            if (roles.contains("instructor")) instructorCheck.setSelected(true);
	            if (roles.contains("staff")) staffCheck.setSelected(true);
	            if (roles.contains("reviewer")) reviewerCheck.setSelected(true);
	        }
	    });
	    
	    // Box for displaying details
	 // Label to show details
	    Label userDetails = new Label("Select a user to view details.");
	    userDetails.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-font-size: 14px;");

	    // When user is selected, update details
	    userDropDown.setOnAction(a -> {
	        String selectedUser = userDropDown.getValue();
	        if (selectedUser == null) return;

	        String roleStr = databaseHelper.getUserRole(selectedUser);

	        // Reset checkboxes
	        adminCheck.setSelected(false);
	        studentCheck.setSelected(false);
	        instructorCheck.setSelected(false);
	        staffCheck.setSelected(false);
	        reviewerCheck.setSelected(false);

	        if (roleStr != null && !roleStr.isEmpty()) {
	            Set<String> roles = new HashSet<>();
	            for (String r : roleStr.split(",")) {
	                roles.add(r.trim().toLowerCase(Locale.ROOT));
	            }
	            if (roles.contains("admin")) adminCheck.setSelected(true);
	            if (roles.contains("student")) studentCheck.setSelected(true);
	            if (roles.contains("instructor")) instructorCheck.setSelected(true);
	            if (roles.contains("staff")) staffCheck.setSelected(true);
	            if (roles.contains("reviewer")) reviewerCheck.setSelected(true);
	        }

	        // Update details section
	        userDetails.setText(
	            "User: " + selectedUser + "\n" +
	            "Roles: " + (roleStr == null || roleStr.isEmpty() ? "None" : roleStr) + "\n" +
	            "Other info: (e.g., last login, email, etc.)"
	        );
	    });
	    
	    // Delete user button
	    Button deleteUser = new Button("Delete User");
	    deleteUser.setOnAction(a -> {
	        String selectedUser = userDropDown.getValue();
	        if (selectedUser == null) return;

	        // Confirm deletion
	        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
	        confirm.setTitle("Delete User");
	        confirm.setHeaderText("Are you sure you want to delete user: " + selectedUser + "?");
	        confirm.setContentText("This action cannot be undone.");

	        Optional<ButtonType> result = confirm.showAndWait();
	        if (result.isPresent() && result.get() == ButtonType.OK) {
	            try {
	                databaseHelper.deleteUser(selectedUser);
	                userDropDown.getItems().remove(selectedUser);
	                userDropDown.setValue(null);

	                Alert info = new Alert(Alert.AlertType.INFORMATION);
	                info.setTitle("Deleted");
	                info.setHeaderText(null);
	                info.setContentText("User " + selectedUser + " deleted.");
	                info.showAndWait();
	            } catch (Exception e) {
	                Alert error = new Alert(Alert.AlertType.ERROR);
	                error.setTitle("Error");
	                error.setHeaderText("Failed to delete user");
	                error.setContentText(e.getMessage());
	                error.showAndWait();
	            }
	        }
	    });
	    
	    
	    // Save button
	    Button saveRoles = new Button("Save");
	    saveRoles.setOnAction(a -> {
	        String selectedUser = userDropDown.getValue();
	        if (selectedUser == null) return;

	        // Build fresh from the current checkbox state (no DB comparison)
	        List<String> roles = new ArrayList<>();
	        if (adminCheck.isSelected()) roles.add("admin");
	        if (studentCheck.isSelected()) roles.add("student");
	        if (instructorCheck.isSelected()) roles.add("instructor");
	        if (staffCheck.isSelected()) roles.add("staff");
	        if (reviewerCheck.isSelected()) roles.add("reviewer");
	        // refresh the details section - could be optimized out if needed
	        String roleStr = String.join(",", roles);
	        userDetails.setText(
	            "User: " + selectedUser + "\n" +
	            "Roles: " + (roleStr.isEmpty() ? "None" : roleStr) + "\n" +
	            "Other info: (e.g., last login, email, etc.)"
	        );

	        databaseHelper.updateUserRoles(selectedUser, roles);
	        System.out.println("Updated Roles for " + selectedUser + ": " + roles);
	        databaseHelper.printUsersTable();
	    });
	    
	    roleBox.getChildren().addAll(userDropDown, new Label("Choose Role to add"), adminCheck, studentCheck, instructorCheck, staffCheck, reviewerCheck, saveRoles);
	    
	    layout.getChildren().addAll(adminLabel, roleBox, userDetails, deleteUser);
	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
    
    
}