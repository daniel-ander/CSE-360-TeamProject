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
        
        setInitialLayout();
        
        setLogoutButtonAction();
        setChangeRolesButtonAction();
        setBackButtonAction();
        
        bindRequestsTab();
		
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
			adminRequestsViewModel.reopenSelected();
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
