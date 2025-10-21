package Controllers;

import Models.Answer;
import Models.IPost;
import Models.Question;

import Sessions.UserSession;

import Utils.KeyPairHolder;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import Config.AppCache;
import Database.DBConnectionStore;
import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IQuestionService;
import DisplayModels.PostWithPermissions;
import Exceptions.DBExceptions.DataAccessException;

import UI.Elements.QuestionListView;

import Utils.Debug;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class QuestionListViewController {
	private final IQuestionService questionService;
	private final IAnswerService answerService;
	private final List<PostDisplayViewModel> questionViewModels = new ArrayList<>();
	private ObservableList<PostDisplayViewModel> observableVMs;
	private final Map<KeyPairHolder<Integer, Integer>, List<PostDisplayViewModel>> postParentMap = new HashMap<>();
//	private final Map<KeyPairHolder<Integer, Integer>, List<PostDisplayViewModel>> postParentMap = new HashMap<>();
	private final UserSession userSession;
	private final QuestionListView view;

	public QuestionListViewController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession,
			QuestionListView view) {
		this.view = view;
		this.questionService = dBConnectionStore.getQuestionService();
		this.answerService = dBConnectionStore.getAnswerService();

		this.userSession = userSession;

		setUpActions();
		// initializeListeners();
		refresh();
	}

	// ===================
	// SETTERS
	// ===================

	private void toggleExpand(int postIdToToggle, KeyPairHolder<Integer, Integer> questionAnswerIDs, IPost.Type postType) {
		
		if(postType == IPost.Type.Answer) {
//			System.out.println("The keys are: " + questionAnswerIDs);
//			System.out.println("The size is: " + parentAnswerMap.size());
			for(Entry<KeyPairHolder<Integer, Integer>, List<PostDisplayViewModel>> key : postParentMap.entrySet()) {
				System.out.println("Read Keys: " + key);
			}
//			System.out.println("The size of sub is: " + parentAnswerMap.get(questionAnswerIDs));
		}
		
		PostDisplayViewModel currentPostVM = observableVMs.stream()
			    .filter(v -> v.getPost().getId().equals(postIdToToggle) && v.getPost().getPostType() == postType)
			    .findFirst()
			    .orElse(null);
		
		if(currentPostVM == null) return;
		
		
		Map<KeyPairHolder<Integer, Integer>, List<PostDisplayViewModel>> answers = currentPostVM.isQuestion() ? postParentMap
				: postParentMap;

		int currentPostIndex = IntStream.range(0, observableVMs.size())
				.filter(i -> observableVMs.get(i).getPost().getId().equals(postIdToToggle)
						&& (currentPostVM.isQuestion() ? observableVMs.get(i).isQuestion()
								: !observableVMs.get(i).isQuestion()))
									.findFirst().orElse(-1);

		boolean lookingForNextQuestion = true;
		int indexPointer = currentPostIndex + 1;
		int nextQuestionIndex = indexPointer;
		
		while(lookingForNextQuestion) {
			// [Q1, A1, A2, --> END/NULL <--] OR [Q1, A1, A2, --> Q2 <--, A1]
			if(observableVMs.get(indexPointer) == null || observableVMs.get(indexPointer).isQuestion()) {
				nextQuestionIndex = indexPointer;
				break;
			}

			indexPointer++;
		}

		System.out.println("current post index: " + currentPostIndex);
		System.out.println("next question index: " + nextQuestionIndex);

		if (currentPostVM.isExpanded()) {
			for (int i = answers.get(questionAnswerIDs).size() - 1; i >= 0; i--) {
				PostDisplayViewModel child = answers.get(questionAnswerIDs).get(i);

				child.setDepthLevel(currentPostVM.getDepthLevel() + 1);

				observableVMs.add(currentPostIndex + 1, child);
				
//				 If the leaf comment doesn't have any sub-comments
				if (postParentMap.get(questionAnswerIDs) == null && !currentPostVM.isQuestion()) {
					currentPostVM.setDoesHaveChildren(false);
				}

			}
		} else {
			int answerOffSetIndex = nextQuestionIndex - 1;

		    if (!currentPostVM.isQuestion()) {
		        answerOffSetIndex = postParentMap.get(questionAnswerIDs).size() + currentPostIndex;
		    }

		    List<PostDisplayViewModel> children = answers.get(questionAnswerIDs);
		    if (children != null) {
		        for (PostDisplayViewModel child : children) {
		            child.setIsExpanded(false); 

		            Map<KeyPairHolder<Integer, Integer>, List<PostDisplayViewModel>> subMap = child.isQuestion()
		                    ? postParentMap
		                    : postParentMap;

		            List<PostDisplayViewModel> subChildren = subMap.get(questionAnswerIDs);
		            if (subChildren != null) {
		                for (PostDisplayViewModel subChild : subChildren) {
		                    subChild.setIsExpanded(false);
		                }
		            }
		        }
		    }

		    for (int i = answerOffSetIndex; i > currentPostIndex; i--) {
		        observableVMs.remove(i);
		    }

		}
	}

	public void refresh() {
		AppCache.getInstance().getInitialQuestionData().addListener((_, _, questionsWithPermissions) -> {
			if (questionsWithPermissions == null)
				return;

			Platform.runLater(() -> {
				for (PostWithPermissions questionWithPermissions : questionsWithPermissions) {
					// TODO: - Add button action to expand
					PostDisplayViewModel vm = new PostDisplayViewModel(
							questionWithPermissions.getPost(),
							questionWithPermissions.getPermissions(), 
							userSession, id -> {
								try {
									answerService.deleteAnswer(id);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}, post -> {
								try {
									answerService.addAnswer((Answer) post);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}, (id, newBody, isCorrect) -> {
								try {
									answerService.updateAnswer(id, newBody, isCorrect.equals("yes") ? true : false);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}, () -> {
								this.toggleExpand(
										questionWithPermissions.getPost().getId(), 
										new KeyPairHolder<Integer, Integer>(
												questionWithPermissions.getPost().getId(), null), 
										IPost.Type.Question
								);
							}, this::refresh);
					vm.setDepthLevel(0);
					
					questionViewModels.add(vm);
				}

				observableVMs = FXCollections.observableArrayList(questionViewModels);
				view.getForumListView().setItems(observableVMs);
			});

		});

		AppCache.getInstance().getAnswersDataProperty().addListener((_, _, answersWithPermissions) -> {
			if (answersWithPermissions == null)
				return;

			Platform.runLater(() -> {  

				for (PostWithPermissions answerWithPermissions : answersWithPermissions) {
					
					Integer parentAnswerId = ((Answer) answerWithPermissions.getPost()).getParentAnswerId();
					int parentQuestionId = ((Answer) answerWithPermissions.getPost()).getQuestionId();
					
					KeyPairHolder<Integer, Integer> keys = new KeyPairHolder<Integer, Integer>(parentQuestionId, parentAnswerId);
					
					boolean isChildOfAnswer = parentAnswerId != null;
					Map<KeyPairHolder<Integer, Integer>, List<PostDisplayViewModel>> answerMap = 
							isChildOfAnswer ? postParentMap : postParentMap;
					
					
						PostDisplayViewModel vm = new PostDisplayViewModel(
								answerWithPermissions.getPost(),
								answerWithPermissions.getPermissions(), 
								userSession, 
								id -> {
									try {
										answerService.deleteAnswer(id);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}, post -> {
									try {
										answerService.addAnswer((Answer) post);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}, (id, newBody, isCorrect) -> {
									try {
										answerService.updateAnswer(id, newBody, isCorrect.equals("yes") ? true : false);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}, () -> {
									this.toggleExpand(answerWithPermissions.getPost().getId(), keys, IPost.Type.Answer);
								}, 
								this::refresh
						);						
						
						if (answerMap.containsKey(keys)) {
							System.out.println("Added keys: " + keys);
							answerMap.get(keys).add(vm);
						} else {
							System.out.println("Added keys: " + keys);
							List<PostDisplayViewModel> newList = new ArrayList<>();
							newList.add(vm);
							answerMap.put(keys, newList);
						}

					}				
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
			refresh();
		});
	}

	private void setSearchQuestionsButtonAction() {
		view.getSearchButton().setOnAction(_ -> {
			String searchTerm = view.getSearchQuestionsTextField().getText().toLowerCase();
			if (searchTerm == null || searchTerm.trim().isEmpty()) {
				refresh();
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
				refresh(); // test
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
					refresh();
				} catch (DataAccessException dataAccessException) {
					// TODO:- Handle outcome if the question can't be deleted
					System.err.println("Database error: " + dataAccessException.getMessage());
					dataAccessException.printStackTrace();
				}
			}
		});
	}

	private void setDeleteQuestionButtonAction() {
		System.out.println("Super pressed");
		// Delete question
		view.getDeleteQuestionButton().setOnAction(_ -> {
			PostDisplayViewModel selectedQuestion = view.getForumListView().getSelectionModel().getSelectedItem();
			if (selectedQuestion != null) {
				try {
					questionService.deleteQuestion(selectedQuestion.getPost().getId());
					refresh();
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
//			PostDisplayViewModel selectedQuestionVM = view.getForumListView().getSelectionModel().getSelectedItem();

//			if (selectedQuestionVM != null) {
//				AnswerListView detailView = new AnswerListView();
//
//				new AnswerListViewController(dBConnectionStore, primaryStage, (Question) selectedQuestionVM.getPost(), // Hacky,
//																														// but
//																														// good
//																														// for
//																														// now
//						userSession, detailView);
//
//				primaryStage.getScene().setRoot(detailView.getLayout());
//				System.out.println("Viewing details for Question ID: " + selectedQuestionVM.getPost().getId());
//			}
		});
	}

}
