package Controllers;

import java.util.*;
import javafx.stage.Stage;

import UI.Pages.AdminHomePage;
import databasePart1.*;

public class AdminHomePageController {
	private final DatabaseHelper databaseHelper;
	private final AdminHomePage view = new AdminHomePage();
	private final Stage primaryStage;
	
	List<String> users;

	/**
	 * Displays the admin page in the provided primary stage.
	 * 
	 * @param primaryStage The primary stage where the scene will be displayed.
	 */
	public AdminHomePageController(DatabaseHelper databaseHelper, Stage primaryStage) {
		this.databaseHelper = databaseHelper;
		this.primaryStage = primaryStage;
		this.users = databaseHelper.getAllUsernames();
		
		setInitialLayout();
		
		setUpdateRolesButtonAction();
	}
	
	// ===================
	// Setters
	// ===================
	
	private void setInitialLayout() {
		view.setInitialLayout(primaryStage);
		view.setUserDropDownLayout(users);
	}
	
	
	private void setUpdateRolesButtonAction() {
		view.getSaveRolesButton().setOnAction(a -> {
			String selectedUser = view.getUserDropdown().getValue();
			if (selectedUser == null) {
				return;
			}
			List<String> roles = new ArrayList<>();
			if (view.getAdminCheckBox().isSelected())
				roles.add("admin");
			if (view.getStudentCheckBox().isSelected())
				roles.add("student");
			if (view.getInstructorCheckBox().isSelected())
				roles.add("instructor");
			if (view.getStaffCheckBox().isSelected())
				roles.add("staff");
			if (view.getReviewerCheckBox().isSelected())
				roles.add("reviewer");

			databaseHelper.updateUserRoles(selectedUser, roles);
			System.out.println("Updates Roles for " + selectedUser + ": " + roles);
		});
	}
}