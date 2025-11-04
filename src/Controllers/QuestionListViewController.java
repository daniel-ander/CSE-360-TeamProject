package Controllers;

import Models.Question;

import Sessions.UserSession;

import java.util.ArrayList;

import java.util.List;

import Config.AppCache;
import Database.DBConnectionStore;

import Database.Services.Interfaces.IQuestionService;
import DisplayModels.PostWithPermissions;
import Exceptions.DBExceptions.DataAccessException;

import UI.Elements.QuestionListView;
import Utils.AnswerPreloadPriority;
import Utils.Debug;
import javafx.application.Platform;

import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class QuestionListViewController {
	private final IQuestionService questionService;
	private final PostListViewModel viewModel;

	private final UserSession userSession;
	private final QuestionListView view;

	public QuestionListViewController(
			DBConnectionStore dBConnectionStore, 
			Stage primaryStage, 
			UserSession userSession, 
			PostListViewModel viewModel,
			QuestionListView view
	) {
		this.view = view;
		this.questionService = dBConnectionStore.getQuestionService();
		this.viewModel = viewModel;
		
		this.userSession = userSession;

		setUpActions();
		initializeListeners();
		
		view.setGetVisibleQuestionIDsCallback(visiblePostsIndexRange -> {
			List<Integer> visibleQuestionIDs = viewModel.getVisibleQuestionIDs(visiblePostsIndexRange);
			if(visibleQuestionIDs == null)
				return;
			
			AnswerPreloadPriority.resetPriorityList();
			
			for(int visibleQuestionID : visibleQuestionIDs) {
				AnswerPreloadPriority.addPriorityQuestion(visibleQuestionID);
			}
		});
		
	}

	// ===================
	// SETTERS
	// ===================
	
	public void initializeListeners() {
		List<PostWithPermissions> initialQuestions = new ArrayList<>();
		
		if(AppCache.getInstance().areInitialQuestionsLoaded()) {
			initialQuestions.addAll(AppCache.getInstance().getInitialQuestionData().get());
			
			viewModel.populateQuestions(initialQuestions);
			view.getForumListView().setItems(viewModel.getVisiblePosts());
		}
		
		if(AppCache.getInstance().areInitialAnswersLoaded()) {
			viewModel.addToAnswerPool(AppCache.getInstance().getAllInitialAnswersDataProperty().get());
		}
		
		AppCache.getInstance().getInitialQuestionData().addListener((_, _, questionsWithPermissions) -> {
			if (questionsWithPermissions == null)
				return;

			Platform.runLater(() -> {
				// If something has changed since the last cached run
				if(!initialQuestions.equals(questionsWithPermissions)) {
					viewModel.populateQuestions(questionsWithPermissions);
				}

				view.getForumListView().setItems(viewModel.getVisiblePosts());
			});

		});
		
		AppCache.getInstance().getAnswersDataProperty().addListener((_, _, answersWithPermissions) -> {
			if (answersWithPermissions == null)
				return;

			Platform.runLater(() -> { 
				List<PostWithPermissions> newAnswers = new ArrayList<>();
				for(PostWithPermissions answerWithPermissions : answersWithPermissions) {
					boolean exists = AppCache.getInstance()
						    .getAllInitialAnswersDataProperty()
						    .get()
						    .stream()
						    .anyMatch(a -> a.getPost().getId() == answerWithPermissions.getPost().getId());

						if (!exists) {
						    continue;
						} else {
							// If does exist, check every property and replace if necessary
						}
					
					newAnswers.add(answerWithPermissions);
				}
				
				AppCache.getInstance().setAnswersDataProperty(newAnswers);
				viewModel.addToAnswerPool(newAnswers);
			});
		});

	}

	private void setUpActions() {
		setAddQuestionButtonAction();
		setDeleteQuestionButtonAction();
		setEditQuestionButtonAction();
		setViewQuestionButtonAction();
		setShowDeletedQuestionsCheckboxAction();
		setSearchQuestionsButtonAction();
	}

	private void setShowDeletedQuestionsCheckboxAction() {
		view.getShowDeletedQuestionsCheckbox().setOnAction(_ -> {
			initializeListeners();
		});
	}

	private void setSearchQuestionsButtonAction() {
		view.getSearchButton().setOnAction(_ -> {
			String searchTerm = view.getSearchQuestionsTextField().getText().toLowerCase();
			if (searchTerm == null || searchTerm.trim().isEmpty()) {
				initializeListeners();
				return;
			}
			try {
				ObservableList<PostDisplayViewModel> currentItems = view.getForumListView().getItems();
				ObservableList<PostDisplayViewModel> filteredItems = currentItems
						.filtered(vm -> vm.getPost().getTitleText().toLowerCase().contains(searchTerm)
								|| vm.getPost().getBodyText().toLowerCase().contains(searchTerm));
				view.getForumListView().setItems(filteredItems);
			} catch (Exception e) {
				System.err.println("Error during search: " + e.getMessage());
				e.printStackTrace();
			}
		});
	}

	private void setAddQuestionButtonAction() {
		// Add question
		view.getAddQuestionButton().setOnAction(_ -> {
			String text = view.getTextInputArea().getText();
			// TODO:- These should also be enforced upon the database call for security
			// purposes
			if (text == null || text.trim().isEmpty() || text.length() > 500 || text.equals("Input text here...")) {
				System.out.println("Question text cannot be empty or placeholder.");
				return;
			}
			// TODO: - Add title input field, set to default for now
			Question q = new Question(userSession.currentUser().getId(), "Default Title", text);
			try {
				questionService.addQuestion(q);
				initializeListeners(); // test
				if (Debug.isEnabled)
					System.out.println("Added question: " + text);
			} catch (DataAccessException dataAccessException) {
				// TODO:- Handle outcome if the question can't be added
				System.err.println("Database error: " + dataAccessException.getMessage());
				dataAccessException.printStackTrace();
			}
		});
	}

	private void setEditQuestionButtonAction() {
		// Edit question
		view.getEditQuestionButton().setOnAction(_ -> {
			PostDisplayViewModel selectedQuestionVM = view.getForumListView().getSelectionModel().getSelectedItem();
			if (selectedQuestionVM != null) {
				try {
					questionService.deleteQuestion(selectedQuestionVM.getPost().getId());
					initializeListeners();
				} catch (DataAccessException dataAccessException) {
					// TODO:- Handle outcome if the question can't be deleted
					System.err.println("Database error: " + dataAccessException.getMessage());
					dataAccessException.printStackTrace();
				}
			}
		});
	}

	private void setDeleteQuestionButtonAction() {
		// Delete question
		view.getDeleteQuestionButton().setOnAction(_ -> {
			PostDisplayViewModel selectedQuestion = view.getForumListView().getSelectionModel().getSelectedItem();
			if (selectedQuestion != null) {
				try {
					questionService.deleteQuestion(selectedQuestion.getPost().getId());
					initializeListeners();
				} catch (DataAccessException dataAccessException) {

					System.err.println("Database error: " + dataAccessException.getMessage());
					dataAccessException.printStackTrace();
				}
			}
		});
	}

	private void setViewQuestionButtonAction() {
		// View question → switch to detail page
		view.getViewQuestionButton().setOnAction(_ -> {
		});
	}

}
