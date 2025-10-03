package Controllers;

import Models.Answer;
import Models.Answers;
import Models.Question;
import Models.Questions;
import UI.Elements.ForumListView;
import UI.Elements.QuestionDetailView;
import Utils.GeneratePostId;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class QuestionDetailViewController {
    private final QuestionDetailView view;
    private final Question question;
    private final Questions questions;
    private final Answers answersRepo;
    private final Stage primaryStage;
	private String currentUser;

    public QuestionDetailViewController(
            QuestionDetailView view,
            Question question,
            Questions questions,
            Answers answersRepo,
            Stage primaryStage, 
            String currentUser
    ) {
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
        view.getAddAnswerBtn().setOnAction(e -> {
        	String text = view.getTextInputArea().getText();
        	if (text == null || text.trim().isEmpty() || text.length() > 500 || text.equals("Input text here...")) {
        		System.out.println("Answer text cannot be empty or placeholder.");
        		return;
        	}
            Answer a = new Answer(
                GeneratePostId.getNextId(),
                question.getId(),
                text,
                currentUser

            );
            answersRepo.addAnswer(a);
            refreshAnswers();
        });

        // Edit answer
        view.getEditAnswerBtn().setOnAction(e -> {
            Answer sel = view.getAnswerList().getSelectionModel().getSelectedItem();
            if (sel != null) {
                sel.updateText("Edited: " + sel.getText());
                refreshAnswers();
            }
        });

        // Delete answer
        view.getDeleteAnswerBtn().setOnAction(e -> {
            Answer sel = view.getAnswerList().getSelectionModel().getSelectedItem();
            if (sel != null) {
                answersRepo.deleteAnswer(sel.getId());
                refreshAnswers();
            }
        });

        // Back to ForumListView (new scene, same as other buttons)
        view.getBackBtn().setOnAction(e -> {
            ForumListView forumView = new ForumListView();
            ForumListViewController forumController =
                new ForumListViewController(forumView, questions, answersRepo, primaryStage, currentUser);

            Scene forumScene = new Scene(forumView.getLayout(), 800, 600);
            primaryStage.setScene(forumScene);
            primaryStage.setTitle("Forum Page");
        });
        // Mark question as resolved
        view.getResolveBtn().setOnAction(e -> {
			question.isResolved();
			view.getQuestionLabel().setText("[" + question.getId() + "] " + question.getText() + " (Resolved)");
			question.updateText(question.getText() + " (Resolved)");

			
		});


    }

    private void refreshAnswers() {
        view.getAnswerList().setItems(FXCollections.observableArrayList(
            answersRepo.getAnswersForQuestion(question.getId())
        ));
    }
}
