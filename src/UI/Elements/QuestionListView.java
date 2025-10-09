package UI.Elements;

import Models.Question;

import javafx.scene.control.*;
import javafx.scene.layout.*;


public class QuestionListView {
	
    private final BorderPane borderpane = new BorderPane();
    
    private final ListView<Question> forumListView;
    
    private final Button addQuestionButton = new Button("Add Question");
    private final Button editQuestionButton = new Button("Edit Question");
    private final Button deleteQuestionButton = new Button("Delete Question");
    private final Button returnButton = new Button("Back");
	private final Button viewQuestionButton = new Button("View Question Details");
	
	private final TextArea textInputArea = new TextArea("Input text here...");

    public QuestionListView() {
        // ListView in center
        forumListView = new ListView<>();
        borderpane.setCenter(forumListView);

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
