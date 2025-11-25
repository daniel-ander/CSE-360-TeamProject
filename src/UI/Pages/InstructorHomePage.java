
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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.function.Consumer;

/**
 * This page displays a simple welcome message for the user.
 */

public class InstructorHomePage implements IInitializable {
	
	private final BorderPane root = new BorderPane();
	
	private final VBox layout = new VBox();
	private final ForumHeaderViewModel instructorHeaderViewModel;
	private final Label userLabel = new Label();
	private final Button logoutButton = new Button("Logout");
	private final Button changeRolesButton = new Button("Change Roles");
	private final Button manageRequestsButton = new Button("Manage Reviewer Requests");
	private final Button enterForumButton = new Button("Enter Forum");
	private final Button enterMessagesButton = new Button("Messages Page");
	
	
	
	public InstructorHomePage(ForumHeaderViewModel instructorHeaderViewModel) {
    	// import stylesheet
		this.instructorHeaderViewModel = instructorHeaderViewModel;
    	
    }
	
    public void setInitialLayout(Stage primaryStage) {
    	
		Region spacer = new Region();
		HBox header = new HBox();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		
		header.getChildren().addAll(logoutButton, changeRolesButton, enterMessagesButton, enterForumButton, manageRequestsButton, spacer);
		header.setStyle("-fx-padding: 10; -fx-alignment: center-left;");
		root.setTop(header);
    	
    	root.getStyleClass().add("layout");
    
	    
	    // Set the scene to primary stage
    	Scene scene = new Scene(root, 800, 400);
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("Instructor Home Page");
	    primaryStage.show();
    	
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
    
    public Button getManageRequestsButton() {
        return manageRequestsButton;
    }
    
	public Button getEnterForumButton() {
		return enterForumButton;
	}
	
	public Button getEnterMessagesPageButton() {
		return enterMessagesButton;
	}

    
    // ===================
    // 		 SETTERS 
 	// ===================
    
}
