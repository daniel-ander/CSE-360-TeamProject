package Controllers;

import Models.Answer;
import Models.IPost;
import Models.Question;

import Sessions.UserSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import Config.AppCache;
import Database.DBConnectionStore;
import Database.IGeneralDBManager;
import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IPermissionService;
import Database.Services.Interfaces.IQuestionService;
import DisplayModels.PostWithPermissions;
import DisplayModels.QuestionWithAnswersAndPermissions;
import Exceptions.DBExceptions.DataAccessException;

import UI.Elements.QuestionListView;

import Utils.Debug;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class QuestionListViewController {
	private final DBConnectionStore dBConnectionStore;
	private final IGeneralDBManager generalDBManager;
	private final IQuestionService questionService;
	private final IAnswerService answerService;
	private final IPermissionService permissionService;
	private final List<PostDisplayViewModel> questionViewModels = new ArrayList<>();
	private ObservableList<PostDisplayViewModel> observableVMs;
	private final Map<Integer, List<PostDisplayViewModel>> answerMap = new HashMap<>();
	private final UserSession userSession;
	private final QuestionListView view;
	private final Stage primaryStage;

	public QuestionListViewController(DBConnectionStore dBConnectionStore, Stage primaryStage, UserSession userSession,
			QuestionListView view) {
		this.view = view;
		this.generalDBManager = dBConnectionStore.getGeneralDBManager();
		this.questionService = dBConnectionStore.getQuestionService();
		this.answerService = dBConnectionStore.getAnswerService();
		this.permissionService = dBConnectionStore.getPermissionService();

		this.primaryStage = primaryStage;
		this.userSession = userSession;

		this.dBConnectionStore = dBConnectionStore;

		setUpActions();
		// initializeListeners();
		refresh();
	}

	// ===================
	// SETTERS
	// ===================
	
	private void expand(int postIdToExpand) {
	    if (!answerMap.containsKey(postIdToExpand))
	        return;

	    List<PostDisplayViewModel> answers = answerMap.get(postIdToExpand);

	    // Find the index of the parent question
	    for (int i = 0; i < observableVMs.size(); i++) {
	        if (observableVMs.get(i).getPost().getId().equals(postIdToExpand)) {
	            // Insert answers right after the question
	        	int insertIndex = i + 1;
	        	for (PostDisplayViewModel answerVm : answers) {
	        	    boolean alreadyPresent = observableVMs.stream()
	        	        .anyMatch(vm -> vm.getPost().getId().equals(answerVm.getPost().getId()));
	        	    if (!alreadyPresent) {
	        	        observableVMs.add(insertIndex, answerVm);
	        	        insertIndex++; // Keep answers in correct order
	        	    }
	        	}

	            break;
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
					questionViewModels.add(new PostDisplayViewModel(
							questionWithPermissions.getPost(),
							questionWithPermissions.getPermissions(), 
							userSession, 
							id -> {
								try {
									questionService.deleteQuestion(id);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}, post -> {
								try {
									answerService.addAnswer((Answer) post);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}, (id, newTitle, newBody) -> {
								try {
									questionService.updateQuestion(id, newTitle, newBody);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}, () -> {
								this.expand(questionWithPermissions.getPost().getId());
							},
							this::refresh));

//						
//						for (PostWithPermissions answer : questionAndAnswers.getAnswers()) {
//							viewModels.add(new PostDisplayViewModel(answer.getPost(),
//									answer.getPermissions(), 
//									userSession, 
//									id -> {
//										try {
//											questionService.deleteQuestion(id);
//										} catch (Exception e) {
//											e.printStackTrace();
//										}
//									}, post -> {
//										try {
//											answerService.addAnswer((Answer) post);
//										} catch (Exception e) {
//											e.printStackTrace();
//										}
//									}, (id, newTitle, newBody) -> {
//										try {
//											questionService.updateQuestion(id, newTitle, newBody);
//										} catch (Exception e) {
//											e.printStackTrace();
//										}
//									}, 
//									this::refresh)
//								);
//						}
				}

				observableVMs = FXCollections.observableArrayList(questionViewModels);
				// Display the questions
				view.getForumListView().setItems(observableVMs);
			});

		});

//		observableVMs.indexOf(viewModels);
		
		// Search observableVMs structure for question by ID
		// Insert backwards into the slot where the ID is
		

		AppCache.getInstance().getAnswersDataProperty().addListener((_, _, answersWithPermissions) -> {
			if (answersWithPermissions == null)
				return;

			Platform.runLater(() -> {
				
				int parentQuestionID = 0;
				
				for(PostWithPermissions answerWithPermissions : answersWithPermissions) {
					parentQuestionID = ((Answer)answerWithPermissions.getPost()).getQuestionId();
					break;
				}
				
//				int insertIndex = 0;
//				
//				for(int i = 0; i < observableVMs.size(); i++) {
//					if(observableVMs.get(i).getPost().getPostType() == IPost.Type.Question &&
//							observableVMs.get(i).getPost().getId().equals(parentQuestionID)) {
//						insertIndex = i + 1;
//						break;
//					}
//				}
				
				List<PostDisplayViewModel> answerInserts = new ArrayList<>();
				

				for (PostWithPermissions answerWithPermissions : answersWithPermissions) {
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
								this.expand(answerWithPermissions.getPost().getId());
							},
							this::refresh
					);
//					vm.setIsHidden(true);

					boolean alreadyAdded = observableVMs.stream()
							.anyMatch(v -> v.getPost().getId().equals(vm.getPost().getId()));
					
					
					if (!alreadyAdded)
						answerInserts.add(vm);
					
//					observableVMs.addAll(insertIndex, answerInserts);
					
					
				}
				
				answerMap.put(parentQuestionID, answerInserts);

//				view.getForumListView().setItems(observableVMs);
			});
		});

//		AppCache.getInstance().getInitialDataProperty().addListener((_, _, questionsWithAnswersAndPermissions) -> {
//			if (questionsWithAnswersAndPermissions == null)
//				return;
//			
//			Platform.runLater(() -> {
//				int index = 0;
//
//				for (QuestionWithAnswersAndPermissions questionWithAnswersAndPermissions : questionsWithAnswersAndPermissions) {
//					index += 1;
//
//					List<PostDisplayViewModel> answerInserts = new ArrayList<>();
//
//					for (PostWithPermissions answerWithPermissions : questionWithAnswersAndPermissions
//							.getAnswersWithPermissions()) {
//						PostDisplayViewModel vm = new PostDisplayViewModel(answerWithPermissions.getPost(),
//								answerWithPermissions.getPermissions(), userSession, id -> {
//									try {
//										answerService.deleteAnswer(id);
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//								}, post -> {
//									try {
//										answerService.addAnswer((Answer) post);
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//								}, (id, newBody, isCorrect) -> {
//									try {
//										answerService.updateAnswer(id, newBody, isCorrect.equals("yes") ? true : false);
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//								}, this::refresh);
//						// vm.setIsHidden(true);
//						System.out.println(vm.isQuestion());
//						boolean alreadyAdded = viewModels.stream()
//								.anyMatch(v -> v.getPost().getId().equals(vm.getPost().getId()));
//
//						if (!alreadyAdded)
//							answerInserts.add(vm);
//					}
//
//					System.out.println("index is: " + index);
//					System.out.println("size is: " + observableVMs.size());
//					observableVMs.addAll(index, answerInserts);
//
//					index += answerInserts.size();
//				}
//
//				view.getForumListView().setItems(observableVMs);
//			});
//
//		});

//		try {
//			List<Question> allQuestions = questionService.getAllQuestions();
//
//			if (!view.getShowDeletedQuestionsCheckbox().isSelected()) {
//				allQuestions.removeIf(Question::getIsDeleted);
//			}
//
//			// TODO:- Inefficient. Figure out the other query
//			for (Question question : allQuestions) {
//				List<String> userQuestionPermissions = permissionService
//						.getEffectivePermissions(userSession.getCurrentUserId(), question.getId(), IPost.Type.Question);
//
//				// For the question cell
//				viewModels.add(new PostDisplayViewModel(question, userQuestionPermissions, userSession, id -> {
//					try {
//						questionService.deleteQuestion(id);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}, post -> {
//					try {
//						answerService.addAnswer((Answer) post);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}, (id, newTitle, newBody) -> {
//					try {
//						questionService.updateQuestion(id, newTitle, newBody);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}, this::refresh));
//
//				List<Answer> answers = answerService.getAnswersByQuestionID(question.getId());
//
//				// TODO: - Put the indenting logic somewhere more appropriate
//				calculateDepths(answers); // For indenting purposes
//
//				for (Answer answer : answers) {
//					List<String> userAnswerPermissions = permissionService
//							.getEffectivePermissions(userSession.getCurrentUserId(), answer.getId(), IPost.Type.Answer);
//
//					// For the answer cell
//					viewModels.add(new PostDisplayViewModel(answer, userAnswerPermissions, userSession, id -> {
//						try {
//							answerService.deleteAnswer(id);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}, post -> {
//						try {
//							answerService.addAnswer((Answer) post);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}, (id, newBody, isCorrect) -> {
//						try {
//							answerService.updateAnswer(id, newBody, isCorrect.equals("yes") ? true : false);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}, this::refresh));
//
//				}
//
//			}
//
//			// Set view model list to the ListView
//			ObservableList<PostDisplayViewModel> observableVMs = FXCollections.observableArrayList(viewModels);
//
//			view.getForumListView().setItems(observableVMs);
//
//			if (Debug.isEnabled)
//				System.out.println("Forum list refreshed with " + observableVMs.size() + " questions.");
//		} catch (DataAccessException e) {
//			e.printStackTrace();
//		}
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
			PostDisplayViewModel selectedQuestionVM = view.getForumListView().getSelectionModel().getSelectedItem();

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

	private void calculateDepths(List<Answer> answers) {
		Map<Integer, Integer> depthMap = new HashMap<>();
		Map<Integer, Answer> answerById = new HashMap<>();
		for (Answer a : answers) {
			answerById.put(a.getId(), a);
			if (Debug.isEnabled) {
				System.out.println("Answer ID " + a.getId() + " with parent ID " + a.getParentAnswerId());
			}
		}

		for (Answer answer : answers) {
			int depth = 0;
			int parentId = answer.getParentAnswerId();
			if (Debug.isEnabled) {
				System.out.println("Calculating depth for answer ID " + answer.getId() + " with parent ID " + parentId);
			}

			while (parentId > 0 && depth < 10) { // Prevent infinite loops
				depth++;
				Integer knownDepth = depthMap.get(parentId);
				if (knownDepth != null) {
					depth += knownDepth;
					break;
				}
				Answer parentAnswer = answerById.get(parentId);
				if (parentAnswer == null)
					break;
				parentId = parentAnswer.getParentAnswerId();
			}
			depthMap.put(answer.getId(), depth);
			answer.setReplyDepth(depth);
			if (Debug.isEnabled) {
				System.out.println("replyDepth for answer ID " + answer.getId() + " set to " + depth);
			}
		}
	}

//    private void initializeListeners() {
//    	AppCache.getInstance().getInitialDataProperty().addListener((obs, newValue, oldValue) -> {
//    		if(newValue != null) {
//    			
//    		}
//    	});
//    }

}
