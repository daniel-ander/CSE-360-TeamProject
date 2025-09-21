package application;

import java.sql.SQLException;
import java.util.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import databasePart1.*;

/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */
public class AdminHomePage {
    
    private final DatabaseHelper databaseHelper;
    private final String currentAdmin; // logged-in admin username
    
    public AdminHomePage(DatabaseHelper databaseHelper, String currentAdmin) {
        this.databaseHelper = databaseHelper;
        this.currentAdmin = currentAdmin;
    }
    
    public void show(Stage primaryStage) {
        VBox layout = new VBox();
        HBox roleBox = new HBox(20);
        
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        roleBox.setStyle(null);

        // Checkboxes for role selection
        CheckBox adminCheck = new CheckBox("Admin");
        CheckBox studentCheck = new CheckBox("Student");
        CheckBox instructorCheck = new CheckBox("Instructor");
        CheckBox staffCheck = new CheckBox("Staff");
        CheckBox reviewerCheck = new CheckBox("Reviewer");

        // Admins can't be un-admined by UI, so disable interaction
        adminCheck.setDisable(true);
        
        // Welcome label
        Label adminLabel = new Label("Hello, Admin!\n Select a User, Select their roles, then save.");
        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Dropdown with all usernames
        ComboBox<String> userDropDown = new ComboBox<>();
        List<String> users = databaseHelper.getAllUsernames();
        userDropDown.setPromptText("Select user");
        userDropDown.getItems().addAll(users);

        // User details section
        Label userDetails = new Label("Select a user to view details.");
        userDetails.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-font-size: 14px;");
        
        Label tempPasswordInfo = new Label();
        tempPasswordInfo.setStyle("-fx-text-fill: blue; -fx-font-size: 12px;");

        Button setTempPassword = new Button("Set Temp Password");
        setTempPassword.setOnAction(a -> {
            String selectedUser = userDropDown.getValue();
            if (selectedUser == null) return;

            String tempPass = databaseHelper.issueResetPassword(selectedUser);
            
            if (tempPass == null) {
                tempPasswordInfo.setText("Error setting temp password");
            } else {
                tempPasswordInfo.setText("Temp password for " + selectedUser + " is: " + tempPass);
            }
        });

        // Populate checkboxes and details when user is selected
        userDropDown.setOnAction(a -> {
            String selectedUser = userDropDown.getValue();
            if (selectedUser == null) return;

            String roleStr = databaseHelper.getUserRole(selectedUser);

            // Reset all checkboxes
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

            // Prevent deleting own admin account
            if (selectedUser.equals(currentAdmin)) {
                Alert warn = new Alert(Alert.AlertType.WARNING);
                warn.setTitle("Action Not Allowed");
                warn.setHeaderText("You cannot delete your own admin account.");
                warn.setContentText("Please have another admin remove this account if needed.");
                warn.showAndWait();
                return;
            }

            // Confirm deletion
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete User");
            confirm.setHeaderText("Are you sure you want to delete user: " + selectedUser + "?");
            confirm.setContentText("This action cannot be undone.");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean success = databaseHelper.deleteUser(currentAdmin, selectedUser);
                if (success) {
                    userDropDown.getItems().remove(selectedUser);
                    userDropDown.setValue(null);

                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Deleted");
                    info.setHeaderText(null);
                    info.setContentText("User " + selectedUser + " deleted.");
                    info.showAndWait();
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("Failed to delete user");
                    error.setContentText("See logs for details.");
                    error.showAndWait();
                }
            }
        });

        // Save roles button
        Button saveRoles = new Button("Save");
        saveRoles.setOnAction(a -> {
            String selectedUser = userDropDown.getValue();
            if (selectedUser == null) return;

            // Build new role list from checkboxes
            List<String> roles = new ArrayList<>();
            if (adminCheck.isSelected()) roles.add("admin");
            if (studentCheck.isSelected()) roles.add("student");
            if (instructorCheck.isSelected()) roles.add("instructor");
            if (staffCheck.isSelected()) roles.add("staff");
            if (reviewerCheck.isSelected()) roles.add("reviewer");

            // Update details view
            String roleStr = String.join(",", roles);
            userDetails.setText(
                "User: " + selectedUser + "\n" +
                "Roles: " + (roleStr.isEmpty() ? "None" : roleStr) + "\n" +
                "Other info: (e.g., last login, email, etc.)"
            );

            // Save roles in database
            databaseHelper.updateUserRoles(selectedUser, roles);
            System.out.println("Updated Roles for " + selectedUser + ": " + roles);
            databaseHelper.printUsersTable();
        });

        // Add everything to layout
        roleBox.getChildren().addAll(
            userDropDown,
            new Label("Choose Role to add"),
            adminCheck, studentCheck, instructorCheck, staffCheck, reviewerCheck,
            saveRoles
        );

        layout.getChildren().addAll(adminLabel, roleBox, userDetails, deleteUser, setTempPassword, tempPasswordInfo);
        Scene adminScene = new Scene(layout, 800, 400);

        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
    }
}
