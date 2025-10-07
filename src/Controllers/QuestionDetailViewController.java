package Controllers;

import Models.Answer;
import Models.Question;
import Sessions.UserSession;
import UI.Elements.ForumListView;
import UI.Elements.QuestionDetailView;

import java.sql.SQLException;

import Database.DBConnectionStore;

import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IQuestionService;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class QuestionDetailViewController {
    private final DBConnectionStore dbConnectionStore;
	private final QuestionDetailView view;
    private final Question question;
    private final IQuestionService questionService;
    private final IAnswerService answerService;
    private final Stage primaryStage;
    private final UserSession userSession;

    public QuestionDetailViewController(
            DBConnectionStore dBConnectionStore,
            Stage primaryStage, 
            Question question,
            UserSession userSession,
            QuestionDetailView view
    ) {
        this.dbConnectionStore = dBConnectionStore;
        this.questionService = dBConnectionStore.getQuestionService();
        this.answerService = dBConnectionStore.getAnswerService();
        
        this.primaryStage = primaryStage;
		this.view = view;
        this.question = question;

		this.userSession = userSession;
        wire();
        refreshAnswers();
    }
    
    // ===================
    // 		 SETTERS 
 	// ===================

    private void wire() {
        // Show selected question at the top
        view.getQuestionLabel().setText("[" + question.getId() + "] " + question.getText());

        // Add answer
        view.getAddAnswerButton().setOnAction(e -> {
        	String text = view.getTextInputArea().getText();
        	if (text == null || text.trim().isEmpty() || text.length() > 500 || text.equals("Input text here...")) {
        		System.out.println("Answer text cannot be empty or placeholder.");
        		return;
        	}
        	try {
        		answerService.addAnswer(question.getId(), userSession.currentUser().getId(), text);
        	} catch (SQLException exception) {
        		// TODO: - Deal with answer not being able to be added
                System.err.println("Database error: " + exception.getMessage());
                exception.printStackTrace();
            }
        	
        	
            refreshAnswers();
        });

        // Edit answer
        view.getEditAnswerButton().setOnAction(e -> {
            Answer sel = view.getAnswerList().getSelectionModel().getSelectedItem();
            if (sel != null) {
                sel.setText("Edited: " + sel.getText());
                refreshAnswers();
            }
        });

        // Delete answer
        view.getDeleteAnswerButton().setOnAction(e -> {
            Answer sel = view.getAnswerList().getSelectionModel().getSelectedItem();
            if (sel != null) {
            	try {
            		answerService.deleteAnswer(sel.getId());
            		refreshAnswers();
            	} catch (SQLException exception) {
            		// TODO: - Deal with answer not being able to be deleted
                    System.err.println("Database error: " + exception.getMessage());
                    exception.printStackTrace();
                }
                
            }
        });

        // Back to ForumListView (new scene, same as other buttons)
        view.getBackButton().setOnAction(e -> {
            ForumListView forumView = new ForumListView();

            ForumListViewController forumController =
                new ForumListViewController(dbConnectionStore, primaryStage, userSession, new ForumListView());

            Scene forumScene = new Scene(forumView.getLayout(), 800, 600);
            primaryStage.setScene(forumScene);
            primaryStage.setTitle("Forum Page");
        });
        // Mark question as resolved
        view.getResolveButton().setOnAction(e -> {
			question.setResolved(true);
			view.getQuestionLabel().setText("[" + question.getId() + "] " + question.getText() + " (Resolved)");
			question.setText(question.getText() + " (Resolved)");

			
		});


    }

    private void refreshAnswers() {
    		try {
    			view.getAnswerList().setItems(
    					FXCollections.observableArrayList(answerService.getAllAnswers()));
    		} catch (SQLException exception) {
    			// TODO: - Deal with answer not being able to be fetched
    			System.err.println("Database error: " + exception.getMessage());
                exception.printStackTrace();
    		}
    }
}
