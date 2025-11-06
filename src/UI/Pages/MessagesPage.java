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

public class MessagesPage implements IInitializable {

	private final BorderPane root = new BorderPane();
	
	private final Label userLabel = new Label();
	private final Button logoutButton = new Button("Logout");
	private final Button changeRolesButton = new Button("Change Roles");
	private final Button enterMessagesButton = new Button("Messages Page");
	private final Button enterForumButton = new Button("Enter Forum");
    private final TextField searchMessagesTextField = new TextField();
    private final Button filterMessagesButton = new Button("Filter Messages");
	
	private final ObservableList<PostDisplayViewModel> viewModels = FXCollections.observableArrayList();
	private final ListView<PostDisplayViewModel> messagesListView = new ListView<>(viewModels);
	
	private final ForumHeaderViewModel messagesHeaderViewModel;
	//TODO: change "QuestionPaneViewModel" when you make the messages pane
	private final QuestionPaneViewModel messagesPaneViewModel;
	
	private Consumer<int[]> getVisibleMessagesCallback;
	
	// ===================
	// INITIALIZATION
	// ===================
	
	public MessagesPage(ForumHeaderViewModel messagesHeaderViewModel, QuestionPaneViewModel messagesPaneViewModel) {
		this.messagesHeaderViewModel = messagesHeaderViewModel;
		this.messagesPaneViewModel = messagesPaneViewModel;
		bindViewModelProperties();
		
		filterMessagesButton.setOnMouseClicked(_ -> messagesHeaderViewModel.triggerPostsFilter());
		
		searchMessagesTextField.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) {
				messagesHeaderViewModel.triggerPostsFilter(); 
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
    	
		messagesListView.setCellFactory(_ -> new PostCell());
		
		searchMessagesTextField.setPromptText("Filter questions...");
    	
		BorderPane centerPane = new BorderPane();
		centerPane.setCenter(messagesListView);
    	
		root.setCenter(centerPane);
		
		Scene scene = new Scene(root, 800, 400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Messages Home Page");
		primaryStage.show();
		
		setupScrollVisibilityCallback();
    	
    }
    
    private void setupScrollVisibilityCallback() {
        messagesListView.skinProperty().addListener((_, _, newSkin) -> {
            if (newSkin == null) return;

            Platform.runLater(() -> {
                ScrollBar verticalBar = (ScrollBar) messagesListView.lookup(".scroll-bar:vertical");
                if (verticalBar == null) return;

                verticalBar.valueProperty().addListener((_, _, _) -> {
                    int[] visibleIndices = getVisibleIndices();
                    if (visibleIndices == null || getVisibleMessagesCallback == null) return;
                    getVisibleMessagesCallback.accept(visibleIndices);
                });
            });
        });
    }
    private int[] getVisibleIndices() {
        if (!(messagesListView.getSkin() instanceof ListViewSkin<?> skin)) return null;
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
   
    
    public ListView<PostDisplayViewModel> getMessagesListView() {
		return messagesListView;
	}
    
    // ===================
    // 		 SETTERS 
 	// ===================
    
    public void setUserLabel(String username) {
        userLabel.setText("Hello, " + username + "!");
    }

    public void setGetVisibleMessageIDsCallback(Consumer<int[]> callback) {
        this.getVisibleMessagesCallback = callback;
    }
    
    private void bindViewModelProperties() {
    	searchMessagesTextField.textProperty().bindBidirectional(messagesHeaderViewModel.getSearchFieldTextProperty());
    }
}