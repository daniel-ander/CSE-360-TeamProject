package UI.Pages;

import UI.Elements.PostCell;
import UI.Interfaces.IInitializable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.function.Consumer;

import Controllers.ForumPage.ForumHeaderViewModel;
import Controllers.ForumPage.QuestionPaneViewModel;
import Controllers.ForumPage.PostDisplayViewModel;
/**
 * This page displays a simple welcome message for the user.
 */

public class ReviewerHomePage implements IInitializable {

	private final BorderPane root = new BorderPane();
	
	private final Label userLabel = new Label();
	private final Button logoutButton = new Button("Logout");
	private final Button changeRolesButton = new Button("Change Roles");
	private final Button enterMessagesButton = new Button("Messages Page");
	private final Button enterForumButton = new Button("Enter Forum");
    private final TextField searchQuestionsTextField = new TextField();
    private final Button filterQuestionsButton = new Button("Search");
	
	private final ObservableList<PostDisplayViewModel> viewModels = FXCollections.observableArrayList();
	private final ListView<PostDisplayViewModel> reviewerListView = new ListView<>(viewModels);
	
	private final ForumHeaderViewModel reviewerHeaderViewModel;
	private final QuestionPaneViewModel questionPaneViewModel;
	
	private Consumer<int[]> getVisibleQuestionsCallback;
	
	// ===================
	// INITIALIZATION
	// ===================
	
	public ReviewerHomePage(ForumHeaderViewModel reviewerHeaderViewModel, QuestionPaneViewModel questionPaneViewModel) {
		this.reviewerHeaderViewModel = reviewerHeaderViewModel;
		this.questionPaneViewModel = questionPaneViewModel;
		bindViewModelProperties();
		
		filterQuestionsButton.setOnMouseClicked(_ -> reviewerHeaderViewModel.triggerPostsFilter());
		
		searchQuestionsTextField.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) {
				reviewerHeaderViewModel.triggerPostsFilter(); 
			}
		});
		
    }
    public void setInitialLayout(Stage primaryStage) {
    	Region spacer = new Region();
    	HBox header = new HBox();
    	HBox.setHgrow(spacer, Priority.ALWAYS);
    	
    	header.getChildren().addAll(logoutButton, changeRolesButton, enterMessagesButton, enterForumButton, spacer);
		header.setStyle("-fx-padding: 10; -fx-alignment: center-left;");
		root.setTop(header);
    	
		reviewerListView.setCellFactory(_ -> new PostCell());
		
		searchQuestionsTextField.setPromptText("Filter questions...");
    	
		BorderPane centerPane = new BorderPane();
		centerPane.setCenter(reviewerListView);
    	
		root.setCenter(centerPane);
		
		Scene scene = new Scene(root, 800, 400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Reviewer Home Page");
		primaryStage.show();
		
		setupScrollVisibilityCallback();
    	
    }
    
    private void setupScrollVisibilityCallback() {
        reviewerListView.skinProperty().addListener((_, _, newSkin) -> {
            if (newSkin == null) return;

            Platform.runLater(() -> {
                ScrollBar verticalBar = (ScrollBar) reviewerListView.lookup(".scroll-bar:vertical");
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
        if (!(reviewerListView.getSkin() instanceof ListViewSkin<?> skin)) return null;
        if (skin.getChildren().isEmpty()) return null;

        VirtualFlow<?> flow = (VirtualFlow<?>) skin.getChildren().get(0);
        if (flow == null) return null;

        IndexedCell<?> first = flow.getFirstVisibleCell();
        IndexedCell<?> last = flow.getLastVisibleCell();
        if (first == null || last == null) return null;

        return new int[]{first.getIndex(), last.getIndex()};
    }
	
	
	
	
	// ===================
    // 		 GETTERS 
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
    
    public ListView<PostDisplayViewModel> getReviewerListView() {
		return reviewerListView;
	}
    
    // ===================
    // 		 SETTERS 
 	// ===================
    
    public void setUserLabel(String username) {
        userLabel.setText("Hello, " + username + "!");
    }

    public void setGetVisibleQuestionIDsCallback(Consumer<int[]> callback) {
        this.getVisibleQuestionsCallback = callback;
    }
    
    private void bindViewModelProperties() {
    	searchQuestionsTextField.textProperty().bindBidirectional(reviewerHeaderViewModel.getSearchFieldTextProperty());
    }
}