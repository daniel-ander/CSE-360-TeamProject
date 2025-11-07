package Controllers;

import Controllers.ForumPage.ForumPageController;
import Controllers.ForumPage.QuestionPaneViewModel;
import Controllers.ForumPage.ForumHeaderViewModel;
import Controllers.ForumPage.PostListViewModel;
import Database.*;
import Database.Services.RequestService;
import Sessions.UserSession;

import UI.Pages.ForumPage;
import UI.Pages.StudentHomePage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;

import Utils.Debug;

import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user. It prompts
 * the user to set up administrator access and navigate to the setup screen.
 */
public class StudentHomePageController {

	// Reference to the DatabaseHelper for database interactions
	private final DBConnectionStore dBConnectionStore;

	private final Stage primaryStage;
	private final StudentHomePage view;
	private final UserSession userSession;

	public StudentHomePageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession,
			StudentHomePage view) {
		this.dBConnectionStore = dBConnectionStore;
		this.view = view;
		this.primaryStage = primaryStage;
		this.userSession = userSession;

		setInitialLayout();

		setLogoutButtonAction();
		setChangeRolesButtonAction();
		setEnterForumButtonAction();
		setRequestReviewerButtonAction();  // Set up the action for the new button

		if (Debug.isEnabled) {
			setUpDebugEnvironment();
		}
	}

	// ===================
	// SETTERS
	// ===================

	private void setLogoutButtonAction() {
		view.getLogoutButton().setOnAction(_ -> {
			// Return to login page
			userSession.setLoggedStatus(false);
			new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
		});
	}

	private void setChangeRolesButtonAction() {
		view.getChangeRolesButton().setOnAction(_ -> {
			// TODO: -- Something weird happening here. Might need to make a DB call to
			// update roles
			// Return to welcome page

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
	
	 // request reviewer button action
    private void setRequestReviewerButtonAction() {
        view.getRequestReviewerButton().setOnAction(_ -> {
            String currentUserName = userSession.getCurrentUserName();
            
            try { // Submit the reviewer request
                boolean requestSubmitted = submitReviewerRequest(currentUserName);
                
                if (requestSubmitted) {
                    view.showSuccessMessage("Request to become reviewer submitted successfully.");
                } else {
                    view.showErrorMessage("Failed to submit request. Please try again.");
                }
            } catch (Exception e) {
                view.showErrorMessage("Error submitting request: " + e.getMessage());
            }
        });
    }

    // submit reviewer request
    private boolean submitReviewerRequest(String studentUsername) { // Interact with RequestService to submit the request
        RequestService requestService = new RequestService(dBConnectionStore);
        return requestService.submitReviewerRequest(studentUsername);
    }

	private void setInitialLayout() {
		view.setInitialLayout(primaryStage);
	}

	private final void setUpDebugEnvironment() {
		view.addDebugLabel();
	}
}
