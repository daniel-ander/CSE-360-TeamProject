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
    // 		SETTERS 
    // ===================
    
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
     * 🔀 Central branching method:
     * Based on role, decide which HomePage to launch.
     *
     * ✨ Where to look if you want to add a new role:
     * 1. Add it to your DB (users can be assigned).
     * 2. Add checkbox in AdminHomePage if needed.
     * 3. Add a case here → launch NewRoleHomePage.
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