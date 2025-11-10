package Controllers;

import Controllers.ForumPage.ForumHeaderViewModel;
import Controllers.ForumPage.ForumPageController;
import Controllers.ForumPage.PostListViewModel;
import Controllers.ForumPage.QuestionPaneViewModel;
import Database.*;
import Database.Services.Interfaces.IQuestionService;
import Sessions.UserSession;
import UI.Pages.ForumPage;
import UI.Pages.MessagesPage;
import UI.Pages.ReviewerHomePage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;

import Utils.Debug;
import javafx.stage.Stage;

/**
 * The ReviewerHomePageController class manages the reviewer's home page.
 * It handles navigation actions such as logging out, changing roles,
 * entering the forum, and accessing the messages page.
 */
public class ReviewerHomePageController {
	
	/**
	 *  Reference to the DatabaseHelper for database interactions
	 */
	private final DBConnectionStore dBConnectionStore;
	private final UserSession userSession;
	private final IQuestionService questionService;
	private final Stage primaryStage;
	private final ReviewerHomePage view;
	
	private final PostListViewModel postListViewModel;
	private final ForumHeaderViewModel reviewerHeaderViewModel;
	private final QuestionPaneViewModel questionPaneViewModel;
	
    /**
     * Creates a new ReviewerHomePageController.
     * Initializes the layout, sets up button actions, and configures search functionality.
     */
	public ReviewerHomePageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, ReviewerHomePage view, 
			PostListViewModel postListViewModel, ForumHeaderViewModel reviewerHeaderViewModel, QuestionPaneViewModel questionPaneViewModel) {
		this.dBConnectionStore = dBConnectionStore;
		this.questionService = dBConnectionStore.getQuestionService();
		this.primaryStage = primaryStage;
		this.userSession = userSession;
		this.view = view;
		this.postListViewModel = postListViewModel;
		this.reviewerHeaderViewModel = reviewerHeaderViewModel;
		this.questionPaneViewModel = questionPaneViewModel;
	    
	        
	    setInitialLayout();
	    setEnterForumButtonAction();
	    setLogoutButtonAction();
	    setChangeRolesButtonAction();
	    setEnterMessagesPageButtonAction();
	    
		reviewerHeaderViewModel.getSearchButtonPressedProperty().addListener((_, _, isTriggered) -> {
			if (isTriggered) {
				postListViewModel.filterVisibleQuestionsByText(reviewerHeaderViewModel.getSearchFieldText());
			}
		});	    
	    if(Debug.isEnabled) {
        	setUpDebugEnvironment();
        }
	}
	
	// ===================
    // 		 SETTERS 
 	// ===================
	
    /**
     * Configures the logout button action.
     * Navigates back to the login page.
     */
	private void setLogoutButtonAction() {
		view.getLogoutButton().setOnAction(_ -> {
			// Return to login page
			// TODO: -- Need to empty UserSession
	        new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
		});
	}
	
    /**
     * Configures the change roles button action.
     * Navigates back to the welcome page.
     */
	private void setChangeRolesButtonAction() {
		view.getChangeRolesButton().setOnAction(_ -> {
			// Return to welcome page
			// TODO: -- Needs to be DB call here along with UI update
	    	new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());
		});
	}
	
    /**
     * Configures the enter forum button action.
     * Navigates to the forum page.
     */
	private void setEnterForumButtonAction() {
		view.getEnterForumButton().setOnAction(_ -> {
			// Enter the forum page
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
					forumHeaderViewModel,
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
			// Enter the messages page
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
     * Sets up the initial layout of the reviewer home page.
     */
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
    
    private final void setUpDebugEnvironment() {
    	//view.addDebugLabel();
    }
}