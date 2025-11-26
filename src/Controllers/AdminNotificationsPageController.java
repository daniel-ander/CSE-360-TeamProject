package Controllers;

import Models.User;
import Database.*;
import Database.Services.Interfaces.IUserService;
import Database.Services.Interfaces.IAdminRequestService;
import Controllers.InstructorPage.AdminRequestsViewModel;

import Exceptions.DBExceptions.DataAccessException;

import Sessions.UserSession;
import UI.Pages.AdminNotificationsPage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;
import Utils.Debug;

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
	
	public AdminNotificationsPageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, AdminNotificationsPage view) {
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
        setChangeRolesButtonAction();
        setBackButtonAction();
        
        // bind the Requests tab UI to the viewmodel
        bindRequestsTab();
		
		if (Debug.isEnabled) {
			// Additional debug setup can be added here
		}
	}
	
	private void bindRequestsTab() {
		// wire lists to VM observable lists
		view.getOpenRequestsListView().setItems(adminRequestsViewModel.getOpenRequests());
		view.getClosedRequestsListView().setItems(adminRequestsViewModel.getClosedRequests());
		
		// bind text inputs bidirectionally
		view.getReopenDescriptionArea().textProperty().bindBidirectional(adminRequestsViewModel.reopenDescriptionProperty());
		
		// selection -> viewmodel properties
		view.getOpenRequestsListView().getSelectionModel().selectedItemProperty().addListener((_, _, newV) -> {
			adminRequestsViewModel.selectedOpenProperty().set(newV);
		});
		view.getClosedRequestsListView().getSelectionModel().selectedItemProperty().addListener((_, _, newV) -> {
			adminRequestsViewModel.selectedClosedProperty().set(newV);
		});
		
		// buttons invoke VM actions and refresh
		view.getCloseButton().setOnAction(_ -> {
			adminRequestsViewModel.closeSelected();
		});
		view.getReopenButton().setOnAction(_ -> {
			adminRequestsViewModel.reopenSelected();
		});
		
		// initial load
		adminRequestsViewModel.refresh();
	}
	
	// ===================
	// 		 SETTERS 
	// ===================
	
    private void setLogoutButtonAction() {
    	view.getLogoutButton().setOnAction(_ -> {
	         new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
	    });
	}
    
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
    
    private void setBackButtonAction() {
    	view.getBackButton().setOnAction(_ -> {
    		new AdminHomePageController(dBConnectionStore, primaryStage, userSession, new UI.Pages.AdminHomePage());
	    });
    }
    
    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    }
}
