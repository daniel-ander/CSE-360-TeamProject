package UI.Pages;

import java.util.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * AdminPage class represents the user interface for the admin user. This page
 * displays a simple welcome message for the admin.
 */

public class AdminHomePage {
	/**
	 * Displays the admin page in the provided primary stage.
	 * 
	 * @param primaryStage The primary stage where the scene will be displayed.
	 */

	// Checkboxes for role selection
	private final CheckBox adminCheckBox = new CheckBox("Admin");
	private final CheckBox studentCheckBox = new CheckBox("Student");
	private final CheckBox instructorCheckBox = new CheckBox("Instructor");
	private final CheckBox staffCheckBox = new CheckBox("Staff");
	private final CheckBox reviewerCheckBox = new CheckBox("Reviewer");

	private final VBox layout = new VBox();
	private final Scene adminScene = new Scene(layout, 800, 400);
	private final HBox roleBox = new HBox(20);
	
	// label to display the welcome message for the admin
	private final Label adminLabel = new Label("Hello, Admin!\n Select a User, Select their roles, then save.");
	private final Button saveRolesButton = new Button("Save");
	
	private final ComboBox<String> userDropDown = new ComboBox<>();
	
	// ===================
	// Getters
	// ===================
	
	public Button getSaveRolesButton() {
		return saveRolesButton;
	}
	
	public ComboBox<String> getUserDropdown() {
		return userDropDown;
	}
	
	public CheckBox getAdminCheckBox() {
		return adminCheckBox;
	}
	
	public CheckBox getStudentCheckBox() {
		return studentCheckBox;
	}
	
	public CheckBox getInstructorCheckBox() {
		return instructorCheckBox;
	}
	
	public CheckBox getStaffCheckBox() {
		return staffCheckBox;
	}
	
	public CheckBox getReviewerCheckBox() {
		return reviewerCheckBox;
	}
	
	// ===================
	// Setters
	// ===================
	
	public void setInitialLayout(Stage primaryStage) {
		// Set the scene to primary stage
		primaryStage.setScene(adminScene);
		primaryStage.setTitle("Admin Page");
				
		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
		roleBox.setStyle(null);
		adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		
		addUserRoleDropdown();
	}
	
	public void setUserDropDownLayout(List<String> users) {
		// Create drop down w/ all users names
		userDropDown.setPromptText("Select user");
		userDropDown.getItems().addAll(users);
	}
	
	private void addUserRoleDropdown() {
		roleBox.getChildren().addAll(userDropDown, new Label("Choose Role to add"), adminCheckBox, studentCheckBox,
				instructorCheckBox, staffCheckBox, reviewerCheckBox, saveRolesButton);

		layout.getChildren().addAll(adminLabel, roleBox);
	}

 }
