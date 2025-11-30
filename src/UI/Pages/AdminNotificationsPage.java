package UI.Pages;

import UI.Interfaces.IInitializable;
import Models.AdminRequest;
import Models.Answer;
import Models.Message;
import Models.Question;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * This page displays a simple a Requests tab.
 */
public class AdminNotificationsPage implements IInitializable {
	
	private final BorderPane root = new BorderPane();
	private final Button logoutButton = new Button("Logout");
	private final Button changeRolesButton = new Button("Change Roles");
	private final Button backButton = new Button("Back");

	// Requests tab UI
	private final TabPane tabPane = new TabPane();
	private final ListView<AdminRequest> openRequestsList = new ListView<>();
	private final ListView<AdminRequest> closedRequestsList = new ListView<>();
	private final Button closeButton = new Button("Close Request");
	private final TextArea reopenDescription = new TextArea();
	private final Button reopenButton = new Button("Reopen Selected");
	private final TextArea requestsContentViewer = new TextArea();
	
	public final ListView<String> userListView = new ListView<>();
	public final ListView<Question> questionsListView = new ListView<>();
	public final ListView<Answer> answersListView = new ListView<>();
	public final ListView<Message> messagesListView = new ListView<>();
	public final TextArea contentViewer = new TextArea();
	
	
	public AdminNotificationsPage() {
		// small cell factory so AdminRequest.toString() shows nicely
		openRequestsList.setCellFactory(_ -> new ListCell<>() {
			@Override protected void updateItem(AdminRequest item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty || item == null ? null : item.toString());
			}
		});
		closedRequestsList.setCellFactory(openRequestsList.getCellFactory());
		reopenDescription.setPromptText("Description for reopened request...");
		reopenDescription.setPrefRowCount(3);
	}
	
    public void setInitialLayout(Stage primaryStage) {
    	
		Region spacer = new Region();
		HBox header = new HBox();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		header.getChildren().addAll(logoutButton, changeRolesButton, backButton, spacer);
		header.setStyle("-fx-padding: 10; -fx-alignment: center-left;");
		root.setTop(header);
    	
    	root.getStyleClass().add("layout");
    	
    	// Requests tab layout
    	GridPane requestsGrid = new GridPane();
    	requestsGrid.setPadding(new Insets(10));
    	requestsGrid.setHgap(10);
    	requestsGrid.setVgap(10);
    	
    	Label openLabel = new Label("Open Requests");
    	Label closedLabel = new Label("Closed Requests");
    	openRequestsList.setPrefSize(300, 200);
    	closedRequestsList.setPrefSize(300, 200);
    	
    	// Top row: lists
    	requestsGrid.add(openLabel, 0, 0);
    	requestsGrid.add(closedLabel, 1, 0);
    	requestsGrid.add(openRequestsList, 0, 1);
    	requestsGrid.add(closedRequestsList, 1, 1);
    	
    	// Bottom row: create + reopen
    	HBox createBox = new HBox(10, closeButton);
    	createBox.setPrefHeight(35);
    	VBox reopenBox = new VBox(5, reopenDescription, reopenButton);
    	reopenBox.setPrefWidth(300);
    	VBox requestsContentView = new VBox(10);
    	requestsContentView.setPadding(new Insets(10));
    	requestsContentView.setPrefWidth(300);
    	Label requestsContentLabel = new Label("Content Viewer");
    	requestsContentViewer.setWrapText(true);
    	requestsContentViewer.setEditable(false);
    	requestsContentView.getChildren().addAll(requestsContentLabel, requestsContentViewer);
    	
    	
    	requestsGrid.add(createBox, 0, 2);
    	requestsGrid.add(reopenBox, 1, 2);
    	
    	Tab requestsTab = new Tab("Requests", requestsGrid);
    	requestsTab.setClosable(false);
    	
    	// Reviews tab layout (left: users, center: activity tabs, right: content viewer)
    	VBox leftUsersBox = new VBox(10);
    	leftUsersBox.setPadding(new Insets(10));
    	Label usersLabel = new Label("Users");
    	leftUsersBox.getChildren().addAll(usersLabel, userListView);
    	leftUsersBox.setPrefWidth(200);

    	// Center activity tabs for Reviews
    	VBox centerActivityBox = new VBox(10);
    	centerActivityBox.setPadding(new Insets(10));
    	TabPane activityTabPane = new TabPane();
    	Tab questionsTabR = new Tab("Questions", questionsListView);
    	questionsTabR.setClosable(false);
    	Tab answersTabR = new Tab("Answers", answersListView);
    	answersTabR.setClosable(false);
    	Tab messagesTabR = new Tab("Messages", messagesListView);
    	messagesTabR.setClosable(false);
    	activityTabPane.getTabs().addAll(questionsTabR, answersTabR, messagesTabR);
    	activityTabPane.setTabMinWidth(79);
    	activityTabPane.setTabMaxWidth(79);
    	centerActivityBox.getChildren().add(activityTabPane);

    	// Right content viewer
    	VBox rightContentBox = new VBox(10);
    	rightContentBox.setPadding(new Insets(10));
    	rightContentBox.setPrefWidth(300);
    	Label contentLabel = new Label("Content Viewer");
    	contentViewer.setWrapText(true);
    	contentViewer.setEditable(false);
    	rightContentBox.getChildren().addAll(contentLabel, contentViewer);

    	// Combine into a BorderPane for the Reviews tab
    	BorderPane reviewsPane = new BorderPane();
    	reviewsPane.setLeft(leftUsersBox);
    	reviewsPane.setCenter(centerActivityBox);
    	reviewsPane.setRight(rightContentBox);

    	// Create and add the Reviews tab
    	Tab reviewsTab = new Tab("Reviews", reviewsPane);
    	reviewsTab.setClosable(false);
    	
    	tabPane.getTabs().addAll(requestsTab, reviewsTab);
    	root.setCenter(tabPane);
    
	    Scene scene = new Scene(root, 900, 500);
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("Admin Notifications Page");
	    primaryStage.show();
    }
    
	// ===================
    // 		 GETTERS 
 	// ===================
    
    public Button getLogoutButton() { return logoutButton; }
    public Button getChangeRolesButton() { return changeRolesButton; }
    public Button getBackButton() { return backButton; }

	// Requests tab getters for controller binding
	public ListView<AdminRequest> getOpenRequestsListView() { return openRequestsList; }
	public ListView<AdminRequest> getClosedRequestsListView() { return closedRequestsList; }

	public TextArea getReopenDescriptionArea() { return reopenDescription; }
	public Button getReopenButton() { return reopenButton; }
	public Button getCloseButton() { return closeButton; }
	public TextArea getRequestsContentViewer() { return requestsContentViewer; }
	
	// Reviews tab getters for controller binding
	// return type updated to match view model (String usernames)
	public ListView<String> getUserListView() { return userListView; }
	public ListView<Question> getQuestionsListView() { return questionsListView; }
	public ListView<Answer> getAnswersListView() { return answersListView; }
	public ListView<Message> getMessagesListView() { return messagesListView; }
	public TextArea getContentViewerTextArea() { return contentViewer; }
    
    // ===================
    // 		 SETTERS 
 	// ===================
    
}
