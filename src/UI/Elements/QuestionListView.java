package UI.Elements;

import Controllers.QuestionDisplayViewModel;


import javafx.scene.control.*;
import javafx.scene.layout.*;


public class QuestionListView {
	
    private final BorderPane borderpane = new BorderPane();
    
    private final ListView<QuestionDisplayViewModel> forumListView;
    
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
        // ListView in center
        forumListView = new ListView<QuestionDisplayViewModel>();
        
        //XXX: working on this, could break. trying to add cells to the UI 
        forumListView.setCellFactory(_-> new QuestionCell());
        
        borderpane.setCenter(forumListView);
        
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
        borderpane.setRight(controlBox);
        borderpane.setBottom(textInputArea);
    }

    public BorderPane getLayout() { return borderpane; }
    
    public ListView<QuestionDisplayViewModel> getForumListView() { return forumListView; }
    
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
