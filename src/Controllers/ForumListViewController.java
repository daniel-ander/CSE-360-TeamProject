package Controllers;

import Models.Question;
import Models.QuestionRepository;
import Models.AnswerRepository;
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
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final Stage primaryStage;
    private final String currentUser;

    public ForumListViewController(DBConnectionStore dBConnectionStore, ForumListView view, QuestionRepository questionRepository, AnswerRepository answerRepository, Stage primaryStage, String currentUser) {
        this.view = view;
        this.databaseHelper = dBConnectionStore.getDatabaseHelper();
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
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
            questionRepository.addQuestion(q);
            refresh();
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
                // Gonna have to pass the answers here too somehow
                questionRepository.deleteQuestion(selectedQuestion.getId(), answerRepository);
                refresh();
            }
        });

        // View question → switch to detail page
        view.getViewQuestionButton().setOnAction(e -> {
            Question selectedQuestion = view.getForumListView().getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                QuestionDetailView detailView = new QuestionDetailView();


                new QuestionDetailViewController(
					dBConnectionStore, // DBConnectionStore not needed here
					primaryStage,
					detailView,
					selectedQuestion,
					questionRepository,
					answerRepository,
					currentUser
				);

                primaryStage.getScene().setRoot(detailView.getLayout());
                System.out.println("Viewing details for Question ID: " + selectedQuestion.getId());
            }
        });
    }

    private void refresh() {
        view.getForumListView().setItems(
            FXCollections.observableArrayList(questionRepository.getAll())
        );
    }
}
