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

public class ForumPage implements IInitializable {

    private final BorderPane root = new BorderPane();

    private final Label userLabel = new Label();
    private final Button logoutButton = new Button("Logout");
    private final Button changeRolesButton = new Button("Change Roles");
    private final Button seeMyReviewsButton = new Button("See My Reviews");

    // Center UI components (formerly QuestionListView)
    private final ObservableList<PostDisplayViewModel> viewModels = FXCollections.observableArrayList();
    private final ListView<PostDisplayViewModel> forumListView = new ListView<>(viewModels);
    
    private final ForumHeaderViewModel forumHeaderViewModel;
    private final QuestionPaneViewModel questionPaneViewModel;

    private final TextArea textInputArea = new TextArea();
    private final Button addQuestionButton = new Button("Add Question");
    private final TextField searchQuestionsTextField = new TextField();
    private final Button filterQuestionsButton = new Button("Search");

    private Consumer<int[]> getVisibleQuestionsCallback;

    // ===================
    // INITIALIZATION
    // ===================
    
    public ForumPage(ForumHeaderViewModel forumHeaderViewModel, QuestionPaneViewModel questionPaneViewModel) {
    	this.forumHeaderViewModel = forumHeaderViewModel;
    	this.questionPaneViewModel = questionPaneViewModel;
    	
    	bindViewModelProperties();
    	
    	filterQuestionsButton.setOnMouseClicked(_ -> forumHeaderViewModel.triggerPostsFilter());
    	
    	searchQuestionsTextField.setOnKeyPressed(e -> {
    		if(e.getCode() == KeyCode.ENTER) {
    			forumHeaderViewModel.triggerPostsFilter(); 
    		}
    	});
    	
    	seeMyReviewsButton.setOnMouseClicked(_ -> {
    		forumHeaderViewModel.setSeeReviewsButtonPressedProperty(!forumHeaderViewModel.getSeeReviewsButtonPressedProperty().get());
    	});
    	
    	forumHeaderViewModel.getSeeReviewsButtonPressedProperty().addListener((_, _, isPressed) -> {
    		if(isPressed) {
    			seeMyReviewsButton.setText("See All Posts");
    			seeMyReviewsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
    		} else {
    			seeMyReviewsButton.setText("See My Reviews");
    			seeMyReviewsButton.setStyle("");
    		}
    	});
    	
    	addQuestionButton.setOnAction(_ -> showAddQuestionDialog());
    }
    
    private void showAddQuestionDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add Question");
        dialog.setHeaderText("Enter question details");

        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        // Create content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Question body");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Body:"), 0, 1);
        grid.add(bodyArea, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return new Pair<>(titleField.getText(), bodyArea.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            questionPaneViewModel.submitNewQuestion(result.getKey(), result.getValue());
        });
    }


    public void setInitialLayout(Stage primaryStage) {
    	Region spacer = new Region();
    	HBox header = new HBox(10);
    	HBox.setHgrow(spacer, Priority.ALWAYS);
    	
    	header.getChildren().addAll(logoutButton, changeRolesButton, spacer, addQuestionButton, searchQuestionsTextField, filterQuestionsButton);
    	
    	if(forumHeaderViewModel.isReviewer()) {
    		header.getChildren().add(3, seeMyReviewsButton);
    	}
    	
        header.setStyle("-fx-padding: 10; -fx-alignment: center-left;");
        root.setTop(header);

        forumListView.setCellFactory(_ -> new PostCell());

        searchQuestionsTextField.setPromptText("Search questions...");
        textInputArea.setWrapText(true);
        textInputArea.setPromptText("Input text here...");

        BorderPane centerPane = new BorderPane();
        centerPane.setCenter(forumListView);
//        centerPane.setBottom(textInputArea);

        root.setCenter(centerPane);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Forum Page");
        primaryStage.show();

        setupScrollVisibilityCallback();
    }

    private void setupScrollVisibilityCallback() {
        forumListView.skinProperty().addListener((_, _, newSkin) -> {
            if (newSkin == null) return;

            Platform.runLater(() -> {
                ScrollBar verticalBar = (ScrollBar) forumListView.lookup(".scroll-bar:vertical");
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
        if (!(forumListView.getSkin() instanceof ListViewSkin<?> skin)) return null;
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

    public BorderPane getRoot() { return root; }

    public Button getLogoutButton() { return logoutButton; }

    public Button getChangeRolesButton() { return changeRolesButton; }
    
    

    public ListView<PostDisplayViewModel> getForumListView() { return forumListView; }

    public TextField getSearchQuestionsTextField() { return searchQuestionsTextField; }

    public TextArea getTextInputArea() { return textInputArea; }

    public Button getSearchButton() { return filterQuestionsButton; }
    
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
    	textInputArea.textProperty().bindBidirectional(questionPaneViewModel.getTextInputProperty());
    	searchQuestionsTextField.textProperty().bindBidirectional(forumHeaderViewModel.getSearchFieldTextProperty());
    }
}