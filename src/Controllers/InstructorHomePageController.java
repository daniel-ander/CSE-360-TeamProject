package Controllers;

import Models.User;
import Controllers.ForumPage.ForumHeaderViewModel;
import Controllers.ForumPage.ForumPageController;
import Controllers.ForumPage.PostListViewModel;
import Controllers.ForumPage.QuestionPaneViewModel;
import Controllers.Messages.MessagesPageController;
import Database.*;
import Database.Services.Interfaces.IUserService;
import Database.Services.Interfaces.IAdminRequestService;
import Controllers.InstructorPage.AdminRequestsViewModel;
import Models.AdminRequest;

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
import javafx.collections.ObservableList;

/**
 * The InstructorHomePageController class manages the instructor's home page.
 */
public class InstructorHomePageController {
	
	private final DBConnectionStore dBConnectionStore;
	private final IUserService userService;
	private final Stage primaryStage;
	private final InstructorHomePage view;
	private final UserSession userSession;

	private final AdminRequestsViewModel adminRequestsViewModel;
	
	/**
	 * Creates a new InstructorHomePage Controller.
	 * Initializes layout, sets up button actions, and configures debug environment if needed
	 * @param dBConnectionStore
	 * @param primaryStage
	 * @param userSession
	 * @param view
	 */
	public InstructorHomePageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, InstructorHomePage view) {
		this.dBConnectionStore = dBConnectionStore;
        this.userService = dBConnectionStore.getUserService();
        this.view = view;
        this.primaryStage = primaryStage;
        this.userSession = userSession;
        
        // create admin requests VM using store's service
        IAdminRequestService reqService = null;
        try {
            reqService = dBConnectionStore.getAdminRequestService();
        } catch (Throwable ignore) { }
        this.adminRequestsViewModel = new AdminRequestsViewModel(reqService);
        
        setInitialLayout();
        
        setLogoutButtonAction();
        setEnterForumButtonAction();
        setEnterMessagesPageButtonAction();
        setChangeRolesButtonAction();
        setManageRequestsButtonAction(); // still available if needed
        
        // bind the Requests tab UI to the viewmodel
        bindRequestsTab();
		
		if (Debug.isEnabled) {
			// Additional debug setup can be added here
		}
	}
	
	/**
	 * Binds admin requests tab to page
	 */
	private void bindRequestsTab() {
		// wire lists to VM observable lists
		view.getOpenRequestsListView().setItems(adminRequestsViewModel.getOpenRequests());
		view.getClosedRequestsListView().setItems(adminRequestsViewModel.getClosedRequests());
		
		// bind text inputs bidirectionally
		view.getNewRequestField().textProperty().bindBidirectional(adminRequestsViewModel.newRequestTextProperty());
		view.getReopenDescriptionArea().textProperty().bindBidirectional(adminRequestsViewModel.reopenDescriptionProperty());
		
		// selection -> viewmodel properties
		view.getOpenRequestsListView().getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
			adminRequestsViewModel.selectedOpenProperty().set(newV);
		});
		view.getClosedRequestsListView().getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
			adminRequestsViewModel.selectedClosedProperty().set(newV);
		});
		
		// buttons invoke VM actions and refresh
		view.getCreateRequestButton().setOnAction(_e -> {
			adminRequestsViewModel.createRequest();
			adminRequestsViewModel.refresh();
		});
		view.getReopenButton().setOnAction(_e -> {
			adminRequestsViewModel.reopenSelected();
			adminRequestsViewModel.refresh();
		});
		
		// initial load
		adminRequestsViewModel.refresh();
	}
	
	// ===================
	// 		 SETTERS 
	// ===================
	
	/**
	 * Configures Enter forum button
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
	 * Configures enter messages page button
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
    			User.Role role = userService.getUserRole(userSession.getCurrentUserName());
    		} catch (DataAccessException e) {
	            System.err.println(e.getMessage());
	            e.printStackTrace();
	        }
	    	new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());
	    });
	}

    /**
     * Configures manage requests button
     */
    private void setManageRequestsButtonAction() {
        view.getManageRequestsButton().setOnAction(_ -> {
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

	private ForumHeaderViewModel forum_header_view_model_safe(ForumHeaderViewModel vm) { return vm; }
}
