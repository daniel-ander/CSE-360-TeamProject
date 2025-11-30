//StaffHomePageController.java
package Controllers;

import Controllers.ForumPage.ForumHeaderViewModel;
import Controllers.ForumPage.ForumPageController;
import Controllers.ForumPage.PostListViewModel;
import Controllers.ForumPage.QuestionPaneViewModel;
import Controllers.Messages.MessagesPageController;
import Controllers.StaffPage.StaffViewModel;
import Database.DBConnectionStore;
import Database.Services.Interfaces.IQuestionService;
import Database.Services.Interfaces.IUserService;
import Models.Answer;
import Models.Message;
import Models.Question;
import Models.User;
import Sessions.UserSession;
import UI.Pages.ForumPage;
import UI.Pages.MessagesPage;
import UI.Pages.StaffHomePage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;
import Utils.Debug;
import javafx.stage.Stage;

/**
 * The StaffHomePageController class manages the staff's home page.
 * It handles navigation actions such as logging out, changing roles,
 * entering the forum, and accessing the messages page.
 */
public class StaffHomePageController {
	
	/**
	 *  Reference to the DatabaseHelper for database interactions
	 */
	private final DBConnectionStore dBConnectionStore;
	private final UserSession userSession;
	private final IQuestionService questionService;
	private final IUserService userService;
	private final Stage primaryStage;
	private final StaffHomePage view;
	
	private final PostListViewModel postListViewModel;
	private final ForumHeaderViewModel staffHeaderViewModel;
	private final QuestionPaneViewModel questionPaneViewModel;
	private final StaffViewModel viewModel;
	
	/**
	 * Creates a new StaffHomePageController.
	 * Initializes the layout, sets up button actions, and configures search functionality.
	 * 
     * Creates a new StaffHomePageController.
     *
     * @param dBConnectionStore    the database connection store used for data access
     * @param userSession          the current user's session information
     * @param primaryStage         the primary JavaFX stage for showing pages
     * @param view                 the StaffHomePage view associated with this controller
     * @param postListViewModel    the view model that manages posts shown on the staff home page
     * @param staffHeaderViewModel the view model that manages the header on the staff page
     * @param questionPaneViewModel the view model that manages the question detail pane
     */
	public StaffHomePageController(
			DBConnectionStore dBConnectionStore,
			Stage primaryStage,
			UserSession userSession,
			StaffHomePage view,
			PostListViewModel postListViewModel,
			ForumHeaderViewModel staffHeaderViewModel,
			QuestionPaneViewModel questionPaneViewModel) {
		
		this.dBConnectionStore = dBConnectionStore;
		this.questionService = dBConnectionStore.getQuestionService();
		this.userService = dBConnectionStore.getUserService();
		this.primaryStage = primaryStage;
		this.userSession = userSession;
		this.view = view;
		this.postListViewModel = postListViewModel;
		this.staffHeaderViewModel = staffHeaderViewModel;
		this.questionPaneViewModel = questionPaneViewModel;
		
		this.viewModel = new StaffViewModel(
				dBConnectionStore.getUserService(),
				dBConnectionStore.getQuestionService(),
				dBConnectionStore.getAnswerService(),
				dBConnectionStore.getMessageService(), 
				dBConnectionStore.getAdminRequestService()
		);
		
		setInitialLayout();
		bindUI();
		bindEvents();
		setEnterForumButtonAction();
		setLogoutButtonAction();
		setChangeRolesButtonAction();
		setEnterMessagesPageButtonAction();
		
		staffHeaderViewModel.getSearchButtonPressedProperty().addListener((_, _, isTriggered) -> {
			if (isTriggered) {
				postListViewModel.filterVisibleQuestionsByText(staffHeaderViewModel.getSearchFieldText());
			}
		});
		
		if (Debug.isEnabled) {
			setUpDebugEnvironment();
		}
	}
	
	// ===================
	// SETTERS
	// ===================
	
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
					forum_header_view_model_safe(forumHeaderViewModel),
					questionPaneViewModel
			);
		});
	}
	
	/**
	 * Configures the enter messages page button action.
	 * Navigates to the messages page.
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
	
	// ===============================
	// BIND UI LISTS TO VIEWMODEL DATA
	// ===============================
	private void bindUI() {
		view.userListView.setItems(viewModel.getUsers());
		view.questionsListView.setItems(viewModel.getQuestions());
		view.answersListView.setItems(viewModel.getAnswers());
		view.messagesListView.setItems(viewModel.getMessages());
		view.notesListView.setItems(viewModel.getStaffNotes());
	}
	
	// ===============================
	// EVENT HANDLERS
	// ===============================
	private void bindEvents() {
		
		// Selecting a user
		view.userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				viewModel.setSelectedUser(newVal);
				view.selectedUserLabel.setText("Selected: " + newVal);
			}
		});
		
		// Selecting a question
		view.questionsListView.setOnMouseClicked(e -> {
			Question q = view.questionsListView.getSelectionModel().getSelectedItem();
			if (q != null) {
				viewModel.setCurrentlyDisplayedContent(q);
				view.contentViewerTextArea.setText(formatQuestion(q));
			}
		});
		
		// Selecting an answer
		view.answersListView.setOnMouseClicked(e -> {
			Answer a = view.answersListView.getSelectionModel().getSelectedItem();
			if (a != null) {
				viewModel.setCurrentlyDisplayedContent(a);
				view.contentViewerTextArea.setText(formatAnswer(a));
			}
		});
		
		// Selecting a message
		view.messagesListView.setOnMouseClicked(e -> {
			Message m = view.messagesListView.getSelectionModel().getSelectedItem();
			if (m != null) {
				viewModel.setCurrentlyDisplayedContent(m);
				view.contentViewerTextArea.setText(formatMessage(m));
			}
		});
		
		// Add staff note
		view.addNoteButton.setOnAction(_ -> {
			String text = view.noteInputField.getText().trim();
			if (text.isEmpty()) return;
			viewModel.addStaffNote(text);
			view.noteInputField.clear();
		});
		
		// Flag content
		view.flagButton.setOnAction(_ -> {
			viewModel.flagCurrentContent();
		});
	}
	
	// ===============================
	// FORMATTING HELPERS
	// ===============================
	
	/**
	 * Formats a question for display.
	 */
	private String formatQuestion(Question q) {
		return "Question by " + userService.getUserNameById(q.getAuthor().getId()) + "\n"
				+ "Posted: " + q.getCreatedTimestamp() + "\n\n"
				+ q.getBodyText();
	}
	/**
	 * Formats an answer for display.
	 */
	private String formatAnswer(Answer a) {
		return "Answer by " + a.getAuthor().getUserName() + "\n"
				+ "Posted: " + a.getCreatedTimestamp() + "\n\n"
				+ a.getBodyText();
	}
	/**
	 * Formats a message for display.
	 */
	private String formatMessage(Message m) {
		return "Message\n"
				+ "From: " + m.getSenderUsername() + "\n"
				+ "To: " + m.getReceiverUsername() + "\n"
				+ "Sent: " + m.getSentTimestamp() + "\n\n"
				+ m.getContent();
	}
	
	/**
	 * Sets up the initial layout of the staff home page.
	 */
	private void setInitialLayout() {
		view.setInitialLayout(primaryStage);
	}
	
	private void setUpDebugEnvironment() {
		// view.addDebugLabel();
	}

	// small helper to avoid accidental refactor break in posted snippet
	private ForumHeaderViewModel forum_header_view_model_safe(ForumHeaderViewModel vm) { return vm; }
}
