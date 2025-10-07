package UI.Pages;

import java.util.*;

import UI.Interfaces.IInitializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * AdminPage class represents the user interface for the admin user. This page
 * displays a simple welcome message for the admin.
 */

public class AdminHomePage implements IInitializable {
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
	private final Scene scene = new Scene(layout, 800, 400);
	private final HBox roleBox = new HBox(20);
	
	// label to display the welcome message for the admin
	private final Label adminLabel = new Label("Hello, Admin!\n Select a User, Select their roles, then save.");
	private final Label userDetailsLabel = new Label("Select a user to view details.");
	private final Label tempPasswordInfoLabel = new Label();
	
	private final Button saveRolesButton = new Button("Save");
	private final Button setTempPasswordButton = new Button("Set Temp Password");
	private final Button deleteUserButton = new Button("Delete User");
	
	private final Alert warningAlert = new Alert(Alert.AlertType.WARNING);
	private final Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
	private final Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
	private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);
	
	private final ComboBox<String> userDropDown = new ComboBox<>();
	
	public AdminHomePage() {
    	// import stylesheet
    	scene.getStylesheets().add(
    		getClass().getResource("/Styles/styles.css").toExternalForm()
    	);
    }
	
	// ===================
    // 		 GETTERS 
 	// ===================
	
	public ComboBox<String> getUserDropdown() {
		return userDropDown;
	}
	
	public Alert getErrorAlert() {
		return errorAlert;
	}
	
	public Alert getInfoAlert() {
		return infoAlert;
	}
	
	public Alert getWarningAlert() {
		return warningAlert;
	}
	
	public Alert getConfirmationAlert() {
		return confirmAlert;
	}
	
	public Button getDeleteUserButton() {
		return deleteUserButton;
	}
	
	public Button getSetTempPasswordButton() {
		return setTempPasswordButton;
	}
	
	public Button getSaveRolesButton() {
		return saveRolesButton;
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
    // 		 SETTERS 
 	// ===================
	
	public void setTempPasswordInfoLabel(String text) {
		tempPasswordInfoLabel.setText(text);
	}
	
	public void setAdminCheckBox(boolean flag) {
		adminCheckBox.setSelected(flag);
	}
	
	public void setStudentCheckBox(boolean flag) {
		studentCheckBox.setSelected(flag);
	}
	
	public void setInstructorCheckBox(boolean flag) {
		instructorCheckBox.setSelected(flag);
	}
	
	public void setStaffCheckBox(boolean flag) {
		staffCheckBox.setSelected(flag);
	}
	
	public void setReviewerCheckBox(boolean flag) {
		reviewerCheckBox.setSelected(flag);
	}
	
	public void setUserDetailsLabel(String text) {
		userDetailsLabel.setText(text);
	}
	
	public void setInitialLayout(Stage primaryStage) {	
		layout.getStyleClass().add("layout");
		adminLabel.getStyleClass().add("main-label");
		userDetailsLabel.getStyleClass().add("minor-label");
        tempPasswordInfoLabel.getStyleClass().addAll("auxilary-label", "info-label");
        
        userDropDown.setPromptText("Select user");
        
		roleBox.setStyle(null);
        adminCheckBox.setDisable(true); // Admins can't be un-admined by UI, so disable interaction
		
		setUserRoleDropdownLayout();
		
		layout.getChildren().addAll(adminLabel, roleBox, userDetailsLabel, deleteUserButton, setTempPasswordButton, tempPasswordInfoLabel);
		
		// Set the scene to primary stage
		primaryStage.setScene(scene);
		primaryStage.setTitle("Admin Page");
	}
	
	public void setUserDropDownLayout(List<String> users) {
		// Create drop down w/ all users names
		userDropDown.getItems().addAll(users);
	}
	
	private void setUserRoleDropdownLayout() {
		roleBox.getChildren().addAll(
	            userDropDown,
	            new Label("Choose Role to add"),
	            adminCheckBox, studentCheckBox, instructorCheckBox, staffCheckBox, reviewerCheckBox,
	            saveRolesButton
	    );
	}

 }