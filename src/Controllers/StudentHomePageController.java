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
 * The StudentHomePageController class manages the student home page.
 * It handles navigation actions such as logging out, changing roles,
 * entering the forum, and submitting reviewer requests.
 */
public class StudentHomePageController {

	/**
	 *  Reference to the DatabaseHelper for database interactions
	 */
	private final DBConnectionStore dBConnectionStore;

	private final Stage primaryStage;
	private final StudentHomePage view;
	private final UserSession userSession;

    /**
     * Creates a new StudentHomePageController.
     * Initializes the layout, sets up button actions, and configures debug environment if enabled.
     */
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
		setRequestReviewerButtonAction();

		if (Debug.isEnabled) {
			setUpDebugEnvironment();
		}
	}

	// ===================
	// SETTERS
	// ===================

    /**
     * Configures the logout button action.
     * Logs the user out, updates session status, and navigates back to the login page.
     */
	private void setLogoutButtonAction() {
		view.getLogoutButton().setOnAction(_ -> {
			// Return to login page
			userSession.setLoggedStatus(false);
			new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
		});
	}

    /**
     * Configures the change roles button action.
     * Navigates back to the welcome page. May require a database update for roles.
     * TODO: currently not implemented
     */
	private void setChangeRolesButtonAction() {
		view.getChangeRolesButton().setOnAction(_ -> {
			// TODO: -- Something weird happening here. Might need to make a DB call to
			// update roles
			// Return to welcome page

			new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());

		});
	}

    /**
     * Configures the enter forum button action.
     * Navigates to the forum page and initializes its controllers and view models.
     */
	private void setEnterForumButtonAction() {
		view.getEnterForumButton().setOnAction(_ -> {
			// Enter the forum page
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
	
	/**
	 *  request reviewer button action
	 */
    private void setRequestReviewerButtonAction() {
        view.getRequestReviewerButton().setOnAction(_ -> {
            String currentUserName = userSession.getCurrentUserName();
            try { 
                boolean requestSubmitted = submitReviewerRequest(currentUserName);
                // show success/error message based on the result
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

	/**
	 *  submit reviewer request
	 * @param studentUsername
	 * @return
	 */
	private boolean submitReviewerRequest(String studentUsername) { 
		// interact w/ RequestService to submit the request
		RequestService requestService = new RequestService(dBConnectionStore);
		return requestService.submitReviewerRequest(studentUsername);
	}

    /**
     * Sets up the initial layout of the student home page.
     */
	private void setInitialLayout() {
		view.setInitialLayout(primaryStage);
		view.setUserLabel(userSession.getCurrentUserName());
	}

	private final void setUpDebugEnvironment() {
		view.addDebugLabel();
	}
}