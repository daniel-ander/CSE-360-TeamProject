// language: java
package Controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Controllers.ForumPage.ForumHeaderViewModel;
import Controllers.ForumPage.ForumPageController;
import Controllers.ForumPage.PostListViewModel;
import Controllers.ForumPage.QuestionPaneViewModel;
import Controllers.Messages.MessagesViewModel;
import Controllers.Messages.RecentInteractionsViewModel;
import Database.*;
import Database.Services.Interfaces.IMessageService;
import Database.Services.UserService;
import Database.Services.Interfaces.IUserService;
import Database.Services.Interfaces.IQuestionService;
import Exceptions.DBExceptions.DataAccessException;
import Models.Message;
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
 * MessagesPageController - populate MessagesPage from DB and hook cells/models.
 */
public class MessagesPageController {

	private final DBConnectionStore dBConnectionStore;
	private final UserSession userSession;
	private final IQuestionService questionService;
	private final Stage primaryStage;
	private final MessagesPage view;
	private final PostListViewModel messageListViewModel;
	private final IUserService userService;

	public MessagesPageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, MessagesPage view, 
			PostListViewModel postListViewModel, ForumHeaderViewModel messagesHeaderViewModel, QuestionPaneViewModel questionPaneViewModel) {
		this.dBConnectionStore = dBConnectionStore;
		this.questionService = dBConnectionStore.getQuestionService();
		this.primaryStage = primaryStage;
		this.userService = dBConnectionStore.getUserService();
		this.userSession = userSession;
		this.view = view;
		this.messageListViewModel = postListViewModel;

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

	private void setLogoutButtonAction() {
		view.getLogoutButton().setOnAction(_ -> {
	        new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
		});
	}

	private void setChangeRolesButtonAction() {
		view.getChangeRolesButton().setOnAction(_ -> {
	    	new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());
		});
	}

	private void setEnterForumButtonAction() {
		view.getEnterForumButton().setOnAction(_ -> {
			PostListViewModel postListViewModel = new PostListViewModel(
					dBConnectionStore.getQuestionService(), 
					dBConnectionStore.getAnswerService(),
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

	private void setInitialLayout() {
	    view.setInitialLayout(primaryStage);

	    initializeMessageListListeners();
	    setMessageListActions();
	    
	    // Try to load messages from service and populate the UI lists
	    IMessageService messageService = dBConnectionStore.getMessageService();
	    try {
	    	List<Message> messages = messageService.getMessagesForUsername(userSession.getCurrentUserName());
	    	// Build full messages list view models
	    	ObservableList<MessagesViewModel> messagesVMList = FXCollections.observableArrayList();
	    	for (Message m : messages) {
	    		messagesVMList.add(new MessagesViewModel(m));
	    	}

	    	// Build recent interactions by grouping messages by "other" participant and picking last message
	    	Map<Integer, Message> lastByOther = new HashMap<>();
	    	for (Message m : messages) {
	    		Integer sender = m.getSenderId();
	    		Integer receiver = m.getReceiverId();
	    		Integer current = userSession.getCurrentUserId();
	    		Integer other = null;
	    		if (current != null && current.equals(sender)) other = receiver;
	    		else other = sender;
	    		if (other == null) continue;
	    		Message existing = lastByOther.get(other);
	    		if (existing == null) {
	    			lastByOther.put(other, m);
	    		} else {
	    			// prefer later timestamp when available
	    			if (m.getSentTimestamp() != null && (existing.getSentTimestamp() == null || m.getSentTimestamp().after(existing.getSentTimestamp()))) {
	    				lastByOther.put(other, m);
	    			}
	    		}
	    	}

	    	ObservableList<RecentInteractionsViewModel> recentList = FXCollections.observableArrayList();
	    	for (Map.Entry<Integer, Message> entry : lastByOther.entrySet()) {
	    		Integer otherId = entry.getKey();
	    		Message lastMsg = entry.getValue();
	    		String usernameFallback = otherId == null ? "" : String.valueOf(otherId);
	    		String preview = lastMsg.getContent() == null ? "" : lastMsg.getContent();
	    		String ts = lastMsg.getSentTimestamp() == null ? "" : lastMsg.getSentTimestamp().toString();
	    		recentList.add(new RecentInteractionsViewModel(otherId == null ? 0 : otherId, usernameFallback, preview, ts, 0));
	    	}

	    	// Populate the UI list views
	    	view.getRecentInteractionsListView().setItems(recentList);
	    	view.getMessagesListView().setItems(messagesVMList);

	    	// When selecting a recent interaction, filter messages to that conversation
	    	view.getRecentInteractionsListView().getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
	    		if (newV == null) {
	    			view.getMessagesListView().setItems(messagesVMList);
	    			return;
	    		}
	    		int otherId = newV.getUserId();
	    		ObservableList<MessagesViewModel> convo = FXCollections.observableArrayList();
	    		for (Message m : messages) {
	    			if ((m.getSenderId() != null && m.getSenderId().equals(otherId)) ||
	    				(m.getReceiverId() != null && m.getReceiverId().equals(otherId))) {
	    				convo.add(new MessagesViewModel(m));
	    			}
	    		}
	    		view.getMessagesListView().setItems(convo);
	    	});

	    } catch (DataAccessException ex) {
	    	ex.printStackTrace();
	    	// fallback to placeholder data if DB call fails
	    	ObservableList<RecentInteractionsViewModel> users = FXCollections.observableArrayList(
	                new RecentInteractionsViewModel(1, "Alice", "See you soon!", "10:32 AM", 0),
	                new RecentInteractionsViewModel(2, "Bob", "Got your message", "9:47 AM", 2)
	        );

	        ObservableList<MessagesViewModel> messages = FXCollections.observableArrayList(
	                new MessagesViewModel(101, "Alice", "You", "Looking forward to our meeting.", "10:30 AM"),
	                new MessagesViewModel(102, "You", "Alice", "See you soon!", "10:32 AM"),
	                new MessagesViewModel(201, "Bob", "You", "Did you get my last message?", "9:45 AM"),
	                new MessagesViewModel(202, "You", "Bob", "Got your message", "9:47 AM")
	        );

	        view.getRecentInteractionsListView().setItems(users);
	        view.getMessagesListView().setItems(messages);
	    }
	}
    
    private void setMessageListActions() {
		// TODO: implement sending/updating/deleting messages using messageService
	}

	private void initializeMessageListListeners() {
		// place for additional listeners (e.g., double-click to open conversation details)
	}

	private final void setUpDebugEnvironment() {
    	//view.addDebugLabel();
    }
}