package Controllers;

import Models.User;

import Sessions.UserSession;

import javafx.stage.Stage;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import Database.*;
import Database.Services.Interfaces.IUserService;

import Exceptions.DBExceptions.DataAccessException;

import UI.Pages.AdminHomePage;
import UI.Pages.InstructorHomePage;
import UI.Pages.InvitationPage;
import UI.Pages.ReviewerHomePage;
import UI.Pages.StaffHomePage;
import UI.Pages.StudentHomePage;
import UI.Pages.WelcomeLoginPage;

import javafx.scene.control.ChoiceDialog;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class WelcomeLoginPageController {
    
    private final DBConnectionStore dBConnectionStore;
    private final IUserService userService;
    private final Stage primaryStage;
    private final WelcomeLoginPage view;
    private final User user;
    private final UserSession userSession;
    
    String roleString;
    ArrayList<String> roles = new ArrayList<String>();

    public WelcomeLoginPageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, WelcomeLoginPage view) {
        this.dBConnectionStore = dBConnectionStore;
        this.userService = dBConnectionStore.getUserService();
        this.userSession = userSession;
        
        this.primaryStage = primaryStage;
        this.user = userSession.currentUser();
        this.view = view;
        
        assignRoles(user);
        
        setInitialLayout();
        setContinueButtonAction();
        setQuitButtonAction();
    }
    
    // ===================
    //        SETTERS 
    // ===================
    
    private void setContinueButtonAction() {
        // 🎯 Continue button → routes user into their page
        view.getContinueButton().setOnAction(_ -> {
            try {
                if (roles == null || roles.isEmpty()) {
                    // fallback: use single role stored on User object
                    navigateToPage(primaryStage, userSession.currentUser().getRole(), user.getUserName());
                    return;
                }

                if (roles.size() == 1) {
                    // single role
                    User.Role r = User.roleFromString(roles.get(0));
                    // ensure session user reflects chosen role
                    userSession.currentUser().changeRole(r);
                    navigateToPage(primaryStage, r, user.getUserName());
                    return;
                }

                // multiple roles -> prompt user to pick one
                ChoiceDialog<String> dialog = new ChoiceDialog<>(roles.get(0), roles);
                dialog.setTitle("Select Role");
                dialog.setHeaderText("You have multiple roles");
                dialog.setContentText("Choose the role to continue with:");

                dialog.showAndWait().ifPresent(selectedRole -> {
                    try {
                        User.Role chosen = User.roleFromString(selectedRole);
                        userSession.currentUser().changeRole(chosen);
                        navigateToPage(primaryStage, chosen, user.getUserName());
                    } catch (Exception e) {
                        System.err.println("Invalid role selected: " + e.getMessage());
                    }
                });

            } catch (Exception e) {
                System.err.println("Error during role selection: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private void setQuitButtonAction() {
        view.getQuitButton().setOnAction(_ -> {
            Platform.exit(); // Exit the JavaFX application
        });
    }
    
    private void setInviteButtonAction() {
        view.getInviteButton().setOnAction(_ -> {
            new InvitationPageController(dBConnectionStore, primaryStage, new InvitationPage());
        });
        
        view.getLayout().getChildren().add(view.getInviteButton());
    }
    
    private void setInitialLayout() {
        view.setInitialLayout(primaryStage);
    }
    
    private void assignRoles(User user) {
        try {
            // Fetch the raw comma-separated roles string and split into individual roles
            String rolesRaw = userService.getUserRolesString(user.getUserName());
            if (rolesRaw == null || rolesRaw.trim().isEmpty()) {
                // Fallback to single role stored on the User object
                roles.clear();
                roles.add(user.getRole().name());
            } else {
                String[] tokens = rolesRaw.split(",");
                roles.clear();
                for (String t : tokens) {
                    String r = t.trim();
                    if (!r.isEmpty()) roles.add(r);
                }
            }

            // If any role is admin, show invite button
            for (String r : roles) {
                if (r.equalsIgnoreCase("admin")) {
                    setInviteButtonAction();
                    break;
                }
            }
        } catch(DataAccessException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            // Fallback: keep the role from the User object
            roles.clear();
            roles.add(user.getRole().name());
        }
    }
    
    /**
     * 🔀 Central branching method:
     * Based on role, decide which HomePage to launch.
     */
    private void navigateToPage(Stage stage, User.Role role, String username) {
        switch (role.name()) {
            case "admin":
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
            default:
                System.out.println("No page mapped for role: " + role);
                break;
        }
    }

}
