package Controllers;

import java.util.*;

import Sessions.UserSession;
// -------------------- Should get this out of the controller
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import UI.Pages.AdminHomePage;
import databasePart1.*;

public class AdminHomePageController {
	private final IUserRepository userRepository;
	private final AdminHomePage view;
	private final Stage primaryStage;
	private final UserSession userSession;
	private String selectedDropdownUser = null;
	
	List<String> users;

	/**
	 * Displays the admin page in the provided primary stage.
	 * 
	 * @param primaryStage The primary stage where the scene will be displayed.
	 */
	public AdminHomePageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, AdminHomePage view) {
		this.view = view;
		this.userRepository = dBConnectionStore.getUserRepository();
		this.primaryStage = primaryStage;
		this.users = userRepository.getAllUsernames();
		this.userSession = userSession;
		
		setInitialLayout();
		
		setUpdateRolesButtonAction();
		setSetTempPasswordButtonAction();
		setOnUserDropdownAction();
		setUserDeleteButtonAction();
		setSaveRolesButtonAction();
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
			selectedDropdownUser = view.getUserDropdown().getValue();
			if (selectedDropdownUser == null) {
				return;
			}
			List<String> roles = new ArrayList<>();
			if (view.isAdminCheckBoxSelected())
				roles.add("admin");
			if (view.isStudentCheckBoxSelected())
				roles.add("student");
			if (view.isInstructorCheckBoxSelected())
				roles.add("instructor");
			if (view.isStaffCheckBoxSelected())
				roles.add("staff");
			if (view.isReviewerCheckBoxSelected())
				roles.add("reviewer");

			userRepository.updateUserRoles(selectedDropdownUser, roles);
			System.out.println("Updates Roles for " + selectedDropdownUser + ": " + roles);
		});
	}
	
	private void setSetTempPasswordButtonAction() {
		view.getSetTempPasswordButton().setOnAction(a -> {
            String selectedUser = view.getUserDropdown().getValue();
            if (selectedUser == null) return;

            String tempPass = userRepository.issueResetPassword(selectedUser);

            if (tempPass == null) {
                view.setTempPasswordInfoLabel("Error setting temp password");
            } else {
                view.setTempPasswordInfoLabel("Temp password for " + selectedUser + " is: " + tempPass);
            }
        });
	}
	
	private void setOnUserDropdownAction() {
		view.getUserDropdown().setOnAction(a -> {
            String selectedUser = view.getUserDropdown().getValue();
            if (selectedUser == null) return;

            String roleStr = userRepository.getUserRole(selectedUser);

            // Reset all checkboxes
            view.setAdminCheckBox(false);
            view.setStudentCheckBox(false);
            view.setInstructorCheckBox(false);
            view.setStaffCheckBox(false);
            view.setReviewerCheckBox(false);

            if (roleStr != null && !roleStr.isEmpty()) {
                Set<String> roles = new HashSet<>();
                for (String r : roleStr.split(",")) {
                    roles.add(r.trim().toLowerCase(Locale.ROOT));
                }
                if (roles.contains("admin")) view.setAdminCheckBox(true);
                if (roles.contains("student")) view.setStudentCheckBox(true);
                if (roles.contains("instructor")) view.setInstructorCheckBox(true);
                if (roles.contains("staff")) view.setStaffCheckBox(true);
                if (roles.contains("reviewer")) view.setReviewerCheckBox(true);
            }

            // Update details section
            view.setUserDetailsLabel(
                "User: " + selectedUser + "\n" +
                "Roles: " + (roleStr == null || roleStr.isEmpty() ? "None" : roleStr) + "\n" +
                "Other info: (e.g., last login, email, etc.)"
            );
        });

	}
	
	private void setUserDeleteButtonAction() {
		view.getDeleteUserButton().setOnAction(a -> {
            String selectedUser = view.getUserDropdown().getValue();
            if (selectedUser == null) return;

            // Prevent deleting own admin account
            if (selectedUser.equals(userSession.getCurrentUserName())) {
                view.getWarningAlert().setTitle("Action Not Allowed");
                view.getWarningAlert().setHeaderText("You cannot delete your own admin account.");
                view.getWarningAlert().setContentText("Please have another admin remove this account if needed.");
                view.getWarningAlert().showAndWait();
                return;
            }

            // Confirm deletion
            
            view.getConfirmationAlert().setTitle("Delete User");
            view.getConfirmationAlert().setHeaderText("Are you sure you want to delete user: " + selectedUser + "?");
            view.getConfirmationAlert().setContentText("This action cannot be undone.");

            Optional<ButtonType> result = view.getConfirmationAlert().showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean success = userRepository.deleteUser(userSession.getCurrentUserName(), selectedUser);
                if (success) {
                    view.getUserDropdown().getItems().remove(selectedUser);
                    view.getUserDropdown().setValue(null);

                    view.getInfoAlert().setTitle("Deleted");
                    view.getInfoAlert().setHeaderText(null);
                    view.getInfoAlert().setContentText("User " + selectedUser + " deleted.");
                    view.getInfoAlert().showAndWait();
                } else {
                    view.getErrorAlert().setTitle("Error");
                    view.getErrorAlert().setHeaderText("Failed to delete user");
                    view.getErrorAlert().setContentText("See logs for details.");
                    view.getErrorAlert().showAndWait();
                }
            }
        });
	}
	
	private void setSaveRolesButtonAction() {

        String selectedUser = view.getUserDropdown().getValue();
        if (selectedUser == null) return;

        // Build new role list from checkboxes
        List<String> roles = new ArrayList<>();
        if (view.isAdminCheckBoxSelected()) roles.add("admin");
        if (view.isStudentCheckBoxSelected()) roles.add("student");
        if (view.isInstructorCheckBoxSelected()) roles.add("instructor");
        if (view.isStaffCheckBoxSelected()) roles.add("staff");
        if (view.isReviewerCheckBoxSelected()) roles.add("reviewer");

        // Update details view
        String roleStr = String.join(",", roles);
        view.setUserDetailsLabel(
            "User: " + selectedUser + "\n" +
            "Roles: " + (roleStr.isEmpty() ? "None" : roleStr) + "\n" +
            "Other info: (e.g., last login, email, etc.)"
        );

        // Save roles in database
        userRepository.updateUserRoles(selectedUser, roles);
        System.out.println("Updated Roles for " + selectedUser + ": " + roles);
        userRepository.printUsersTable();
    
	}
}