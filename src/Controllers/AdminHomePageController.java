package Controllers;

import java.sql.SQLException;
import java.util.*;

import Controllers.Interfaces.IPageController;
import Database.*;

import Database.Services.Interfaces.IUserService;
import Models.User;
import Sessions.UserSession;

import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import UI.Pages.AdminHomePage;

public class AdminHomePageController implements IPageController {
	private final IUserService userService;
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
	
	private void setInitialLayout() {
		view.setInitialLayout(primaryStage);
		
		try {
			users = userService.getAllUsernames();
		} catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
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

			// TODO :- Need to figure out what we're doing with roles
			try {
				userService.updateUserRole(selectedDropdownUser, User.Role.admin);
				System.out.println("Updates Roles for " + selectedDropdownUser + ": " + roles);
			} catch (SQLException e) {
	            System.err.println("Database error: " + e.getMessage());
	            e.printStackTrace();
	        }
		});
	}
	
	private void setSetTempPasswordButtonAction() {
		view.getSetTempPasswordButton().setOnAction(a -> {
            String selectedUser = view.getUserDropdown().getValue();
            if (selectedUser == null) return;
            
            String tempPass = null;
            
            try {
            	tempPass = userService.resetPasswordForUser(selectedUser);
            } catch (SQLException e) {
	            System.err.println("Database error: " + e.getMessage());
	            e.printStackTrace();
	        }

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
            
            User.Role role;
            
            try {
            	role = userService.getUserRole(selectedUser);
            } catch (SQLException e) {
	            System.err.println("Database error: " + e.getMessage());
	            e.printStackTrace();
	            
	            return;
	        }

            // Reset all checkboxes
            view.setAdminCheckBox(false);
            view.setStudentCheckBox(false);
            view.setInstructorCheckBox(false);
            view.setStaffCheckBox(false);
            view.setReviewerCheckBox(false);

            // NOTE :- Again, based on 
//            if (roleStr != null && !roleStr.isEmpty()) {
//                Set<String> roles = new HashSet<>();
//                for (String r : roleStr.split(",")) {
//                    roles.add(r.trim().toLowerCase(Locale.ROOT));
//                }
//                if (roles.contains("admin")) view.setAdminCheckBox(true);
//                if (roles.contains("student")) view.setStudentCheckBox(true);
//                if (roles.contains("instructor")) view.setInstructorCheckBox(true);
//                if (roles.contains("staff")) view.setStaffCheckBox(true);
//                if (roles.contains("reviewer")) view.setReviewerCheckBox(true);
//            }
            
            if (role.name().equals("admin")) view.setAdminCheckBox(true);
            if (role.name().equals("student")) view.setStudentCheckBox(true);
            if (role.name().equals("instructor")) view.setInstructorCheckBox(true);
            if (role.name().equals("staff")) view.setStaffCheckBox(true);
            if (role.name().equals("reviewer")) view.setReviewerCheckBox(true);

            // Update details section
            view.setUserDetailsLabel(
                "User: " + selectedUser + "\n" +
                "Roles: " + role.name() + "\n" +
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
            	try {
	                boolean success = userService.deleteUser(userSession.getCurrentUserName(), selectedUser);
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
            	} catch (SQLException e) {
    	            System.err.println("Database error: " + e.getMessage());
    	            e.printStackTrace();
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
        try {
	        userService.updateUserRole(selectedUser, User.Role.admin);
	        System.out.println("Updated Roles for " + selectedUser + ": " + roles);
	        userService.printUsersTable();
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    
	}
}