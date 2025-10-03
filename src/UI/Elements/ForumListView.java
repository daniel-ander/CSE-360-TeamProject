package UI.Elements;

import Models.Question;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ForumListView {
    private final BorderPane borderpane;
    private final ListView<Question> forumListView;
    private final Button addQuestionButton;
    private final Button editQuestionButton;
    private final Button deleteQuestionButton;
    private final Button returnButton;
	private final Button viewQuestionButton;
	private final TextArea textInputArea;

    public ForumListView() {
        borderpane = new BorderPane();

        // ListView in center
        forumListView = new ListView<>();
        borderpane.setCenter(forumListView);

        // Buttons
        addQuestionButton = new Button("Add Question");
        viewQuestionButton = new Button("View Question Details");
        editQuestionButton = new Button("Edit Question");
        deleteQuestionButton = new Button("Delete Question");
        returnButton = new Button("Back");
        textInputArea = new TextArea("Input text here...");

        textInputArea.setWrapText(true);
        
        VBox controlBox = new VBox(8, addQuestionButton, viewQuestionButton, editQuestionButton, deleteQuestionButton);
        borderpane.setRight(controlBox);
        borderpane.setBottom(textInputArea);
    }

    public BorderPane getLayout() { return borderpane; }
    public ListView<Question> getForumListView() { return forumListView; }
    public Button getAddQuestionButton() { return addQuestionButton; }
    public Button getEditQuestionButton() { return editQuestionButton; }
    public Button getDeleteQuestionButton() { return deleteQuestionButton; }
    public Button getViewQuestionButton() { return viewQuestionButton; }
    public Button getReturnButton() { return returnButton; }
    public TextArea getTextInputArea() { return textInputArea; }

}
