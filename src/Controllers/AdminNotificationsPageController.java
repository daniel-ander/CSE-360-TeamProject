package Controllers;

import Models.Answer;
import Models.Message;
import Models.Question;
import Models.User;
import Database.*;
import Database.Services.Interfaces.IUserService;
import Database.Services.Interfaces.IAdminRequestService;
import Controllers.AdminPage.AdminReviewViewModel;
import Controllers.InstructorPage.AdminRequestsViewModel;
import Controllers.InstructorPage.InstructorReviewViewModel;
import Exceptions.DBExceptions.DataAccessException;

import Sessions.UserSession;
import UI.Pages.AdminNotificationsPage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;
import Utils.Debug;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * The AdminNotificationsPageController class manages the admin's notifications page.
 */
public class AdminNotificationsPageController {
	
	private final DBConnectionStore dBConnectionStore;
	private final IUserService userService;
	private final Stage primaryStage;
	private final AdminNotificationsPage view;
	private final UserSession userSession;

	private final AdminRequestsViewModel adminRequestsViewModel;
	private final AdminReviewViewModel adminReviewViewModel;
	
	/**
	 * AdminNotificationsPageController Constructor
	 * @param dBConnectionStore
	 * @param primaryStage
	 * @param userSession
	 * @param view
	 */
	public AdminNotificationsPageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, AdminNotificationsPage view) {
		this.dBConnectionStore = dBConnectionStore;
        this.userService = dBConnectionStore.getUserService();
        this.view = view;
        this.primaryStage = primaryStage;
        this.userSession = userSession;
        
        IAdminRequestService reqService = null;
        try {
            reqService = dBConnectionStore.getAdminRequestService();
        } catch (Throwable ignore) { }
        this.adminRequestsViewModel = new AdminRequestsViewModel(reqService);
		this.adminReviewViewModel = new AdminReviewViewModel(
				dBConnectionStore.getUserService(),
				dBConnectionStore.getQuestionService(),
				dBConnectionStore.getAnswerService(),
				dBConnectionStore.getMessageService()
		);
        
        setInitialLayout();
        
        setLogoutButtonAction();
        setChangeRolesButtonAction();
        setBackButtonAction();
        
        bindRequestsTab();
        bindAdminReviewTab();
		
		if (Debug.isEnabled) {
			// Additional debug setup can be added here
		}
	}
	
	/**
	 * Binds the "Admin Requests" tab to the view model
	 */
	private void bindRequestsTab() {
		view.getOpenRequestsListView().setItems(adminRequestsViewModel.getOpenRequests());
		view.getClosedRequestsListView().setItems(adminRequestsViewModel.getClosedRequests());
		
		view.getReopenDescriptionArea().textProperty().bindBidirectional(adminRequestsViewModel.reopenDescriptionProperty());
		
		view.getOpenRequestsListView().getSelectionModel().selectedItemProperty().addListener((_, _, newV) -> {
			adminRequestsViewModel.selectedOpenProperty().set(newV);
		});
		view.getClosedRequestsListView().getSelectionModel().selectedItemProperty().addListener((_, _, newV) -> {
			adminRequestsViewModel.selectedClosedProperty().set(newV);
		});
		
		view.getCloseButton().setOnAction(_ -> {
			adminRequestsViewModel.closeSelected();
		});
		view.getReopenButton().setOnAction(_ -> {
			boolean validDescription = adminRequestsViewModel.reopenSelected();
			
			if (!validDescription) {
				Alert alert = new Alert(Alert.AlertType.WARNING);
		        alert.setTitle("Missing Description");
		        alert.setHeaderText(null);
		        alert.setContentText("Please fill description box before reopening a request.");
		        alert.showAndWait();
		        return;
			}
			
			adminRequestsViewModel.refresh();
		});
		
		
		adminRequestsViewModel.refresh();
	}
	
	// ===================
	// 		 SETTERS 
	// ===================
	
	/**
	 * Configures logout button
	 */
    private void setLogoutButtonAction() {
    	view.getLogoutButton().setOnAction(_ -> {
	         new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
	    });
	}
    
    /**
     * Configures change roles button
     */
    private void setChangeRolesButtonAction() {
    	view.getChangeRolesButton().setOnAction(_ -> {
    		try {
    			User.Role _ = userService.getUserRole(userSession.getCurrentUserName());
    		} catch (DataAccessException e) {
	            System.err.println(e.getMessage());
	            e.printStackTrace();
	        }
	    	new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());
	    });
    }
    
	private void bindAdminReviewTab() {
	    // bind lists to viewmodel observable lists
	    view.getUserListView().setItems(adminReviewViewModel.getUsers());
	    view.getQuestionsListView().setItems(adminReviewViewModel.getQuestions());
	    view.getAnswersListView().setItems(adminReviewViewModel.getAnswers());
	    view.getMessagesListView().setItems(adminReviewViewModel.getMessages());

	    // Selecting a user -> update view model
	    view.getUserListView().getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
	        if (newVal != null) {
	            adminReviewViewModel.setSelectedUser(newVal);
	            // no selected-user label on InstructorHomePage, so we don't update UI label here
	        } else {
	            adminReviewViewModel.setSelectedUser(null);
	            view.getContentViewerTextArea().clear();
	        }
	    });

	    // Selecting a question -> display formatted question in the content viewer
	    view.getQuestionsListView().setOnMouseClicked(e -> {
	        Question q = view.getQuestionsListView().getSelectionModel().getSelectedItem();
	        if (q != null) {
	            adminReviewViewModel.setCurrentlyDisplayedContent(q);
	            view.getContentViewerTextArea().setText(formatQuestion(q));
	        }
	    });

	    // Selecting an answer -> display formatted answer in the content viewer
	    view.getAnswersListView().setOnMouseClicked(e -> {
	        Answer a = view.getAnswersListView().getSelectionModel().getSelectedItem();
	        if (a != null) {
	            adminReviewViewModel.setCurrentlyDisplayedContent(a);
	            view.getContentViewerTextArea().setText(formatAnswer(a));
	        }
	    });

	    // Selecting a message -> display formatted message in the content viewer
	    view.getMessagesListView().setOnMouseClicked(e -> {
	        Message m = view.getMessagesListView().getSelectionModel().getSelectedItem();
	        if (m != null) {
	            adminReviewViewModel.setCurrentlyDisplayedContent(m);
	            view.getContentViewerTextArea().setText(formatMessage(m));
	        }
	    });

	    // initial load from viewmodel
	    // (viewmodel constructor already calls loadAllUsers; ensure lists are up-to-date)
	    // No extra UI elements like notes/flag buttons assumed on InstructorHomePage.
	}
	
	private String formatQuestion(Question q) {
	    try {
	        String author = userService.getUserNameById(q.getAuthor().getId());
	        if (author == null || author.trim().isEmpty()) author = "(unknown)";
	        return "Question by " + author + "\n"
	                + "Posted: " + q.getCreatedTimestamp() + "\n\n"
	                + safe(q.getBodyText());
	    } catch (Exception ignore) {
	        return safe(q.getBodyText());
	    }
	}

	private String formatAnswer(Answer a) {
	    try {
	        String author = a.getAuthor().getUserName();
	        try { String resolved = userService.getUserNameById(a.getAuthor().getId()); if (resolved != null && !resolved.trim().isEmpty()) author = resolved; } catch (Exception ignored) {}
	        if (author == null || author.trim().isEmpty()) author = "(unknown)";
	        return "Answer by " + author + "\n"
	                + "Posted: " + a.getCreatedTimestamp() + "\n\n"
	                + safe(a.getBodyText());
	    } catch (Exception ignore) {
	        return safe(a.getBodyText());
	    }
	}

	private String formatMessage(Message m) {
	    try {
	        return "Message\n"
	                + "From: " + safe(m.getSenderUsername()) + "\n"
	                + "To: " + safe(m.getReceiverUsername()) + "\n"
	                + "Sent: " + m.getSentTimestamp() + "\n\n"
	                + safe(m.getContent());
	    } catch (Exception ignore) {
	        return safe(m == null ? null : m.getContent());
	    }
	}
	

	private static String safe(String s) {
	    return s == null ? "" : s;
	}
    
    /**
     * Configures back button
     */
    private void setBackButtonAction() {
    	view.getBackButton().setOnAction(_ -> {
    		new AdminHomePageController(dBConnectionStore, primaryStage, userSession, new UI.Pages.AdminHomePage());
	    });
    }
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
}
