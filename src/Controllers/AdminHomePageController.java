package Controllers;

import Models.User;
import Models.User.Role;

import java.util.*;

import Database.*;
import Database.Services.Interfaces.IUserService;

import Exceptions.DBExceptions.DataAccessException;

import Sessions.UserSession;

import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import UI.Pages.AdminHomePage;

public class AdminHomePageController {
	private final IUserService userService;
	private final AdminHomePage view;
	private final Stage primaryStage;
	private final UserSession userSession;
	private String selectedDropdownUser = null;
	
	List<String> users;

	/**
	 * The AdminHomePageController class handles tasks 
	 * This is intended to be used by the first user to initialize the system with admin credentials.
	 */
	
	public AdminHomePageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, AdminHomePage view) {
		this.view = view;
		this.userService = dBConnectionStore.getUserService();
		this.primaryStage = primaryStage;
		this.userSession = userSession;
		
		setInitialLayout();
		
		setUpdateRolesButtonAction();
		setSetTempPasswordButtonAction();
		setOnUserDropdownAction();
		setUserDeleteButtonAction();
	}
	
	// ===================
    // 		 SETTERS 
  	// ===================
	
	private void setInitialLayout() {
		view.setInitialLayout(primaryStage);
		
		try {
			users = userService.getAllUsernames();
			view.setUserDropDownLayout(users);
		} catch (DataAccessException e) {
			// TODO: - Handle the case if user names can't be fetched
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
	}
	
	
	private void setUpdateRolesButtonAction() {
		view.getSaveRolesButton().setOnAction(_ -> {
			selectedDropdownUser = view.getUserDropdown().getValue();
			if (selectedDropdownUser == null) return;

			// Build new role list from checkboxes
			List<String> roles = new ArrayList<>();
			if (view.isAdminCheckBoxSelected()) roles.add("admin");
			if (view.isStudentCheckBoxSelected()) roles.add("student");
			if (view.isInstructorCheckBoxSelected()) roles.add("instructor");
			if (view.isStaffCheckBoxSelected()) roles.add("staff");
			if (view.isReviewerCheckBoxSelected()) roles.add("reviewer");

			String roleStr = String.join(",", roles);
			view.setUserDetailsLabel(
				"User: " + selectedDropdownUser + "\n" +
				"Roles: " + (roleStr.isEmpty() ? "None" : roleStr) + "\n" +
				"Other info: (e.g., last login, email, etc.)"
			);

			try {
				userService.updateUserRoles(selectedDropdownUser, roleStr);
				System.out.println("Updated Roles for " + selectedDropdownUser + ": " + roleStr);
				userService.printUsersTable();
				// Refresh dropdown and keep the same selection so UI reflects saved roles
				try {
					users = userService.getAllUsernames();
					view.setUserDropDownLayout(users);
					view.getUserDropdown().setValue(selectedDropdownUser);
				} catch (DataAccessException ignore) { /* non-fatal UI refresh failure */ }
			} catch (DataAccessException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		});
	}
	
	private void setSetTempPasswordButtonAction() {
		view.getSetTempPasswordButton().setOnAction(_ -> {
            String selectedUserName = view.getUserDropdown().getValue();
            if (selectedUserName == null) return;
            
            String tempPass = null;
            
            try {
            	tempPass = userService.updatePasswordForUser(selectedUserName);
            } catch (DataAccessException e) {
            	// TODO: - Handle the case if the user couldn't update their password
            	System.err.println(e.getMessage());
	            e.printStackTrace();
	        }

            if (tempPass == null) {
                view.setTempPasswordInfoLabel("Error setting temp password");
            } else {
                view.setTempPasswordInfoLabel("Temp password for " + selectedUserName + " is: " + tempPass);
            }
        });
	}
	
	private void setOnUserDropdownAction() {
		view.getUserDropdown().setOnAction(_ -> {
            String selectedUserName = view.getUserDropdown().getValue();
            if (selectedUserName == null) return;
            
            int userId;
            String roleStr;
            
            try {
				// Use the raw roles string so multiple roles are preserved and shown in UI
				roleStr = userService.getUserRolesString(selectedUserName);
				userId = userService.getUserIdByName(selectedUserName);
            } catch (DataAccessException e) {
            	// TODO: - Handle the case if user role can't be fetched
            	System.err.println(e.getMessage());
	            e.printStackTrace();
	            
	            return;
	        }

            // Reset all checkboxes
            view.setAdminCheckBox(false);
            view.setStudentCheckBox(false);
            view.setInstructorCheckBox(false);
            view.setStaffCheckBox(false);
            view.setReviewerCheckBox(false);

            // If roleStr can contain multiple roles separated by commas, handle that
            if (roleStr != null && !roleStr.trim().isEmpty()) {
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
                "User: " + selectedUserName + "\n" +
                "User ID: " + userId + "\n" +
                "Roles: " + (roleStr == null || roleStr.trim().isEmpty() ? "None" : roleStr) + "\n" +
                "Other info: (e.g., last login, email, etc.)"
            );
        });

	}
	
	private void setUserDeleteButtonAction() {
		view.getDeleteUserButton().setOnAction(_ -> {
            String selectedUserName = view.getUserDropdown().getValue();
            if (selectedUserName == null) return;

            // Prevent deleting own admin account
            if (selectedUserName.equals(userSession.getCurrentUserName())) {
                view.getWarningAlert().setTitle("Action Not Allowed");
                view.getWarningAlert().setHeaderText("You cannot delete your own admin account.");
                view.getWarningAlert().setContentText("Please have another admin remove this account if needed.");
                view.getWarningAlert().showAndWait();
                return;
            }

            // Confirm deletion
            
            view.getConfirmationAlert().setTitle("Delete User");
            view.getConfirmationAlert().setHeaderText("Are you sure you want to delete user: " + selectedUserName + "?");
            view.getConfirmationAlert().setContentText("This action cannot be undone.");

            Optional<ButtonType> result = view.getConfirmationAlert().showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
            	try {
	                boolean success = userService.deleteUser(userSession.getCurrentUserName(), selectedUserName);
	                if (success) {
	                    view.getUserDropdown().getItems().remove(selectedUserName);
	                    view.getUserDropdown().setValue(null);
	
	                    view.getInfoAlert().setTitle("Deleted");
	                    view.getInfoAlert().setHeaderText(null);
	                    view.getInfoAlert().setContentText("User " + selectedUserName + " deleted.");
	                    view.getInfoAlert().showAndWait();
	                } else {
	                    view.getErrorAlert().setTitle("Error");
	                    view.getErrorAlert().setHeaderText("Failed to delete user");
	                    view.getErrorAlert().setContentText("See logs for details.");
	                    view.getErrorAlert().showAndWait();
	                }
            	} catch (DataAccessException e) {
					// TODO: - Handle the case if user names can't be fetched
					System.err.println(e.getMessage());
				    e.printStackTrace();
			    }
            }
        });
	}
}
