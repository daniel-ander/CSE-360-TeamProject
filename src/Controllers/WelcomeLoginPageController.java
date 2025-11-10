package Controllers;

import Models.User;

import Sessions.UserSession;

import javafx.stage.Stage;
import javafx.application.Platform;

import java.util.ArrayList;

import Controllers.ForumPage.ForumHeaderViewModel;
import Controllers.ForumPage.PostListViewModel;
import Controllers.ForumPage.QuestionPaneViewModel;
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

/**
 * The WelcomeLoginPageController class manages the welcome page after user login.
 * It validates user roles, allows role selection if multiple exist,
 * and navigates to the appropriate home page based on the chosen role.
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

    /**
     * Creates a new WelcomeLoginPageController.
     * Initializes layout, assigns roles, and sets up button actions.
     */
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
    // 		SETTERS 
    // ===================
    
    /**
     * Configures the continue button action.
     * If the user has multiple roles, prompts them to select one.
     * Navigates to the appropriate page based on the chosen role.
     */
    private void setContinueButtonAction() {
     
        view.getContinueButton().setOnAction(_ -> {
            if (roles.isEmpty()) {
                System.out.println("No roles found for user: " + user.getUserName());
                return;
            }
            if (roles.size() == 1) {
                navigateToPage(primaryStage, roles.get(0), user.getUserName());
            } else {
                javafx.scene.control.ChoiceDialog<String> dialog = new javafx.scene.control.ChoiceDialog<>(roles.get(0), roles);
                dialog.setTitle("Select Role");
                dialog.setHeaderText("You have multiple roles");
                dialog.setContentText("Choose the role to continue with:");

                dialog.showAndWait().ifPresent(selectedRole -> {
                    navigateToPage(primaryStage, selectedRole, user.getUserName());
                });
            }
        });
 	}
    
    /**
     * Configures the quit button action.
     * Exits the JavaFX application.
     */
    private void setQuitButtonAction() {
    	view.getQuitButton().setOnAction(_ -> {
            Platform.exit(); // Exit the JavaFX application
        });
    }
    
    /**
     * Configures the invite button action.
     */
    private void setInviteButtonAction() {
    	view.getInviteButton().setOnAction(_ -> {
            new InvitationPageController(dBConnectionStore, primaryStage, new InvitationPage());
        });
     	
     	view.getLayout().getChildren().add(view.getInviteButton());
    }
    
    /**
     * Sets up the initial layout of the welcome login page.
     */
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
    
    /**
     * Assigns roles to the current user by querying the database.
     * Adds the invite button if the user has the admin role.
     */
    private void assignRoles(User user) {
     try {
         String roleStr = userService.getUserRolesString(user.getUserName());
         roles.clear();
         if (roleStr == null || roleStr.trim().isEmpty()) {
             // fallback to single role lookup
             try {
                 User.Role role = userService.getUserRole(user.getUserName());
                 if (role != null) roles.add(role.name());
             } catch (DataAccessException ignore) {}
         } else {
             for (String r : roleStr.split(",")) {
                 String v = r.trim();
                 if (!v.isEmpty()) roles.add(v);
             }
         }
         
         if (roles.contains("admin")) {
             setInviteButtonAction();
         }
     } catch(DataAccessException e) {
         System.err.println(e.getMessage());
             e.printStackTrace();
     }
 }
    
    /**
     * Central branching method.
     * Based on role, decides which home page to launch.
     * Extend this method to add new roles in the future.
     */
    private void navigateToPage(Stage stage, String roleName, String username) {
        switch (roleName) {
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
                PostListViewModel postListViewModel = new PostListViewModel(
                        dBConnectionStore.getQuestionService(), 
                        dBConnectionStore.getAnswerService(),
                        userSession
                );
                Controllers.ForumPage.ForumHeaderViewModel forumHeaderViewModel = new Controllers.ForumPage.ForumHeaderViewModel(userSession); 
                Controllers.ForumPage.QuestionPaneViewModel questionPaneViewModel = new Controllers.ForumPage.QuestionPaneViewModel();
                new ReviewerHomePageController(dBConnectionStore, stage, userSession, new ReviewerHomePage(forumHeaderViewModel, questionPaneViewModel), postListViewModel, forumHeaderViewModel, questionPaneViewModel);
                break;
            case "user":
                new StudentHomePageController(dBConnectionStore, stage, userSession, new StudentHomePage());
                break;
            default:
                System.out.println("No page mapped for role: " + roleName);
                break;
        }
    }

}