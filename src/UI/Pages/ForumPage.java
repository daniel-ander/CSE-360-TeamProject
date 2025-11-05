package UI.Pages;

import Controllers.PostDisplayViewModel;
import UI.Elements.PostCell;
import UI.Interfaces.IInitializable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class ForumPage implements IInitializable {

    private final BorderPane root = new BorderPane();

    private final Label userLabel = new Label();
    private final Button logoutButton = new Button("Logout");
    private final Button changeRolesButton = new Button("Change Roles");

    // Center UI components (formerly QuestionListView)
    private final ObservableList<PostDisplayViewModel> viewModels = FXCollections.observableArrayList();
    private final ListView<PostDisplayViewModel> forumListView = new ListView<>(viewModels);

    private final TextArea textInputArea = new TextArea("Input text here...");
    private final TextField searchQuestionsTextField = new TextField();
    private final Button searchQuestionsButton = new Button("Search");

    private Consumer<int[]> getVisibleQuestionsCallback;

    // ===================
    // INITIALIZATION
    // ===================

    public void setInitialLayout(Stage primaryStage) {
    	Region spacer = new Region();
    	HBox header = new HBox(10);
    	HBox.setHgrow(spacer, Priority.ALWAYS);
    	
    	header.getChildren().addAll(logoutButton, changeRolesButton, spacer, searchQuestionsTextField, searchQuestionsButton);
        header.setStyle("-fx-padding: 10; -fx-alignment: center-left;");
        root.setTop(header);

        forumListView.setCellFactory(_ -> new PostCell());

        searchQuestionsTextField.setPromptText("Search questions...");
        textInputArea.setWrapText(true);

        BorderPane centerPane = new BorderPane();
        centerPane.setCenter(forumListView);
        centerPane.setBottom(textInputArea);

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

    public Button getSearchButton() { return searchQuestionsButton; }
    
    // ===================
    // SETTERS
    // ===================

    public void setUserLabel(String username) {
        userLabel.setText("Hello, " + username + "!");
    }

    public void setGetVisibleQuestionIDsCallback(Consumer<int[]> callback) {
        this.getVisibleQuestionsCallback = callback;
    }
}
