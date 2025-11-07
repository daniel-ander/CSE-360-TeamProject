package Controllers.ForumPage;

import java.util.ArrayList;
import java.util.List;

import Config.AppCache;
import Controllers.UserLoginPageController;
import Controllers.WelcomeLoginPageController;
import Database.*;
import Database.Services.Interfaces.IQuestionService;
import DisplayModels.PostWithPermissions;
import Models.Question;
import Sessions.UserSession;

import UI.Pages.ForumPage;
import UI.Pages.UserLoginPage;
import UI.Pages.WelcomeLoginPage;
import Utils.AnswerPreloadPriority;
import Utils.RolePermissions;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

/**
 * ForumPageController manages the Forum page layout and actions such as
 * embedding the forum list and handling logout/role changes.
 */
public class ForumPageController {

	private final DBConnectionStore dBConnectionStore;
	private final IQuestionService questionService;
	private final Stage primaryStage;
	private final ForumPage view;
	private final UserSession userSession;
	private final PostListViewModel postListViewModel;
	private final ForumHeaderViewModel forumHeaderViewModel;
	private final QuestionPaneViewModel questionPaneViewModel;

	// Forum list components

	public ForumPageController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession,
			ForumPage view, PostListViewModel postListViewModel, ForumHeaderViewModel forumHeaderViewModel,
			QuestionPaneViewModel questionPaneViewModel) {
		this.dBConnectionStore = dBConnectionStore;
		this.questionService = dBConnectionStore.getQuestionService();
		this.primaryStage = primaryStage;
		this.userSession = userSession;
		this.view = view;
		this.postListViewModel = postListViewModel;
		this.forumHeaderViewModel = forumHeaderViewModel;
		this.questionPaneViewModel = questionPaneViewModel;

		setInitialLayout();
		setLogoutButtonAction();
		setChangeRolesButtonAction();

		forumHeaderViewModel.getSearchButtonPressedProperty().addListener((_, _, isPressed) -> {
			if (isPressed) {
				postListViewModel.filterVisibleQuestionsByText(forumHeaderViewModel.getSearchFieldText());
			}
		});
		
		forumHeaderViewModel.getSeeReviewsButtonPressedProperty().addListener((_, _, isPressed) -> {
			if(isPressed) {
				
			}
		});

		questionPaneViewModel.getTextInputSubmittedProperty().addListener((_, _, didSubmit) -> {
			String titleText = questionPaneViewModel.getTitleText();
			String bodyText = questionPaneViewModel.getBodyText();

			if (didSubmit) {
				try {
					Question question = new Question(userSession.getCurrentUserId(), titleText, bodyText);
					questionService.addQuestion(question);

					List<String> permissions = new ArrayList<>(List.of(
							RolePermissions.QuestionPermissions.COMMENT.name(),
							RolePermissions.QuestionPermissions.VIEW.name(),
							RolePermissions.QuestionPermissions.DELETE.name(),
							RolePermissions.QuestionPermissions.EDIT.name()));
					
					PostWithPermissions questionWithPermissions = new PostWithPermissions(question, permissions);
					
					postListViewModel.addQuestionToVisibleList(questionWithPermissions, true);
				} catch (Exception e) {
					System.out.println(e.getLocalizedMessage());
				}

			}
		});
	}

	// ===================
	// SETTERS
	// ===================

	private void setLogoutButtonAction() {
		view.getLogoutButton().setOnAction(_ -> {
			// Return to login page
			new UserLoginPageController(dBConnectionStore, primaryStage, new UserLoginPage());
		});
	}

	private void setChangeRolesButtonAction() {
		view.getChangeRolesButton().setOnAction(_ -> {

			// TODO: - Deal with role setting and persistence in regard to it
//            String role = userRepository.getUserRole(userSession);
//            User user = new User(userSession, "", role); // password blank if not needed

			new WelcomeLoginPageController(dBConnectionStore, primaryStage, userSession, new WelcomeLoginPage());
		});
	}

	private void setInitialLayout() {
		view.setInitialLayout(primaryStage);

		initializeQuestionListListeners();
		setQuestionListActions();

		view.setGetVisibleQuestionIDsCallback(visiblePostsIndexRange -> {
			var visibleQuestionIDs = postListViewModel.getVisibleQuestionIDs(visiblePostsIndexRange);
			if (visibleQuestionIDs == null)
				return;

			AnswerPreloadPriority.resetPriorityList();
			visibleQuestionIDs.forEach(AnswerPreloadPriority::addPriorityQuestion);
		});

	}

	private void initializeQuestionListListeners() {
		List<PostWithPermissions> initialQuestions = new ArrayList<>();

		if (AppCache.getInstance().areInitialQuestionsLoaded()) {
			initialQuestions.addAll(AppCache.getInstance().getInitialQuestionDataProperty().get());
			postListViewModel.populateQuestions(initialQuestions);
			view.getForumListView().setItems(postListViewModel.getVisiblePosts());
		}

		if (AppCache.getInstance().areInitialAnswersLoaded()) {
			postListViewModel.addToAnswerPool(AppCache.getInstance().getAllInitialAnswersDataProperty().get());
		}

		AppCache.getInstance().getInitialQuestionDataProperty().addListener((_, _, questionsWithPermissions) -> {
			if (questionsWithPermissions == null)
				return;

			Platform.runLater(() -> {
				if (!initialQuestions.equals(questionsWithPermissions)) {
					postListViewModel.populateQuestions(questionsWithPermissions);
				}
				view.getForumListView().setItems(postListViewModel.getVisiblePosts());
			});
		});

		AppCache.getInstance().getAnswersDataProperty().addListener((_, _, answersWithPermissions) -> {
			if (answersWithPermissions == null)
				return;

			Platform.runLater(() -> {
				List<PostWithPermissions> newAnswers = new ArrayList<>();
				for (PostWithPermissions answer : answersWithPermissions) {
					boolean exists = AppCache.getInstance().getAllInitialAnswersDataProperty().get().stream()
							.anyMatch(a -> a.getPost().getId() == answer.getPost().getId());
					if (exists)
						newAnswers.add(answer);
				}
				postListViewModel.addToAnswerPool(newAnswers);
			});
		});
	}

	private void setQuestionListActions() {
//        view.getSearchButton().setOnAction(_ -> {
//            String searchTerm = view.getSearchQuestionsTextField().getText();
//            if (searchTerm == null || searchTerm.trim().isEmpty()) {
//                initializeQuestionListListeners();
//                return;
//            }
//
//            String term = searchTerm.toLowerCase();
//            try {
//                ObservableList<PostDisplayViewModel> currentItems = view.getForumListView().getItems();
//                ObservableList<PostDisplayViewModel> filteredItems = currentItems.filtered(vm ->
//                        vm.getPost().getTitleText().toLowerCase().contains(term)
//                                || vm.getPost().getBodyText().toLowerCase().contains(term));
//                view.getForumListView().setItems(filteredItems);
//            } catch (Exception e) {
//                System.err.println("Error during search: " + e.getMessage());
//                e.printStackTrace();
//            }
//        });
	}

}
