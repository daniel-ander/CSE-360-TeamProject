package Controllers;

import Models.Answer;
import Models.Answers;
import Models.Question;
import Models.Questions;
import UI.Elements.ForumListView;
import UI.Elements.QuestionDetailView;
import Utils.GeneratePostId;
import databasePart1.DBConnectionStore;
import databasePart1.IDatabaseHelper;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class QuestionDetailViewController {
    private final IDatabaseHelper databaseHelper;
    private final DBConnectionStore dBConnectionStore;
	private final QuestionDetailView view;
    private final Question question;
    private final Questions questions;
    private final Answers answersRepo;
    private final Stage primaryStage;
	private String currentUser;

    public QuestionDetailViewController(
            DBConnectionStore dBConnectionStore,
            Stage primaryStage, 
            QuestionDetailView view,
            Question question,
            Questions questions,
            Answers answersRepo,
            String currentUser
    ) {
        this.databaseHelper = dBConnectionStore.getDatabaseHelper();
        this.dBConnectionStore = dBConnectionStore;
		this.view = view;
        this.question = question;
        this.answersRepo = answersRepo;
        this.primaryStage = primaryStage;
		this.questions = questions;
		this.currentUser = currentUser;
        wire();
        refreshAnswers();
    }

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
            Answer a = new Answer(
                databaseHelper.getNextForumPostId(), // Assuming a method to get next ID
                question.getId(),
                text,
                true, // adding this to match the constructor, probably need to check correctness here in reality?
                currentUser

            );
            answersRepo.addAnswer(a);
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
                answersRepo.deleteAnswer(sel.getId());
                refreshAnswers();
            }
        });

        // Back to ForumListView (new scene, same as other buttons)
        view.getBackButton().setOnAction(e -> {
            ForumListView forumView = new ForumListView();
            ForumListViewController forumController =
                new ForumListViewController(dBConnectionStore, forumView, questions, answersRepo, primaryStage, currentUser);

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
    	view.getAnswerList().setItems(
    			FXCollections.observableArrayList(answersRepo.getAll()));
    }
}
