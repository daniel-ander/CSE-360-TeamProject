package UI.Elements;

import Models.Answer;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class QuestionDetailView {
    private final BorderPane root = new BorderPane();
    private final Label questionLabel = new Label();
    private final ListView<Answer> answerList = new ListView<>();
    private final Button addAnswerButton = new Button("Add Answer");
    private final Button editAnswerButton = new Button("Edit Answer");
    private final Button deleteAnswerButton = new Button("Delete Answer");
    private final Button backButton = new Button("Back");
    private final TextArea textInputArea = new TextArea("Input text here...");
    private final Button resolveButton = new Button("Question Resolved");

    public QuestionDetailView() {
        VBox topBox = new VBox(10, questionLabel);
        topBox.setStyle("-fx-padding: 10; -fx-font-size: 16px; -fx-font-weight: bold;");
        root.setTop(topBox);

        root.setCenter(answerList);

        VBox controls = new VBox(8, addAnswerButton, resolveButton, editAnswerButton, deleteAnswerButton, backButton);
        controls.setStyle("-fx-padding: 10;");
        textInputArea.setWrapText(true);
        root.setRight(controls);
        root.setBottom(textInputArea);
    }

    public BorderPane getLayout() { return root; }
    public Label getQuestionLabel() { return questionLabel; }
    public ListView<Answer> getAnswerList() { return answerList; }
    public Button getAddAnswerButton() { return addAnswerButton; }
    public Button getEditAnswerButton() { return editAnswerButton; }
    public Button getDeleteAnswerButton() { return deleteAnswerButton; }
    public Button getBackButton() { return backButton; }
    public TextArea getTextInputArea() { return textInputArea; }
    public Button getResolveButton() { return resolveButton; }
}
