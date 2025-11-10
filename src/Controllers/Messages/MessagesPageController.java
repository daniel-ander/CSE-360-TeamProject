// language: java
package Controllers.Messages;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Controllers.UserLoginPageController;
import Controllers.WelcomeLoginPageController;
import Controllers.ForumPage.ForumHeaderViewModel;
import Controllers.ForumPage.ForumPageController;
import Controllers.ForumPage.PostListViewModel;
import Controllers.ForumPage.QuestionPaneViewModel;
import Database.*;
import Database.Services.Interfaces.IMessageService;

import Database.Services.Interfaces.IUserService;
import Database.Services.Interfaces.IQuestionService;
import Exceptions.DBExceptions.DataAccessException;
import Models.Message;
import Sessions.UserSession;
import UI.Pages.ForumPage;
import UI.Pages.MessagesPage;

import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;

import Utils.Debug;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.stage.Stage;

/**
 * MessagesPageController - populate MessagesPage from DB and hook cells/models.
 */
public class MessagesPageController {

	private final DBConnectionStore dBConnectionStore;
	private final UserSession userSession;
	private final IQuestionService questionService;
	private final IMessageService messageService;
	private final Stage primaryStage;
	private final MessagesPage view;
	private final PostListViewModel messageListViewModel;
	private final IUserService userService;

    /**
     * Creates a new MessagesPageController.
     * Initializes layout, sets up actions, and configures search functionality.
     */
	public MessagesPageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, MessagesPage view, 
			PostListViewModel postListViewModel, ForumHeaderViewModel messagesHeaderViewModel, QuestionPaneViewModel questionPaneViewModel) {
		this.dBConnectionStore = dBConnectionStore;
		this.questionService = dBConnectionStore.getQuestionService();
		this.primaryStage = primaryStage;
		this.messageService = dBConnectionStore.getMessageService();
		this.userService = dBConnectionStore.getUserService();
		this.userSession = userSession;
		this.view = view;
		this.messageListViewModel = postListViewModel;

	    setInitialLayout();
	    setEnterForumButtonAction();
	    setLogoutButtonAction();
	    setChangeRolesButtonAction();
	    setSendMessageButtonAction();
	    
		messagesHeaderViewModel.getSearchButtonPressedProperty().addListener((_, _, isTriggered) -> {
			if (isTriggered) {
				messageListViewModel.filterVisibleQuestionsByText(messagesHeaderViewModel.getSearchFieldText());
			}
		});	    
	    if(Debug.isEnabled) {
        	setUpDebugEnvironment();
        }
	}

	   /**
     * Configures the logout button action.
     * Navigates back to the login page.
     */
	private void setLogoutButtonAction() {
		view.getLogoutButton().setOnAction(_ -> {
	        new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
		});
	}

    /**
     * Configures the change roles button action.
     * Navigates back to the welcome page.
     */
	private void setChangeRolesButtonAction() {
		view.getChangeRolesButton().setOnAction(_ -> {
	    	new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());
		});
	}

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
					forumHeaderViewModel,
					questionPaneViewModel
			);
		});
	}

    /**
     * Configures the send message button action.
     * Validates input, creates a new message, inserts it into the database,
     * and reloads the current thread.
     */
	private void setSendMessageButtonAction() {
	    view.getSendMessageButton().setOnAction(_ -> {
	        String text = view.getMessageTextArea().getText().trim();
	        if (text.isEmpty()) return;

	        ObservableList<MessagesViewModel> items = view.getMessagesListView().getItems();
	        if (items == null || items.isEmpty()) return;

	        MessagesViewModel last = items.get(items.size() - 1);
	        Integer reviewId = last.getReviewId();
	        Integer parentMessageId = last.getMessageId();
	        if (parentMessageId == null || parentMessageId == 0) parentMessageId = null;

	        int senderId = userSession.getCurrentUserId();
	        int receiverId = (last.getSenderId() == senderId)
	                ? last.getReceiverId()
	                : last.getSenderId();

	        try {
	            Message toInsert = new Message(
	                0,
	                reviewId,
	                senderId,
	                receiverId,
	                parentMessageId,
	                text,
	                null
	            );

	            messageService.insertMessage(toInsert);

	            view.getMessageTextArea().clear();
	            reloadMessagesForCurrentThread(reviewId);

	        } catch (DataAccessException e) {
	            e.printStackTrace();
	        }
	    });
	}

    /**
     * Reloads messages for the current thread.
     * Updates the list view with refreshed messages.
     */
	private void reloadMessagesForCurrentThread(int reviewId) {
	    try {
	        List<Message> refreshed = messageService.getMessagesByReviewId(reviewId);
	        ObservableList<MessagesViewModel> models = FXCollections.observableArrayList();
	        for (Message m : refreshed) {
	            models.add(new MessagesViewModel(m));
	        }
	        view.getMessagesListView().setItems(models);
	        view.getMessagesListView().scrollTo(models.size() - 1);
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	    }
	}

    /**
     * Sets up the initial layout of the messages page.
     * Loads messages, organizes recent interactions, and wires conversation filters.
     */
	private void setInitialLayout() {
	    view.setInitialLayout(primaryStage);

	    initializeMessageListListeners();
	    setMessageListActions();

	    // Only load data if lists are empty — prevents full refresh on every reopen
	    if (!view.getMessagesListView().getItems().isEmpty()
	            && !view.getRecentInteractionsListView().getItems().isEmpty()) {
	        return;
	    }

	    try {
	        List<Message> messages = messageService.getMessagesForUsername(userSession.getCurrentUserName());
	        ObservableList<MessagesViewModel> messagesVMList = FXCollections.observableArrayList();
	        for (Message m : messages) {
	            messagesVMList.add(new MessagesViewModel(m));
	        }

	        // Group by other user, keep most recent message per conversation
	        Map<Integer, Message> lastByOther = new HashMap<>();
	        for (Message m : messages) {
	            Integer current = userSession.getCurrentUserId();
	            Integer sender = m.getSenderId();
	            Integer receiver = m.getReceiverId();
	            if (sender == null || receiver == null) continue;

	            Integer other = current.equals(sender) ? receiver : sender;
	            Message existing = lastByOther.get(other);
	            if (existing == null
	                    || (m.getSentTimestamp() != null && existing.getSentTimestamp() != null
	                        && m.getSentTimestamp().after(existing.getSentTimestamp()))) {
	                lastByOther.put(other, m);
	            }
	        }

	        ObservableList<RecentInteractionsViewModel> recentList = FXCollections.observableArrayList();
	        for (Map.Entry<Integer, Message> entry : lastByOther.entrySet()) {
	            Integer otherId = entry.getKey();
	            Message lastMsg = entry.getValue();
	            String username = String.valueOf(otherId);
	            String preview = lastMsg.getContent() == null ? "" : lastMsg.getContent();
	            String ts = lastMsg.getSentTimestamp() == null ? "" : lastMsg.getSentTimestamp().toString();
	            recentList.add(new RecentInteractionsViewModel(otherId, username, preview, ts, 0));
	        }

	        // Set items only once
	        view.getRecentInteractionsListView().setItems(recentList);
	        view.getMessagesListView().setItems(messagesVMList);

	        // Conversation filter when selecting a user
	     // Conversation filter when selecting a user
	        view.getRecentInteractionsListView().getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
	            if (newV == null) return;
	            int otherId = newV.getUserId();

	            try {
	                // always pull latest from DB to stay current
	                List<Message> refreshed = messageService.getMessagesForUsername(userSession.getCurrentUserName());
	                ObservableList<MessagesViewModel> convo = FXCollections.observableArrayList();

	                for (Message m : refreshed) {
	                    if ((m.getSenderId() != null && m.getSenderId().equals(otherId)) ||
	                        (m.getReceiverId() != null && m.getReceiverId().equals(otherId))) {
	                        convo.add(new MessagesViewModel(m));
	                    }
	                }

	                // keep same list reference, update contents
	                view.getMessagesListView().getItems().setAll(convo);
	                if (!convo.isEmpty()) {
	                    view.getMessagesListView().scrollTo(convo.size() - 1);
	                }
	            } catch (DataAccessException e) {
	                e.printStackTrace();
	            }
	        });


	    } catch (DataAccessException ex) {
	        ex.printStackTrace();
	    }
	}

    
	/**
	 *  TODO: implement sending/updating/deleting messages using messageService
	 */
    private void setMessageListActions() {
	}

    /**
     *  place for additional listeners (e.g., double-click to open conversation details)
     */
	private void initializeMessageListListeners() {
	}
	
	private final void setUpDebugEnvironment() {
    	//view.addDebugLabel();
    }
}