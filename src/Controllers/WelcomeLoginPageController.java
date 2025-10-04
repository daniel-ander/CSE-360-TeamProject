package Controllers;

import Models.User;
import Sessions.UserSession;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Locale;

import javafx.application.Platform;
import UI.Pages.AdminHomePage;
import UI.Pages.InstructorHomePage;
import UI.Pages.InvitationPage;
import UI.Pages.ReviewerHomePage;
import UI.Pages.StaffHomePage;
import UI.Pages.StudentHomePage;
import UI.Pages.WelcomeLoginPage;
import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class WelcomeLoginPageController {
	
	private final DBConnectionStore dBConnectionStore;
    private final IUserRepository userRepository;
    private final Stage primaryStage;
    private final WelcomeLoginPage view;
    private final User user;
    private final UserSession userSession;
    
    String roleStr;
    ArrayList<String> roles = new ArrayList<String>();

    public WelcomeLoginPageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, WelcomeLoginPage view) {
    	this.dBConnectionStore = dBConnectionStore;
        this.userRepository = dBConnectionStore.getUserRepository();
        this.userSession = userSession;
        
        this.primaryStage = primaryStage;
        this.user = userSession.currentUser();
        this.view = view;
        
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
            System.out.println("No page mapped for role: " + roles.size());
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
    	view.getQuitButton().setOnAction(a -> {
            Platform.exit(); // Exit the JavaFX application
        });
    }
    
    private void setInviteButtonAction() {
    	view.getInviteButton().setOnAction(a -> {
            new InvitationPageController(dBConnectionStore, primaryStage, new InvitationPage());
        });
    	
    	view.getLayout().getChildren().add(view.getInviteButton());
    }
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
    
    private void assignRoles(User user) {
    	this.roleStr = userRepository.getUserRole(user.getUserName());
        
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
            	new AdminHomePageController(dBConnectionStore, stage, userSession, new AdminHomePage());
                break;
            case "student":
                new StudentHomePageController(dBConnectionStore, stage, userSession, new StudentHomePage());
                break;
            case "instructor":
                new InstructorHomePageController(dBConnectionStore, stage, userSession, new InstructorHomePage());
                break;
            case "staff":
                new StaffHomePageController(dBConnectionStore, stage, userSession, new StaffHomePage());
                break;
            case "reviewer":
                new ReviewerHomePageController(dBConnectionStore, stage, userSession, new ReviewerHomePage());
                break;
            case "user":
                new StudentHomePageController(dBConnectionStore, stage, userSession, new StudentHomePage());
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

