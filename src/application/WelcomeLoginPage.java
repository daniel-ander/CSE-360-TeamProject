package application;

import java.util.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import databasePart1.*;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 *
 * 🔑 KEY FEATURES:
 * - Pulls user roles from the database.
 * - Shows a "Continue" button → navigates based on role.
 * - If user has multiple roles → pops up a ChoiceDialog to select one.
 * - Adds "Invite" button for admins.
 * - Quit button closes DB + exits app.
 */
public class WelcomeLoginPage {
    
    private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Display the Welcome screen after login.
     * @param primaryStage The main JavaFX stage.
     * @param user The logged-in user object.
     */
    public void show(Stage primaryStage, User user) {
        
        // Base layout container
        VBox layout = new VBox(5);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Page title / greeting
        Label welcomeLabel = new Label("Welcome!!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // 🔍 FETCH ROLES: query DB for all roles tied to this username
        String roleStr = databaseHelper.getUserRole(user.getUserName());
        ArrayList<String> roles = new ArrayList<>();
        if (roleStr != null && !roleStr.isEmpty()) {
            for (String r : roleStr.split(",")) {
                roles.add(r.trim().toLowerCase(Locale.ROOT));
            }
        }
        
        // 🎯 Continue button → routes user into their page
        Button continueButton = new Button("Continue to your Page");
        continueButton.setOnAction(a -> {
            if (roles.isEmpty()) {
                System.out.println("No roles found for user: " + user.getUserName());
                return;
            }

            if (roles.size() == 1) {
                // ✅ CASE 1: Only one role → jump straight in
                navigateToPage(primaryStage, roles.get(0), user.getUserName());
            } else {
                // ✅ CASE 2: Multiple roles → ask which to use
                ChoiceDialog<String> dialog = new ChoiceDialog<>(roles.get(0), roles);
                dialog.setTitle("Select Role");
                dialog.setHeaderText("You have multiple roles");
                dialog.setContentText("Choose the role to continue with:");

                dialog.showAndWait().ifPresent(selectedRole -> {
                    navigateToPage(primaryStage, selectedRole, user.getUserName());
                });
            }
        });

        // 🚪 Quit button → closes DB connection + exits app
        Button quitButton = new Button("Quit");
        quitButton.setOnAction(a -> {
            databaseHelper.closeConnection();
            Platform.exit(); // Exit the JavaFX application
        });
        
        // 📨 Invite button → only visible if user is an admin
        // (Navigates to InvitationPage)
        if (roles.contains("admin")) {
            Button inviteButton = new Button("Invite");
            inviteButton.setOnAction(a -> {
                new InvitationPage(databaseHelper, roleStr).show(databaseHelper, primaryStage);
            });
            layout.getChildren().add(inviteButton);
        }

        // Assemble UI
        layout.getChildren().addAll(welcomeLabel, continueButton, quitButton);
        Scene welcomeScene = new Scene(layout, 800, 400);

        // Show the scene
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Welcome Page");
    }

    /**
     * 🔀 Central branching method:
     * Based on role, decide which HomePage to launch.
     *
     * ✨ Where to look if you want to add a new role:
     * 1. Add it to your DB (users can be assigned).
     * 2. Add checkbox in AdminHomePage if needed.
     * 3. Add a case here → launch NewRoleHomePage.
     */
    private void navigateToPage(Stage stage, String role, String username) {
        switch (role) {
            case "admin":
                new AdminHomePage(databaseHelper, username).show(stage);
                break;
                /* functionality for later roles?
            case "student":
                new StudentHomePage(databaseHelper, username).show(stage);
                break;
            case "instructor":
                new InstructorHomePage(databaseHelper, username).show(stage);
                break;
            case "staff":
                new StaffHomePage(databaseHelper, username).show(stage);
                break;
            case "reviewer":
                new ReviewerHomePage(databaseHelper, username).show(stage);
                break;
            case "user":
                new UserHomePage(databaseHelper, username).show(stage);
                break;
            default:
                // 👀 Fallback if role has no page mapped yet
                System.out.println("No page mapped for role: " + role);
                break;
                 */
                
        }
    }
}
