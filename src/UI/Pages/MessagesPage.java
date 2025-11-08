package UI.Pages;

import UI.Elements.RecentInteractionsCell;
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
import java.util.function.Consumer;

import Controllers.ForumPage.ForumHeaderViewModel;
import Controllers.ForumPage.QuestionPaneViewModel;
import Controllers.Messages.RecentInteractionsViewModel;

public class MessagesPage implements IInitializable {

    private final BorderPane root = new BorderPane();

    private final Button logoutButton = new Button("Logout");
    private final Button changeRolesButton = new Button("Change Roles");
    private final Button enterMessagesButton = new Button("Messages Page");
    private final Button enterForumButton = new Button("Enter Forum");
    private final TextField searchMessagesTextField = new TextField();
    private final Button filterMessagesButton = new Button("Filter Messages");

    private final ObservableList<RecentInteractionsViewModel> viewModels = FXCollections.observableArrayList();
    private final ListView<RecentInteractionsViewModel> recentInteractionsListView = new ListView<>(viewModels);

    private final ForumHeaderViewModel messagesHeaderViewModel;
    private final QuestionPaneViewModel messagesPaneViewModel;

    private Consumer<int[]> getVisibleMessagesCallback;

    public MessagesPage(ForumHeaderViewModel messagesHeaderViewModel, QuestionPaneViewModel messagesPaneViewModel) {
        this.messagesHeaderViewModel = messagesHeaderViewModel;
        this.messagesPaneViewModel = messagesPaneViewModel;
        bindViewModelProperties();

        filterMessagesButton.setOnMouseClicked(_ -> messagesHeaderViewModel.triggerPostsFilter());
        searchMessagesTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) messagesHeaderViewModel.triggerPostsFilter();
        });
    }

    public void setInitialLayout(Stage primaryStage) {
        // top header
        Region spacer = new Region();
        HBox header = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(logoutButton, changeRolesButton, enterMessagesButton, enterForumButton, spacer);
        header.setStyle("-fx-padding: 10; -fx-alignment: center-left;");
        root.setTop(header);

        // left: recent interactions
        recentInteractionsListView.setCellFactory(_ -> new RecentInteractionsCell());
        recentInteractionsListView.setPrefWidth(200);
        root.setLeft(recentInteractionsListView);

        // right: placeholder for future messages/questions area
        VBox rightPane = new VBox();
        rightPane.setPrefWidth(600);
        rightPane.setStyle("-fx-background-color: #f4f4f4; -fx-alignment: center; -fx-padding: 10;");
        Label placeHolderLabel = new Label("Something happens here");
        rightPane.getChildren().add(placeHolderLabel);
        root.setCenter(rightPane);

        // scene setup
        Scene scene = new Scene(root, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Messages Home Page");
        primaryStage.show();

        setupScrollVisibilityCallback();
    }

    private void setupScrollVisibilityCallback() {
        recentInteractionsListView.skinProperty().addListener((_, _, newSkin) -> {
            if (newSkin == null) return;
            Platform.runLater(() -> {
                ScrollBar verticalBar = (ScrollBar) recentInteractionsListView.lookup(".scroll-bar:vertical");
                if (verticalBar == null) return;
                verticalBar.valueProperty().addListener((_, _, _) -> {
                    int[] visibleIndices = getVisibleIndices();
                    if (visibleIndices != null && getVisibleMessagesCallback != null)
                        getVisibleMessagesCallback.accept(visibleIndices);
                });
            });
        });
    }

    private int[] getVisibleIndices() {
        if (!(recentInteractionsListView.getSkin() instanceof ListViewSkin<?> skin)) return null;
        if (skin.getChildren().isEmpty()) return null;
        VirtualFlow<?> flow = (VirtualFlow<?>) skin.getChildren().get(0);
        if (flow == null) return null;
        IndexedCell<?> first = flow.getFirstVisibleCell();
        IndexedCell<?> last = flow.getLastVisibleCell();
        if (first == null || last == null) return null;
        return new int[]{first.getIndex(), last.getIndex()};
    }

    // GETTERS
    public Button getLogoutButton() { return logoutButton; }
    public Button getChangeRolesButton() { return changeRolesButton; }
    public Button getEnterForumButton() { return enterForumButton; }
    public ListView<RecentInteractionsViewModel> getMessagesListView() { return recentInteractionsListView; }

    // SETTERS
    public void setGetVisibleMessageIDsCallback(Consumer<int[]> callback) { this.getVisibleMessagesCallback = callback; }

    private void bindViewModelProperties() {
        searchMessagesTextField.textProperty().bindBidirectional(messagesHeaderViewModel.getSearchFieldTextProperty());
    }
}
