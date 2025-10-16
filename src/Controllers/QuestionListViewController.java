package Controllers;

import Models.Answer;
import Models.Question;

import Sessions.UserSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import Database.DBConnectionStore;
import Database.IGeneralDBManager;
import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IPermissionService;
import Database.Services.Interfaces.IQuestionService;

import Exceptions.DBExceptions.DataAccessException;

import UI.Elements.QuestionListView;
import UI.Elements.AnswerListView;
import Utils.Debug;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;


public class QuestionListViewController {
	private final DBConnectionStore dBConnectionStore;
	private final IGeneralDBManager generalDBManager;
	private final IQuestionService questionService;
	private final IAnswerService answerService;
	private final IPermissionService permissionService;
    private final UserSession userSession;
    private final QuestionListView view;
    private final Stage primaryStage;

    public QuestionListViewController(
    		DBConnectionStore dBConnectionStore, 
    		Stage primaryStage, 
    		UserSession userSession, 
    		QuestionListView view
    	) {
        this.view = view;
        this.generalDBManager = dBConnectionStore.getGeneralDBManager();
        this.questionService = dBConnectionStore.getQuestionService();
        this.answerService = dBConnectionStore.getAnswerService();
        this.permissionService = dBConnectionStore.getPermissionService();
        
        this.primaryStage = primaryStage;
        this.userSession = userSession;

        this.dBConnectionStore = dBConnectionStore;
        
        setUpActions();
        refresh();
    }
    
    // ===================
    // 		 SETTERS 
 	// ===================
    
    public void refresh() {
        try {
            List<Question> allQuestions = questionService.getAllQuestions();

            if (!view.getShowDeletedQuestionsCheckbox().isSelected()) {
                allQuestions.removeIf(Question::getDeleted);
            }

            List<QuestionDisplayViewModel> viewModels = new ArrayList<>();
            List<Integer> questionIds = new ArrayList<>();
            
            for (Question question : allQuestions) {
                questionIds.add(question.getId());
            }
            
            // TODO:- Inefficient. Figure out the other query
            for (Question question : allQuestions) {
                List<String> userPermissions = permissionService
                        .getEffectivePermissions(userSession.getCurrentUserId(), question.getId(), true);
                
                List<Answer> answers = answerService.getAnswersByQuestionID(question.getId());
                
                List<List<String>>  allAnswerUserPermissions = new ArrayList<>();
                for(Answer answer : answers) {
                	allAnswerUserPermissions.add(permissionService
                            .getEffectivePermissions(userSession.getCurrentUserId(), answer.getId(), false));
                }
                
                calculateDepths(answers);

                viewModels.add(new QuestionDisplayViewModel(
                		question, 
                		userPermissions, 
                		answers, 
                		allAnswerUserPermissions, 
                		userSession,
                		questionService, 
                		answerService, 
                		this,
                		questionService::deleteQuestion, 
                		answerService::addAnswer,
                		this::refresh
                ));
            }

            // Set view model list to the ListView
            ObservableList<QuestionDisplayViewModel> observableVMs =
                    FXCollections.observableArrayList(viewModels);

            view.getForumListView().setItems(observableVMs);

            if (Debug.isEnabled) System.out.println("Forum list refreshed with " + observableVMs.size() + " questions.");
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
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
			ObservableList<QuestionDisplayViewModel> currentItems = view.getForumListView().getItems();
			ObservableList<QuestionDisplayViewModel> filteredItems = currentItems.filtered(qvm -> 
				qvm.getQuestion().getTitle().toLowerCase().contains(searchTerm) || 
				qvm.getQuestion().getText().toLowerCase().contains(searchTerm)
			);
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
        	// TODO:- These should also be enforced upon the database call for security purposes
        	if (text == null || text.trim().isEmpty() || text.length() > 500 || text.equals("Input text here...")) {
        		System.out.println("Question text cannot be empty or placeholder.");
        		return;
        	}
        	// TODO: - Add title input field, set to default for now
            Question q = new Question(userSession.currentUser().getId(), "Default Title", text);
            try {
            	questionService.addQuestion(q);
            	refresh(); //test
            	if (Debug.isEnabled) System.out.println("Added question: " + text);
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
            QuestionDisplayViewModel selectedQuestionVM = view.getForumListView().getSelectionModel().getSelectedItem();
            if (selectedQuestionVM != null) {
                try {
                	questionService.deleteQuestion(selectedQuestionVM.getQuestion().getId());
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
    	view.getDeleteQuestionButton().setOnAction(_-> {
    		QuestionDisplayViewModel selectedQuestion = view.getForumListView().getSelectionModel().getSelectedItem();
			if (selectedQuestion != null) {
				try {
					questionService.deleteQuestion(selectedQuestion.getQuestion().getId());
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
        	QuestionDisplayViewModel selectedQuestionVM = view.getForumListView().getSelectionModel().getSelectedItem();
            
            if (selectedQuestionVM != null) {
                AnswerListView detailView = new AnswerListView();
                
                new AnswerListViewController(
                		dBConnectionStore,
                		primaryStage, 
                		selectedQuestionVM.getQuestion(),
                		userSession,
                		detailView
                );

                primaryStage.getScene().setRoot(detailView.getLayout());
                System.out.println("Viewing details for Question ID: " + selectedQuestionVM.getQuestion().getId());
            }
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
                if (parentAnswer == null) break;
                parentId = parentAnswer.getParentAnswerId();
            }
            depthMap.put(answer.getId(), depth);
            answer.setReplyDepth(depth);
            if (Debug.isEnabled) {
                System.out.println("replyDepth for answer ID " + answer.getId() + " set to " + depth);
            }
        }
    }

}
