package UI.Elements;

import java.util.function.Consumer;

import Controllers.PostDisplayViewModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
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

	private Consumer<int[]> getVisibleQuestionsCallback;

	public QuestionListView() {
		forumListView.setCellFactory(_ -> new PostCell());
		borderPane.setCenter(forumListView);

		searchQuestionsTextField.setPromptText("Search questions...");
		textInputArea.setWrapText(true);

		VBox controlBox = new VBox(8, addQuestionButton, viewQuestionButton, editQuestionButton, deleteQuestionButton,
				showDeletedQuestionsCheckbox, returnButton, searchQuestionsTextField, searchQuestionsButton);
		borderPane.setRight(controlBox);
		borderPane.setBottom(textInputArea);

		// Wait for skin to be ready
		forumListView.skinProperty().addListener((_, _, newSkin) -> {
			if (newSkin == null)
				return;

			Platform.runLater(() -> {
				ScrollBar verticalBar;
				
				try {
					verticalBar = (ScrollBar) forumListView.lookup(".scroll-bar:vertical");
					if(verticalBar == null)
						throw new Exception("No scroll bar found.");
				} catch(Exception e) {
					System.out.println(e.getLocalizedMessage());
					return;
				}
				
				verticalBar.valueProperty().addListener((_, _, _) -> {
					int[] visibleIndices = getVisibleIndices();
					if (visibleIndices == null || getVisibleQuestionsCallback == null)
						return;
					getVisibleQuestionsCallback.accept(visibleIndices);
				});
			});
		});
	}

	// Helper to get first/last visible indices
	private int[] getVisibleIndices() {
		if (!(forumListView.getSkin() instanceof ListViewSkin<?> skin))
			return null;
		if (skin.getChildren().isEmpty())
			return null;

		VirtualFlow<?> virtualFlow = (VirtualFlow<?>) skin.getChildren().get(0);
		if (virtualFlow == null)
			return null;

		IndexedCell<?> firstCell = virtualFlow.getFirstVisibleCell();
		IndexedCell<?> lastCell = virtualFlow.getLastVisibleCell();

		if (firstCell == null || lastCell == null)
			return null;

		return new int[] { firstCell.getIndex(), lastCell.getIndex() };
	}

	public void setGetVisibleQuestionIDsCallback(Consumer<int[]> callback) {
		this.getVisibleQuestionsCallback = callback;
	}

	// ===================
	// GETTERS
	// ===================
	public BorderPane getLayout() {
		return borderPane;
	}

	public ListView<PostDisplayViewModel> getForumListView() {
		return forumListView;
	}

	public Button getAddQuestionButton() {
		return addQuestionButton;
	}

	public Button getEditQuestionButton() {
		return editQuestionButton;
	}

	public Button getDeleteQuestionButton() {
		return deleteQuestionButton;
	}

	public Button getViewQuestionButton() {
		return viewQuestionButton;
	}

	public Button getReturnButton() {
		return returnButton;
	}

	public TextField getSearchQuestionsTextField() {
		return searchQuestionsTextField;
	}

	public TextArea getTextInputArea() {
		return textInputArea;
	}

	public Button getSearchButton() {
		return searchQuestionsButton;
	}

	public CheckBox getShowDeletedQuestionsCheckbox() {
		return showDeletedQuestionsCheckbox;
	}
}
