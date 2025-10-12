package Controllers;

import Models.Question;

import Sessions.UserSession;

import java.util.ArrayList;
import java.util.List;

import Database.DBConnectionStore;
import Database.IGeneralDBManager;
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
    
    private void refresh() {
        try {
            List<Question> allQuestions = questionService.getAllQuestions();

            if (!view.getShowDeletedQuestionsCheckbox().isSelected()) {
                allQuestions.removeIf(Question::getDeletedStatus);
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

                viewModels.add(new QuestionDisplayViewModel(question, userPermissions));
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
    }
    
    private void setShowDeletedQuestionsCheckboxAction() {
		view.getShowDeletedQuestionsCheckbox().setOnAction(_ -> {
			refresh();
		});
	}
    
    private void setAddQuestionButtonAction() {
        // Add question
        view.getAddQuestionButton().setOnAction(_ -> {
        	String text = view.getTextInputArea().getText();
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
    

    

}
