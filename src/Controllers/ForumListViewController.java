package Controllers;

import Models.Question;
import Sessions.UserSession;

import java.sql.SQLException;

import Database.DBConnectionStore;
import Database.IGeneralDBManager;
import Database.Repositories.AnswerRepository;
import Database.Repositories.QuestionRepository;
import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IQuestionService;
import UI.Elements.ForumListView;
import UI.Elements.QuestionDetailView;
import UI.Pages.ReviewerHomePage;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

public class ForumListViewController {
	private final DBConnectionStore dBConnectionStore;
	private final IGeneralDBManager generalDBManager;
	private final IQuestionService questionService;
    private final IAnswerService answerService;
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
        this.answerService = dBConnectionStore.getAnswerService();
        
        this.primaryStage = primaryStage;
        this.userSession = userSession;

        this.dBConnectionStore = dBConnectionStore;
        
        wire();
        refresh();
    }
    
    // ===================
    // 		 SETTERS 
 	// ===================

    private void wire() {
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
            } catch (SQLException exception) {
            	// TODO:- Handle outcome if the question can't be added
                System.err.println("Database error: " + exception.getMessage());
                exception.printStackTrace();
            } 
        });

        // Edit question
        view.getEditQuestionButton().setOnAction(e -> {
            Question selectedQuestion = view.getForumListView().getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                selectedQuestion.setText("Edited: " + selectedQuestion.getText());
                refresh();
            }
        });

        // Delete question

        view.getDeleteQuestionButton().setOnAction(e -> {
            Question selectedQuestion = view.getForumListView().getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                try {
                	questionService.deleteQuestion(selectedQuestion.getId());
                    refresh();
                } catch (SQLException exception) {
                	// TODO:- Handle outcome if the question can't be deleted
                    System.err.println("Database error: " + exception.getMessage());
                    exception.printStackTrace();
                } 
            }
        });

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

    private void refresh() {
        view.getForumListView().setItems(
            FXCollections.observableArrayList(questionService.getAllQuestions())
        );
    }
}
