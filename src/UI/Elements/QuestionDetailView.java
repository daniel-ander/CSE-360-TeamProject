package UI.Elements;

import Models.Answer;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class QuestionDetailView {
    private final BorderPane root = new BorderPane();
    private final Label questionLabel = new Label();
    private final ListView<Answer> answerList = new ListView<>();
    private final Button addAnswerBtn = new Button("Add Answer");
    private final Button editAnswerBtn = new Button("Edit Answer");
    private final Button deleteAnswerBtn = new Button("Delete Answer");
    private final Button backBtn = new Button("Back");
    private final TextArea textInputArea = new TextArea("Input text here...");
    private final Button resolveBtn = new Button("Question Resolved");

    public QuestionDetailView() {
        VBox topBox = new VBox(10, questionLabel);
        topBox.setStyle("-fx-padding: 10; -fx-font-size: 16px; -fx-font-weight: bold;");
        root.setTop(topBox);

        root.setCenter(answerList);

        VBox controls = new VBox(8, addAnswerBtn, resolveBtn, editAnswerBtn, deleteAnswerBtn, backBtn);
        controls.setStyle("-fx-padding: 10;");
        textInputArea.setWrapText(true);
        root.setRight(controls);
        root.setBottom(textInputArea);
    }

    public BorderPane getLayout() { return root; }
    public Label getQuestionLabel() { return questionLabel; }
    public ListView<Answer> getAnswerList() { return answerList; }
    public Button getAddAnswerBtn() { return addAnswerBtn; }
    public Button getEditAnswerBtn() { return editAnswerBtn; }
    public Button getDeleteAnswerBtn() { return deleteAnswerBtn; }
    public Button getBackBtn() { return backBtn; }
    public TextArea getTextInputArea() { return textInputArea; }
    public Button getResolveBtn() { return resolveBtn; }
}
