package Controllers;

import Models.Answer;
import Models.Question;

import Sessions.UserSession;

import UI.Elements.QuestionListView;
import UI.Elements.AnswerListView;
import Utils.Debug;

import java.sql.SQLException;

import Database.DBConnectionStore;
import Database.Services.Interfaces.IAnswerService;
import Exceptions.DBExceptions.DataAccessException;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AnswerListViewController {
    private final DBConnectionStore dbConnectionStore;
	private final AnswerListView view;
    private final Question question;
    private final IAnswerService answerService;
    private final Stage primaryStage;
    private final UserSession userSession;

    public AnswerListViewController(
            DBConnectionStore dBConnectionStore,
            Stage primaryStage, 
            Question question,
            UserSession userSession,
            AnswerListView view
    ) {
        this.dbConnectionStore = dBConnectionStore;
        this.answerService = dBConnectionStore.getAnswerService();
        
        this.primaryStage = primaryStage;
		this.view = view;
        this.question = question;

        this.userSession = userSession;
		setInitialLayout();
		    
        setUpAction();
        refreshAnswers();
    }
    
    // ===================
    // 		 SETTERS 
 	  // ===================
    
    private void refreshAnswers() {
    	try {
    		view.getAnswerList().setItems(
    				FXCollections.observableArrayList(answerService.getAllAnswersForQuestion(question.getId())));
    		if (Debug.isEnabled) System.out.println("Answers refreshed.");
    	} catch (DataAccessException exception) {
    		// TODO: - Deal with answer not being able to be fetched
    		System.err.println("Database error: " + exception.getMessage());
            exception.printStackTrace();
    	}
    }
  
    private void setAddAnswerButtonAction() {
        // Add answer
        view.getAddAnswerButton().setOnAction(_ -> {
        	if (Debug.isEnabled) System.out.println("Add Answer button clicked.");
        	String text = view.getTextInputArea().getText();
        	if (text == null || text.trim().isEmpty() || text.length() > 500 || text.equals("Input text here...")) {
        		System.out.println("Answer text cannot be empty or placeholder.");
        		return;
        	}
        	Answer a = new Answer(question.getId(), userSession.currentUser().getId(), text);
        	try {
        		answerService.addAnswer(a);
                refreshAnswers();
                if (Debug.isEnabled) System.out.println("Added answer: " + text);
        	} catch (DataAccessException dataAccessException) {
        		// TODO: - Deal with answer not being able to be added
                System.err.println("Database error: " + dataAccessException.getMessage());
                dataAccessException.printStackTrace();
            }
        });
		
	}
    private void setReplyToAnswerButtonAction() {
    	// Reply to an answer
    	
    }
    private void setEditAnswerButtonAction() {
        // Edit answer
        view.getEditAnswerButton().setOnAction(_ -> {
            Answer sel = view.getAnswerList().getSelectionModel().getSelectedItem();
            if (sel != null) {
                sel.setText("Edited: " + sel.getText());
                refreshAnswers();
            }
        });
    	
    }
    private void setDeleteAnswerButtonAction() {
        // Delete answer
        view.getDeleteAnswerButton().setOnAction(_ -> {
            Answer sel = view.getAnswerList().getSelectionModel().getSelectedItem();
            if (sel != null) {
            	try {
            		answerService.deleteAnswer(sel.getId());
            		refreshAnswers();
            	} catch (DataAccessException exception) {
            		// TODO: - Deal with answer not being able to be deleted
                    System.err.println("Database error: " + exception.getMessage());
                    exception.printStackTrace();
                }
                
            }
        });
    }
  
    private void setBackButtonAction() {
        // Back to ForumListView (new scene, same as other buttons)
        view.getBackButton().setOnAction(_ -> {
            QuestionListView forumView = new QuestionListView();

            new QuestionListViewController(dbConnectionStore, primaryStage, userSession, forumView);

            Scene forumScene = new Scene(forumView.getLayout(), 800, 600);
            primaryStage.setScene(forumScene);
            primaryStage.setTitle("Forum Page");
        });
    }
  
    private void setResolveButtonAction() {
        // Mark question as resolved
        view.getResolveButton().setOnAction(_ -> {
			  question.setResolved(true);
			  view.getQuestionLabel().setText("[" + question.getId() + "] " + question.getText() + " (Resolved)");
			  question.setText(question.getText() + " (Resolved)");
		  });
		
	  }
    
    private void setUpAction() {
    	setAddAnswerButtonAction();
    	setEditAnswerButtonAction();
    	setDeleteAnswerButtonAction();
    	setBackButtonAction();
    	setResolveButtonAction();
    	setReplyToAnswerButtonAction();
    }
    
    private void setInitialLayout() {
    	// Show selected question at the top
    	view.getQuestionLabel().setText("[" + question.getId() + "] " + question.getText());
    }


}
