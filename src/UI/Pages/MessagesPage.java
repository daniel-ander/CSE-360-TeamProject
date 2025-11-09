package UI.Pages;

import UI.Elements.MessageCell;
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
import Controllers.Messages.MessagesViewModel;
import Controllers.Messages.RecentInteractionsViewModel;
import Models.Message;

public class MessagesPage implements IInitializable {

    private final BorderPane root = new BorderPane();

    private final Button logoutButton = new Button("Logout");
    private final Button changeRolesButton = new Button("Change Roles");
    private final Button enterMessagesButton = new Button("Messages Page");
    private final Button enterForumButton = new Button("Enter Forum");
    private final TextField searchMessagesTextField = new TextField();
    private final Button filterMessagesButton = new Button("Filter Messages");
    private final TextArea messageTextArea = new TextArea();
    private final Button sendMessageButton = new Button("Send");

    private final ObservableList<RecentInteractionsViewModel> viewModels = FXCollections.observableArrayList();
    private final ListView<RecentInteractionsViewModel> recentInteractionsListView = new ListView<>(viewModels);
    
    private final ObservableList<MessagesViewModel> messagesViewModel = FXCollections.observableArrayList();

    private final ListView<MessagesViewModel> messagesListView = new ListView<>(messagesViewModel);

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

        // right: messages list
        messagesListView.setCellFactory(_ -> new MessageCell());
        messagesListView.setPrefWidth(600);
        

        messageTextArea.setPromptText("Write a reply...");
        messageTextArea.setWrapText(true);
        messageTextArea.setPrefRowCount(3);
        
        VBox messageInputBox = new VBox(5, messageTextArea, sendMessageButton);
        VBox messagesBox = new VBox(10, messagesListView, messageInputBox);

        HBox content = new HBox(recentInteractionsListView, messagesBox);
        root.setCenter(content);

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
    public TextArea getMessageTextArea() { return messageTextArea; }
    public Button getSendMessageButton() { return sendMessageButton; }
    public ListView<RecentInteractionsViewModel> getRecentInteractionsListView() { return recentInteractionsListView; }
    public ListView<MessagesViewModel> getMessagesListView() { return messagesListView; }
    

    // SETTERS
    public void setGetVisibleMessageIDsCallback(Consumer<int[]> callback) { this.getVisibleMessagesCallback = callback; }

    private void bindViewModelProperties() {
        searchMessagesTextField.textProperty().bindBidirectional(messagesHeaderViewModel.getSearchFieldTextProperty());
    }
}
