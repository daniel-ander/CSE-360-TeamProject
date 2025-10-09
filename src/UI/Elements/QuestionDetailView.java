package UI.Elements;

import Models.Answer;

import javafx.scene.control.*;
import javafx.scene.layout.*;

public class QuestionDetailView {
    private final BorderPane root = new BorderPane();
    
    private final Label questionLabel = new Label();
    
    private final ListView<Answer> answerListView;
    
    private final Button addAnswerButton = new Button("Add Answer");
    private final Button editAnswerButton = new Button("Edit Answer");
    private final Button deleteAnswerButton = new Button("Delete Answer");
    private final Button backButton = new Button("Back");
    private final Button resolveButton = new Button("Question Resolved");
    private final Button replyToAnswerButton = new Button("Reply to Answer");
    
    private final TextArea textInputArea = new TextArea("Input text here...");
    

    public QuestionDetailView() {
        VBox topBox = new VBox(10, questionLabel);
        topBox.getStyleClass().addAll("main-label", "small-padding");
        
        root.setTop(topBox);
        answerListView = new ListView<>();
        root.setCenter(answerListView);

        VBox controls = new VBox(8, addAnswerButton, resolveButton, editAnswerButton, deleteAnswerButton, backButton);
        controls.getStyleClass().add("small-padding");
        
        textInputArea.setWrapText(true);
        
        root.setRight(controls);
        root.setBottom(textInputArea);
    }

    public BorderPane getLayout() { return root; }
    
    public Label getQuestionLabel() { return questionLabel; }
    
    public ListView<Answer> getAnswerList() { return answerListView; }
    
    public Button getAddAnswerButton() { return addAnswerButton; }
    public Button getEditAnswerButton() { return editAnswerButton; }
    public Button getDeleteAnswerButton() { return deleteAnswerButton; }
    public Button getBackButton() { return backButton; }
    public Button getResolveButton() { return resolveButton; }
    public Button getReplyToAnswerButton() { return replyToAnswerButton; }
    
    public TextArea getTextInputArea() { return textInputArea; }
    
}
