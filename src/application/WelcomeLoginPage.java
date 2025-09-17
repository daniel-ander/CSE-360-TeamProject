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
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
    	
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Label welcomeLabel = new Label("Welcome!!");
	    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    // pull roles from DB for this user
	    String roleStr = databaseHelper.getUserRole(user.getUserName());
	    ArrayList<String> roles = new ArrayList<>();
	    if (roleStr != null && !roleStr.isEmpty()) {
	        for (String r : roleStr.split(",")) {
	            roles.add(r.trim().toLowerCase(Locale.ROOT));
	        }
	    }
	    
	    // Button to navigate to the user's respective page based on their role(s)
	    Button continueButton = new Button("Continue to your Page");
	    continueButton.setOnAction(a -> {
	        if (roles.isEmpty()) {
	            System.out.println("No roles found for user: " + user.getUserName());
	            return;
	        }

	        if (roles.size() == 1) {
	            // Only one role → go straight in
	            navigateToPage(primaryStage, roles.get(0));
	        } else {
	            // Multiple roles → ask user to choose
	            ChoiceDialog<String> dialog = new ChoiceDialog<>(roles.get(0), roles);
	            dialog.setTitle("Select Role");
	            dialog.setHeaderText("You have multiple roles");
	            dialog.setContentText("Choose the role to continue with:");

	            dialog.showAndWait().ifPresent(selectedRole -> {
	                navigateToPage(primaryStage, selectedRole);
	            });
	        }
	    });

	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });
	    
	    // "Invite" button for admin role
	    if (roles.contains("admin")) {
            Button inviteButton = new Button("Invite");
            inviteButton.setOnAction(a -> {
                new InvitationPage().show(databaseHelper, primaryStage);
            });
            layout.getChildren().add(inviteButton);
        }

	    layout.getChildren().addAll(welcomeLabel, continueButton, quitButton);
	    Scene welcomeScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
    }

    // central branching helper
    private void navigateToPage(Stage stage, String role) {
        switch (role) {
            case "admin":
                new AdminHomePage(databaseHelper).show(stage);
                break;
            case "user":
                new UserHomePage().show(stage);
                break;
            // Future roles could be added here:
            // case "student": new StudentHomePage().show(stage); break;
            // case "instructor": new InstructorHomePage().show(stage); break;
            // case "staff": new StaffHomePage().show(stage); break;
            // case "reviewer": new ReviewerHomePage().show(stage); break;
            default:
                System.out.println("No page mapped for role: " + role);
        }
    }
}
