package Controllers;

import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Locale;

import Models.User;
import UI.Pages.InstructorHomePage;
import UI.Pages.WelcomeLoginPage;
import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class WelcomeLoginPageController {
	
    private final DatabaseHelper databaseHelper;
    private final Stage primaryStage;
    private final WelcomeLoginPage view;
    private final User user;
    
    String roleStr;
    ArrayList<String> roles;

    public WelcomeLoginPageController(DatabaseHelper databaseHelper, Stage primaryStage, User user) {
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
        this.user = user;
        this.view = new WelcomeLoginPage();
        
        assignRoles(user);
        
        setInitialLayout();
        setContinueButtonAction();
        setQuitButtonAction();
    }
    
    
    private void setContinueButtonAction() {
    	// 🎯 Continue button → routes user into their page
        view.getContinueButton().setOnAction(a -> {
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
	}
    
    private void setQuitButtonAction() {
    	view.getInviteButton().setOnAction(a -> {
            new InvitationPageController(databaseHelper, primaryStage, roleStr);
        });
    	
    	view.getLayout().getChildren().add(view.getInviteButton());
    }
    
    private void setInviteButtonAction() {
    	view.getInviteButton().setOnAction(a -> {
            new InvitationPageController(databaseHelper, primaryStage, roleStr);
        });
    	
    	view.getLayout().getChildren().add(view.getInviteButton());
    }
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
    
    private void assignRoles(User user) {
    	this.roleStr = databaseHelper.getUserRole(user.getUserName());
        ArrayList<String> roles = new ArrayList<>();
        if (roleStr != null && !roleStr.isEmpty()) {
            for (String r : roleStr.split(",")) {
                roles.add(r.trim().toLowerCase(Locale.ROOT));
            }
        }
       
       if(roles.contains("admin")) {
    	   setInviteButtonAction();
       }
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
                //new AdminHomePageController(databaseHelper, username).show(stage);
            	new AdminHomePageController(databaseHelper, stage, username);
                break;
            case "student":
                new StudentHomePageController(databaseHelper, stage, username);
                break;
            case "instructor":
                new InstructorHomePageController(databaseHelper, stage);
                break;
            case "staff":
                new StaffHomePageController(databaseHelper, stage, username);
                break;
            case "reviewer":
                new ReviewerHomePageController(databaseHelper, stage, username);
                break;
            case "user":
                new StudentHomePageController(databaseHelper, stage, username);
                break;
            // Add more roles here as needed, following the same pattern.
                
            
            // since this isnt dynamic youll have to update other places too
            default:
                // 👀 Fallback if role has no page mapped yet
                System.out.println("No page mapped for role: " + role);
                break;
        }
    }

}

