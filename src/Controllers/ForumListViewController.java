package Controllers;

import Models.Question;
import Models.Questions;
import Models.Answers;
import UI.Elements.ForumListView;
import UI.Elements.QuestionDetailView;
import databasePart1.DBConnectionStore;
import databasePart1.IDatabaseHelper;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

public class ForumListViewController {
	private final DBConnectionStore dBConnectionStore;
	private final IDatabaseHelper databaseHelper;
    private final ForumListView view;
    private final Questions questions;
    private final Answers answers;
    private final Stage primaryStage;
    private final String currentUser;

    public ForumListViewController(DBConnectionStore dBConnectionStore, ForumListView view, Questions questions, Answers answers, Stage primaryStage, String currentUser) {
        this.view = view;
        this.databaseHelper = dBConnectionStore.getDatabaseHelper();
        this.questions = questions;
        this.answers = answers;
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.dBConnectionStore = dBConnectionStore;
        wire();
        refresh();
    }

    private void wire() {
        // Add question
        view.getAddQuestionButton().setOnAction(e -> {
        	String text = view.getTextInputArea().getText();
        	if (text == null || text.trim().isEmpty() || text.length() > 500 || text.equals("Input text here...")) {
        		System.out.println("Question text cannot be empty or placeholder.");
        		return;
        	}
            Question q = new Question(
                databaseHelper.getNextForumPostId(), // Assuming a method to get next ID
                currentUser,
                text   
            );
            questions.addQuestion(q);
            refresh();
        });

        // Edit question
        view.getEditQuestionButton().setOnAction(e -> {
            Question sel = view.getForumListView().getSelectionModel().getSelectedItem();
            if (sel != null) {
                sel.setText("Edited: " + sel.getText());
                refresh();
            }
        });

        // Delete question

        view.getDeleteQuestionButton().setOnAction(e -> {
            Question sel = view.getForumListView().getSelectionModel().getSelectedItem();
            if (sel != null) {
                // Gonna have to pass the answers here too somehow
                questions.deleteQuestion(sel.getId(), answers);
                refresh();
            }
        });

        // View question → switch to detail page
        view.getViewQuestionButton().setOnAction(e -> {
            Question sel = view.getForumListView().getSelectionModel().getSelectedItem();
            if (sel != null) {
                QuestionDetailView detailView = new QuestionDetailView();

                // Define back action → return to the main forum list
                Runnable backAction = () -> primaryStage.getScene().setRoot(view.getLayout());

                new QuestionDetailViewController(
					dBConnectionStore, // DBConnectionStore not needed here
					primaryStage,
					detailView,
					sel,
					questions,
					answers,
					currentUser
				);

                primaryStage.getScene().setRoot(detailView.getLayout());
                System.out.println("Viewing details for Question ID: " + sel.getId());
            }
        });
    }

    private void refresh() {
        view.getForumListView().setItems(
            FXCollections.observableArrayList(questions.getAll())
        );
    }
}
