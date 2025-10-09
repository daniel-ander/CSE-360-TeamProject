package Controllers;

import Models.Question;

import Sessions.UserSession;

import Database.DBConnectionStore;
import Database.IGeneralDBManager;
import Database.Services.Interfaces.IQuestionService;

import Exceptions.DBExceptions.DataAccessException;

import UI.Elements.ForumListView;
import UI.Elements.QuestionDetailView;
import Utils.Debug;
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
        
        setUpActions();
        refresh();
    }
    
    // ===================
    // 		 SETTERS 
 	// ===================
    
    private void refresh() {
        view.getForumListView().setItems(FXCollections.observableArrayList(questionService.getAllQuestions()));
        if (Debug.isEnabled) System.out.println("Forum list refreshed.");
    }
    private void setUpActions() {
    	setAddQuestionButtonAction();
    	setDeleteQuestionButtonAction();
    	setEditQuestionButtonAction();
    	setViewQuestionButtonAction();
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
            Question q = new Question("Default Title", userSession.currentUser().getId(), text);
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
    
    private void setDeleteQuestionButtonAction() {
    	// TODO: - Work with the DB
        view.getDeleteQuestionButton().setOnAction(_ -> {
            Question selectedQuestion = view.getForumListView().getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                selectedQuestion.setText("Edited: " + selectedQuestion.getText());
                refresh();
            }
        });
		
	}
    private void setViewQuestionButtonAction() {
        // View question → switch to detail page
        view.getViewQuestionButton().setOnAction(_ -> {
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
    

    

}
