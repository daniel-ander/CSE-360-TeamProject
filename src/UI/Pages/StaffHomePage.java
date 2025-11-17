// StaffHomePage.java
package UI.Pages;

import UI.Elements.PostCell;
import UI.Interfaces.IInitializable;
import Controllers.ForumPage.ForumHeaderViewModel;
import Controllers.ForumPage.QuestionPaneViewModel;
import Controllers.ForumPage.PostDisplayViewModel;
import Models.User;
import Models.Question;
import Models.Answer;
import Models.Message;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.control.IndexedCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.function.Consumer;

/**
 * This page is the home page for staff users.
 */
public class StaffHomePage implements IInitializable {

	private final BorderPane root = new BorderPane();
	
	public final Label userLabel = new Label();
	public final Button logoutButton = new Button("Logout");
	public final Button changeRolesButton = new Button("Change Roles");
	public final Button enterMessagesButton = new Button("Messages Page");
	public final Button enterForumButton = new Button("Enter Forum");
	public final TextField searchQuestionsTextField = new TextField();
	public final Button filterQuestionsButton = new Button("Search");
	public final Button flagButton = new Button("Flag Content");
	
	private final ObservableList<PostDisplayViewModel> viewModels = FXCollections.observableArrayList();
	private final ListView<PostDisplayViewModel> staffListView = new ListView<>(viewModels);
	
	private final ForumHeaderViewModel staffHeaderViewModel;
	private final QuestionPaneViewModel questionPaneViewModel;

	public final ListView<String> userListView = new ListView<>();
	public final Label selectedUserLabel = new Label("No user selected");
	
	public final ListView<Question> questionsListView = new ListView<>();
	public final ListView<Answer> answersListView = new ListView<>();
	public final ListView<Message> messagesListView = new ListView<>();
	public final ListView<String> notesListView = new ListView<>();
	
	public final TextField noteInputField = new TextField();
	public final TextArea contentViewerTextArea = new TextArea();
	public final Button addNoteButton = new Button("Add Note");
	
	private Consumer<int[]> getVisibleQuestionsCallback;
	
	// ===================
	// INITIALIZATION
	// ===================
	
/**
 * Creates a new StaffHomePage.
 */
	public StaffHomePage(ForumHeaderViewModel staffHeaderViewModel, QuestionPaneViewModel questionPaneViewModel) {
		this.staffHeaderViewModel = staffHeaderViewModel;
		this.questionPaneViewModel = questionPaneViewModel;
		bindViewModelProperties();
		
		filterQuestionsButton.setOnMouseClicked(_ -> staffHeaderViewModel.triggerPostsFilter());
		
		searchQuestionsTextField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				staffHeaderViewModel.triggerPostsFilter(); 
			}
		});
	}
/*
 * Sets up the initial layout of the Staff Home Page.
 */
	public void setInitialLayout(Stage primaryStage) {
		Region spacer = new Region();
		HBox header = new HBox();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		
		header.getChildren().addAll(userLabel, logoutButton, changeRolesButton, enterMessagesButton, enterForumButton, spacer);
		header.setStyle("-fx-padding: 10; -fx-alignment: center-left;");
		root.setTop(header);
		
		staffListView.setCellFactory(_ -> new PostCell());
		
		searchQuestionsTextField.setPromptText("Filter questions...");
		
		BorderPane centerPane = new BorderPane();
		centerPane.setCenter(staffListView);
		
		// Left side for the user list
		VBox leftSideVBox = new VBox(10);
		leftSideVBox.setPadding(new Insets(10));
		Label userListLabel = new Label("Users:");
		leftSideVBox.getChildren().addAll(userListLabel, userListView);
		leftSideVBox.setPrefWidth(200);
		
		// CENTER for the activity tabs
		VBox centerActivityVBox = new VBox(10);
		centerActivityVBox.setPadding(new Insets(10));
		TabPane activityTabPane = new TabPane();
		
		Tab questionsTab = new Tab("Questions", questionsListView);
		Tab answersTab = new Tab("Answers", answersListView);
		Tab messagesTab = new Tab("Messages", messagesListView);
		
		// Staff notes
		VBox staffNotesVBox = new VBox(10);
		staffNotesVBox.setPadding(new Insets(10));
		HBox noteInputHBox = new HBox(10);
		noteInputHBox.getChildren().addAll(noteInputField, addNoteButton);
		staffNotesVBox.getChildren().addAll(notesListView, noteInputHBox);
		Tab notesTab = new Tab("Staff Notes", staffNotesVBox);
		
		activityTabPane.getTabs().addAll(questionsTab, answersTab, messagesTab, notesTab);
		centerActivityVBox.getChildren().addAll(selectedUserLabel, activityTabPane);
		
		// RIGHT SIDE
		VBox rightSideVBox = new VBox(10);
		rightSideVBox.setPadding(new Insets(10));
		rightSideVBox.setPrefWidth(200);
		
		Label contentViewerLabel = new Label("ContentView");
		contentViewerTextArea.setWrapText(true);
		contentViewerTextArea.setEditable(false);
		
		rightSideVBox.getChildren().addAll(contentViewerLabel, contentViewerTextArea, flagButton);
		
		// Main layout
		BorderPane staffLayout = new BorderPane();
		staffLayout.setLeft(leftSideVBox);
		staffLayout.setCenter(centerActivityVBox);
		staffLayout.setRight(rightSideVBox);
		
		root.setCenter(staffLayout);
		
		Scene scene = new Scene(root, 800, 400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Staff Home Page");
		primaryStage.show();
		
		setupScrollVisibilityCallback();
	}
	
/** Sets up a callback to get visible question indices when scrolling occurs.
 */
	
	private void setupScrollVisibilityCallback() {
		staffListView.skinProperty().addListener((_, _, newSkin) -> {
			if (newSkin == null) return;
			
			Platform.runLater(() -> {
				ScrollBar verticalBar = (ScrollBar) staffListView.lookup(".scroll-bar:vertical");
				if (verticalBar == null) return;
				
				verticalBar.valueProperty().addListener((_, _, _) -> {
					int[] visibleIndices = getVisibleIndices();
					if (visibleIndices == null || getVisibleQuestionsCallback == null) return;
					getVisibleQuestionsCallback.accept(visibleIndices);
				});
			});
		});
	}
	
	private int[] getVisibleIndices() {
		if (!(staffListView.getSkin() instanceof ListViewSkin<?> skin)) return null;
		if (skin.getChildren().isEmpty()) return null;
		
		VirtualFlow<?> flow = (VirtualFlow<?>) skin.getChildren().get(0);
		if (flow == null) return null;
		
		IndexedCell<?> first = flow.getFirstVisibleCell();
		IndexedCell<?> last = flow.getLastVisibleCell();
		if (first == null || last == null) return null;
		
		return new int[]{first.getIndex(), last.getIndex()};
	}
	
	// ===================
	// GETTERS
	// ===================
	
	public Button getLogoutButton() {
		return logoutButton;
	}
	
	public Button getChangeRolesButton() {
		return changeRolesButton;
	}
	
	public Button getEnterForumButton() {
		return enterForumButton;
	}
	
	public Button getEnterMessagesPageButton() {
		return enterMessagesButton;
	}
	
	public ListView<PostDisplayViewModel> getStaffListView() {
		return staffListView;
	}
	
	// ===================
	// SETTERS
	// ===================
	
	public void setUserLabel(String username) {
		userLabel.setText("Hello, " + username + "!");
	}
	
	public void setGetVisibleQuestionIDsCallback(Consumer<int[]> callback) {
		this.getVisibleQuestionsCallback = callback;
	}
	
	private void bindViewModelProperties() {
		searchQuestionsTextField.textProperty().bindBidirectional(staffHeaderViewModel.getSearchFieldTextProperty());
	}
}
