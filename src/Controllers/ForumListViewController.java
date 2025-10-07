package Controllers;

import Models.Question;

import Sessions.UserSession;

import Database.DBConnectionStore;
import Database.IGeneralDBManager;
import Database.Services.Interfaces.IQuestionService;

import Exceptions.DBExceptions.DataAccessException;

import UI.Elements.ForumListView;
import UI.Elements.QuestionDetailView;

import javafx.collections.FXCollections;
import javafx.stage.Stage;

public class ForumListViewController {
	private final DBConnectionStore dBConnectionStore;
	private final IGeneralDBManager generalDBManager;
	private final IQuestionService questionService;
    private final UserSession userSession;
    private final ForumListView view;
    private final Stage primaryStage;

    public ForumListViewController(
    		DBConnectionStore dBConnectionStore, 
    		Stage primaryStage, 
    		UserSession userSession, 
    		ForumListView view
    	) {
        this.view = view;
        this.generalDBManager = dBConnectionStore.getGeneralDBManager();
        this.questionService = dBConnectionStore.getQuestionService();
        
        this.primaryStage = primaryStage;
        this.userSession = userSession;

        this.dBConnectionStore = dBConnectionStore;
        
        setUpAction();
        refresh();
    }
    
    // ===================
    // 		 SETTERS 
 	  // ===================
  
    private void setButtonActions() {
		setAddQuestionButtonAction();
		setEditQuestionButtonAction();
		setDeleteQuestionButtonAction();
		setViewQuestionButtonAction();
	}
    
    private void refresh() {
        view.getForumListView().setItems(
            FXCollections.observableArrayList(questionRepository.getAll())
        );
    }
    private void setAddQuestionButtonAction() {
        // Add question
        view.getAddQuestionButton().setOnAction(e -> {
        	String text = view.getTextInputArea().getText();
        	if (text == null || text.trim().isEmpty() || text.length() > 500 || text.equals("Input text here...")) {
        		System.out.println("Question text cannot be empty or placeholder.");
        		return;
        	}
            Question q = new Question(
                generalDBManager.getNextForumPostId(), // Assuming a method to get next ID
                userSession.currentUser().getId(),
                text   
            );
            try {
            	questionService.addQuestion(q);
            } catch (DataAccessException dataAccessException) {
            	// TODO:- Handle outcome if the question can't be added
                System.err.println("Database error: " + dataAccessException.getMessage());
                dataAccessException.printStackTrace();
            } 
        });
	}
    
    private void setDeleteQuestionButtonAction() {
    	view.getDeleteQuestionButton().setOnAction(_ -> {
            Question q = new Question(databaseHelper.getNextForumPostId(), text);
            q.setAuthor(currentUser);
            questionRepository.addQuestion(q);
            refresh();
        });
		
	}
    private void setEditQuestionButtonAction() {
        // Edit question
        view.getEditQuestionButton().setOnAction(e -> {
            Question selectedQuestion = view.getForumListView().getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                try {
                	questionService.deleteQuestion(selectedQuestion.getId());
                    refresh();
                } catch (DataAccessException dataAccessException) {
                	// TODO:- Handle outcome if the question can't be deleted
                    System.err.println("Database error: " + dataAccessException.getMessage());
                    dataAccessException.printStackTrace();
                } 
            }
        });
	}
    
    private void setEditQuestionButtonAction() {
    	// TODO: - DB Call?
    	view.getAddQuestionButton().setOnAction(_ -> {
    		Question selectedQuestion = view.getForumListView().getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            selectedQuestion.setText("Edited: " + selectedQuestion.getText());
            refresh();
        }
    	
    }
    private void setDeleteQuestionButtonAction() {
        // Delete question
        view.getDeleteQuestionButton().setOnAction(e -> {
            Question selectedQuestion = view.getForumListView().getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                selectedQuestion.setText("Edited: " + selectedQuestion.getText());
                refresh();
            }
        });
		
	}
    private void setViewQuestionButtonAction() {
        // View question → switch to detail page
        view.getViewQuestionButton().setOnAction(e -> {
            Question selectedQuestion = view.getForumListView().getSelectionModel().getSelectedItem();
            
            if (selectedQuestion != null) {
                QuestionDetailView detailView = new QuestionDetailView();
                
                new QuestionDetailViewController(
                		dBConnectionStore,
                		primaryStage, 
                		selectedQuestion,
                		userSession,
                		new QuestionDetailView()
                );

                primaryStage.getScene().setRoot(detailView.getLayout());
                System.out.println("Viewing details for Question ID: " + selectedQuestion.getId());
            }
        });
	}
    
    private void setUpAction() {
    	setAddQuestionButtonAction();
    	setDeleteQuestionButtonAction();
    	setEditQuestionButtonAction();
    	setViewQuestionButtonAction();
    }
    

}
