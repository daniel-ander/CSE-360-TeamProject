package Controllers;

import Models.User;

import java.util.List;
import java.util.Optional;

import Database.*;
import Database.Services.RequestService;
import Database.Services.Interfaces.IUserService;

import Exceptions.DBExceptions.DataAccessException;

import Sessions.UserSession;

import UI.Pages.InstructorHomePage;
import UI.Pages.InstructorRequestsPage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;

import Utils.Debug;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class InstructorHomePageController {
	
	// Reference to the DatabaseHelper for database interactions
	private final DBConnectionStore dBConnectionStore;
	private final IUserService userService;
	private final Stage primaryStage;
	private final InstructorHomePage view;
	private final UserSession userSession;
	
	public InstructorHomePageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession, InstructorHomePage view) {
		this.dBConnectionStore = dBConnectionStore;
        this.userService = dBConnectionStore.getUserService();
        this.view = view;
        this.primaryStage = primaryStage;
        this.userSession = userSession;
        
        setInitialLayout();
        
        setLogoutButtonAction();
        setChangeRolesButtonAction();
        setManageRequestsButtonAction();
        
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
	         new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
	    });
	}
    
    private void setChangeRolesButtonAction() {
    	view.getChangeRolesButton().setOnAction(_ -> {
			// instructor view + process pending reviewer requests
			RequestService requestService = new RequestService(dBConnectionStore);
			try { // fetch pending requests
				List<String> pending = requestService.getPendingRequests();
				if (pending == null || pending.isEmpty()) {
					Alert info = new Alert(Alert.AlertType.INFORMATION, "No pending reviewer requests.");
					info.showAndWait();
					return;
				} // select from pending
				ChoiceDialog<String> dialog = new ChoiceDialog<>(pending.get(0), pending);
				dialog.setTitle("Pending Reviewer Requests");
				dialog.setHeaderText("Select a student request to process");
				Optional<String> selected = dialog.showAndWait();
				if (selected.isPresent()) { // confirm granting reviewer role
					String student = selected.get(); // selected student username
					Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Grant reviewer role to '" + student + "'?", ButtonType.YES, ButtonType.NO);
					Optional<ButtonType> resp = confirm.showAndWait();
					if (resp.isPresent() && resp.get() == ButtonType.YES) {
						boolean didGrantRole = requestService.grantReviewerRole(student);
						Alert res = new Alert(didGrantRole ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, didGrantRole ? "Reviewer role granted." : "Failed to grant reviewer role.");
						res.showAndWait();
					}
				}
			} catch (Exception e) {
				Alert err = new Alert(Alert.AlertType.ERROR, "Error processing requests: " + e.getMessage());
				err.showAndWait();
			}
	    });
	}

    // manage requests button action
    private void setManageRequestsButtonAction() {
        view.getManageRequestsButton().setOnAction(_ -> {
            // instructor requests management page
            InstructorRequestsPage requestsPage = new InstructorRequestsPage();
            InstructorRequestsPageController controller = new InstructorRequestsPageController(dBConnectionStore, primaryStage, userSession, requestsPage);
            controller.show();
        });
    }

    private void setInitialLayout() {
    	view.setInitialLayout(primaryStage);
    	view.setUserLabel(userSession.getCurrentUserName());
    }
    
    private void setUpDebugEnvironment() {
    	view.addDebugLabel();
    }
}
