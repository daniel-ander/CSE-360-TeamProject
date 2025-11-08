package Controllers;

import java.util.ArrayList;
import java.util.List;

import Controllers.ForumPage.ForumHeaderViewModel;
import Controllers.ForumPage.ForumPageController;
import Controllers.ForumPage.PostListViewModel;
import Controllers.ForumPage.QuestionPaneViewModel;
import Controllers.Messages.RecentInteractionsViewModel;
import Database.*;
import Database.Services.UserService;
import Database.Services.Interfaces.IUserService;
import Database.Services.Interfaces.IQuestionService;
import Exceptions.DBExceptions.DataAccessException;
import Sessions.UserSession;
import UI.Pages.ForumPage;
import UI.Pages.MessagesPage;
import UI.Pages.ReviewerHomePage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;

import Utils.Debug;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */

public class MessagesPageController {

	/**
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
	
	// Reference to the DatabaseHelper for database interactions
	private final DBConnectionStore dBConnectionStore;
	private final UserSession userSession;
	private final IQuestionService questionService;
	private final Stage primaryStage;
	private final MessagesPage view;
	
	private final PostListViewModel messageListViewModel;
	private final ForumHeaderViewModel messagesHeaderViewModel;
	private final QuestionPaneViewModel questionPaneViewModel;
	private final IUserService userService;
	private final ListView<RecentInteractionsViewModel> recentUsersListView = new ListView<>();
	
		
	public MessagesPageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, MessagesPage view, 
			PostListViewModel postListViewModel, ForumHeaderViewModel messagesHeaderViewModel, QuestionPaneViewModel questionPaneViewModel) {
		this.dBConnectionStore = dBConnectionStore;
		this.questionService = dBConnectionStore.getQuestionService();
		this.primaryStage = primaryStage;
		this.userService = dBConnectionStore.getUserService();
		this.userSession = userSession;
		this.view = view;
		this.messageListViewModel = postListViewModel;
		this.messagesHeaderViewModel = messagesHeaderViewModel;
		this.questionPaneViewModel = questionPaneViewModel;
	    
	        
	    setInitialLayout();
	    setEnterForumButtonAction();
	    setLogoutButtonAction();
	    setChangeRolesButtonAction();
	    
		messagesHeaderViewModel.getSearchButtonPressedProperty().addListener((_, _, isTriggered) -> {
			if (isTriggered) {
				messageListViewModel.filterVisibleQuestionsByText(messagesHeaderViewModel.getSearchFieldText());
			}
		});	    
	    if(Debug.isEnabled) {
        	setUpDebugEnvironment();
        }
	}
	
	// ===================
    // 		 SETTERS 
 	// ===================
	
	private void setLogoutButtonAction() {
		view.getLogoutButton().setOnAction(_ -> {
			// Return to login page
			// TODO: -- Need to empty UserSession
	        new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
		});
	}
	
	private void setChangeRolesButtonAction() {
		view.getChangeRolesButton().setOnAction(_ -> {
			// Return to welcome page
			// TODO: -- Needs to be DB call here along with UI update
	    	new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());
		});
	}
	private void setEnterForumButtonAction() {
		view.getEnterForumButton().setOnAction(_ -> {
			// Enter the forum page
			PostListViewModel postListViewModel = new PostListViewModel(
					dBConnectionStore.getQuestionService(), 
					dBConnectionStore.getAnswerService(),
					userSession
			);
			
			ForumHeaderViewModel forumHeaderViewModel = new ForumHeaderViewModel(); 
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
	
    
	// java
	private void setInitialLayout() {
	    view.setInitialLayout(primaryStage);

	    initializeMessageListListeners();
	    setMessageListActions();
	    
	    // TODO: Replace this once real data is available
	    ObservableList<RecentInteractionsViewModel> users = FXCollections.observableArrayList(
	            new RecentInteractionsViewModel(1, "Alice", "See you soon!", "10:32 AM", 0),
	            new RecentInteractionsViewModel(2, "Bob", "Got your message", "9:47 AM", 2)
	    );

	    // Populate the ListView that's actually shown in the UI
	    view.getRecentInteractionsListView().setItems(users);

	}
    
    private void setMessageListActions() {
		// TODO Auto-generated method stub
		
	}

	private void initializeMessageListListeners() {
		// TODO Auto-generated method stub
		
	}

	private final void setUpDebugEnvironment() {
    	//view.addDebugLabel();
    }
}