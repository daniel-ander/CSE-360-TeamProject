package UI.Elements;

import Controllers.PostDisplayViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;


public class QuestionListView {
	
    private final BorderPane borderPane = new BorderPane();
    
    private final ObservableList<PostDisplayViewModel> viewModels = FXCollections.observableArrayList();
    private final ListView<PostDisplayViewModel> forumListView = new ListView<>(viewModels);
    
    private final Button addQuestionButton = new Button("Add Question");
    private final Button editQuestionButton = new Button("Edit Question");
    private final Button deleteQuestionButton = new Button("Delete Question");
    private final Button returnButton = new Button("Back");
	private final Button viewQuestionButton = new Button("View Question Details");
	private final CheckBox showDeletedQuestionsCheckbox = new CheckBox("Show Deleted Questions");
	private final TextArea textInputArea = new TextArea("Input text here...");
	private final TextField searchQuestionsTextField = new TextField();
	private final Button searchQuestionsButton = new Button("Search");

    public QuestionListView() {
        forumListView.setCellFactory(_ -> new PostCell());
        
        borderPane.setCenter(forumListView);
        
        searchQuestionsTextField.setPromptText("Search questions...");

        textInputArea.setWrapText(true);
        
        VBox controlBox = new VBox(8, 
        		addQuestionButton, 
        		viewQuestionButton, 
        		editQuestionButton, 
        		deleteQuestionButton, 
        		showDeletedQuestionsCheckbox, 
        		returnButton, 
        		searchQuestionsTextField, 
        		searchQuestionsButton);
        borderPane.setRight(controlBox);
        borderPane.setBottom(textInputArea);
    }

    public BorderPane getLayout() { return borderPane; }
    
    public ListView<PostDisplayViewModel> getForumListView() { return forumListView; }
    
    public Button getAddQuestionButton() { return addQuestionButton; }
    public Button getEditQuestionButton() { return editQuestionButton; }
    public Button getDeleteQuestionButton() { return deleteQuestionButton; }
    public Button getViewQuestionButton() { return viewQuestionButton; }
    public Button getReturnButton() { return returnButton; }
    
    public TextField getSearchQuestionsTextField() { return searchQuestionsTextField; } 
    public TextArea getTextInputArea() { return textInputArea; }
    public Button getSearchButton() { return searchQuestionsButton; }

	public CheckBox getShowDeletedQuestionsCheckbox() {return showDeletedQuestionsCheckbox;}

}
