package Controllers;

import java.util.*;

import Database.*;
import Database.Services.Interfaces.IUserService;

import Exceptions.DBExceptions.DataAccessException;

import Sessions.UserSession;

import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import UI.Pages.AdminHomePage;

/**
 * The AdminHomePageController class handles tasks 
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
public class AdminHomePageController {
	private final IUserService userService;
	private final AdminHomePage view;
	private final Stage primaryStage;
	private final UserSession userSession;
	private String selectedDropdownUser = null;
	
	List<String> users;
	
    /**
     * Creates a new AdminHomePageController.
     * Initializes the layout and sets up button actions for user management.
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
		setSaveRolesButtonAction();
	}
	
	// ===================
    // 		 SETTERS 
 	// ===================
	
    /**
     * Sets up the initial layout of the admin home page.
     * Loads all usernames into the dropdown menu.
     */
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
	
    /**
     * Configures the action for updating user roles.
     * Collects selected roles from checkboxes and updates them in the database.
     */
	private void setUpdateRolesButtonAction() {
		view.getSaveRolesButton().setOnAction(_ -> {
			selectedDropdownUser = view.getUserDropdown().getValue();
			if (selectedDropdownUser == null) {
				return;
			}
			List<String> roles = new ArrayList<>();
			if (view.isAdminCheckBoxSelected()) roles.add("admin");
			if (view.isStudentCheckBoxSelected()) roles.add("student");
			if (view.isInstructorCheckBoxSelected()) roles.add("instructor");
			if (view.isStaffCheckBoxSelected()) roles.add("staff");
			if (view.isReviewerCheckBoxSelected()) roles.add("reviewer");

			String roleStr = String.join(",", roles);

			try {
				userService.updateUserRoles(selectedDropdownUser, roleStr);
				System.out.println("Updated Roles for " + selectedDropdownUser + ": " + roleStr);
				// refresh dropdown or user details
				view.setUserDetailsLabel("User: " + selectedDropdownUser + "\nRoles: " + (roleStr.isEmpty() ? "None" : roleStr));
			} catch (DataAccessException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		});
	}
	
    /**
     * Configures the action for setting a temporary password.
     * Updates the selected user's password and displays it in the view.
     */
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
	
    /**
     * Configures the action for when a user is selected from the dropdown.
     * Loads the user's roles and updates the checkboxes and details section.
     */
	private void setOnUserDropdownAction() {
		view.getUserDropdown().setOnAction(_ -> {
            String selectedUserName = view.getUserDropdown().getValue();
            if (selectedUserName == null) return;
            
            String roleStr;
            int userId;
            
            try {
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

            if (roleStr != null && !roleStr.trim().isEmpty()) {
                String[] parts = roleStr.split(",");
                for (String r : parts) {
                    String v = r.trim().toLowerCase();
                    switch (v) {
                        case "admin": view.setAdminCheckBox(true); break;
                        case "student": view.setStudentCheckBox(true); break;
                        case "instructor": view.setInstructorCheckBox(true); break;
                        case "staff": view.setStaffCheckBox(true); break;
                        case "reviewer": view.setReviewerCheckBox(true); break;
                        default: break;
                    }
                }
            }

            // Update details section
            view.setUserDetailsLabel(
                "User: " + selectedUserName + "\n" +
                "User ID: " + userId + "\n" +
                "Roles: " + (roleStr == null || roleStr.isEmpty() ? "None" : roleStr) + "\n" +
                "Other info: (e.g., last login, email, etc.)"
            );
        });

	}
	
    /**
     * Configures the action for deleting a user.
     * Prevents deleting the current admin's own account and confirms deletion before proceeding.
     */
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
	
    /**
     * Configures the action for saving user roles.
     * Updates the roles in the database and refreshes the details view.
     */
	private void setSaveRolesButtonAction() {

        String selectedUserName = view.getUserDropdown().getValue();
        if (selectedUserName == null) return;

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
            "User: " + selectedUserName + "\n" +
            "Roles: " + (roleStr.isEmpty() ? "None" : roleStr) + "\n" +
            "Other info: (e.g., last login, email, etc.)"
        );

        // Save roles in database
        try {
	        userService.updateUserRoles(selectedUserName, roleStr);
	        System.out.println("Updated Roles for " + selectedUserName + ": " + roleStr);
	        userService.printUsersTable();
        } catch (DataAccessException e) {
        	System.err.println(e.getMessage());
            e.printStackTrace();
        }
    
	}
}
