package Controllers;

import Models.User;
import Controllers.ForumPage.ForumHeaderViewModel;
import Controllers.ForumPage.ForumPageController;
import Controllers.ForumPage.PostListViewModel;
import Controllers.ForumPage.QuestionPaneViewModel;
import Controllers.Messages.MessagesPageController;
import Database.*;
import Database.Services.Interfaces.IUserService;

import Exceptions.DBExceptions.DataAccessException;

import Sessions.UserSession;
import UI.Pages.ForumPage;
import UI.Pages.InstructorHomePage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;
import UI.Pages.InstructorRequestsPage;
import UI.Pages.MessagesPage;
import Utils.Debug;

import javafx.stage.Stage;

/**
 * The InstructorHomePageController class manages the instructor's home page.
 * It handles navigation actions such as logging out and managing reviewer requests.
 */
public class InstructorHomePageController {
	
	/** Reference to the DatabaseHelper for database interactions */
	private final DBConnectionStore dBConnectionStore;
	private final IUserService userService;
	private final Stage primaryStage;
	private final InstructorHomePage view;
	private final UserSession userSession;
	
    /**
     * Creates a new InstructorHomePageController.
     * Initializes the layout, sets up button actions, and configures debug environment if enabled.
     */
	public InstructorHomePageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, InstructorHomePage view) {
		this.dBConnectionStore = dBConnectionStore;
        this.userService = dBConnectionStore.getUserService();
        this.view = view;
        this.primaryStage = primaryStage;
        this.userSession = userSession;
        
        setInitialLayout();
        
        setLogoutButtonAction();
        setEnterForumButtonAction();
        setEnterMessagesPageButtonAction();
        setChangeRolesButtonAction();
        setManageRequestsButtonAction();
		if (Debug.isEnabled) {
			// Additional debug setup can be added here
		}
		
		
	}
	
	// ===================
    // 		 SETTERS 
 	// ===================
	
	/**
	 * Configures the enter forum button action.
	 * Navigates to the forum page.
	 */
	private void setEnterForumButtonAction() {
		view.getEnterForumButton().setOnAction(_ -> {
			PostListViewModel postListViewModel = new PostListViewModel(
					dBConnectionStore.getQuestionService(),
					dBConnectionStore.getAnswerService(),
					dBConnectionStore.getUserService(),
					userSession
			);
			
			ForumHeaderViewModel forumHeaderViewModel = new ForumHeaderViewModel(userSession);
			QuestionPaneViewModel questionPaneViewModel = new QuestionPaneViewModel();
			
			new ForumPageController(
					dBConnectionStore,
					primaryStage,
					userSession,
					new ForumPage(forumHeaderViewModel, questionPaneViewModel),
					postListViewModel,
					forum_header_view_model_safe(forumHeaderViewModel),
					questionPaneViewModel
			);
		});
	}
	
	
	/**
	 * Configures the enter messages page button action.
	 * Navigates to the messages page.
	 */
	private void setEnterMessagesPageButtonAction() {
		view.getEnterMessagesPageButton().setOnAction(_ -> {
			PostListViewModel postListViewModel = new PostListViewModel(
					dBConnectionStore.getQuestionService(),
					dBConnectionStore.getAnswerService(),
					dBConnectionStore.getUserService(),
					userSession
			);
			
			ForumHeaderViewModel messagesHeaderViewModel = new ForumHeaderViewModel(userSession);
			QuestionPaneViewModel questionPaneViewModel = new QuestionPaneViewModel();
			
			new MessagesPageController(
					dBConnectionStore,
					primaryStage,
					userSession,
					new MessagesPage(messagesHeaderViewModel, questionPaneViewModel),
					postListViewModel,
					messagesHeaderViewModel,
					questionPaneViewModel
			);
		});
	}
	
	
    
    /**
     * Configures the logout button action.
     * Navigates the user back to the login page.
     */
    private void setLogoutButtonAction() {
    	view.getLogoutButton().setOnAction(_ -> {
	        // Return to login page
	         new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
	    });
	}
    
    /**
     * Configures the change roles button action.
     * Retrieves the current user's role and navigates back to the welcome page.
     */
    private void setChangeRolesButtonAction() {
    	view.getChangeRolesButton().setOnAction(_ -> {
    		
    		try {
    			User.Role role = userService.getUserRole(userSession.getCurrentUserName());
    			// TODO: -- Change role in DB 
    		} catch (DataAccessException e) {
	            System.err.println(e.getMessage());
	            e.printStackTrace();
	        }
	    	
	    	// Return to welcome page
	    	new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());

	    });
	}

    /**
     *  Add action to open the Manage Reviewer Requests page
     */
    private void setManageRequestsButtonAction() {
        view.getManageRequestsButton().setOnAction(_ -> {
            // Create controller for the requests page and show it
            InstructorRequestsPageController reqController = new InstructorRequestsPageController(dBConnectionStore, primaryStage, userSession, new InstructorRequestsPage());
            reqController.show();
        });
    }
    
    /**
     * Sets up the initial layout of the instructor home page.
     */
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }

	// small helper to avoid accidental refactor break in posted snippet
	private ForumHeaderViewModel forum_header_view_model_safe(ForumHeaderViewModel vm) { return vm; }
    
}