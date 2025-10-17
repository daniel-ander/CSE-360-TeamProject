package UI.Pages;

import UI.Elements.QuestionListView;
import UI.Interfaces.IInitializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * ForumPage displays the forum list with a header containing user info and controls.
 */
public class ForumPage implements IInitializable {

    private final BorderPane root = new BorderPane();

    private final Label userLabel = new Label();
    private final Button logoutButton = new Button("Logout");
    private final Button changeRolesButton = new Button("Change Roles");

    private QuestionListView questionListView;

    // ===================
    // 		 GETTERS 
 	// ===================

    public Button getLogoutButton() {
        return logoutButton;
    }

    public Button getChangeRolesButton() {
        return changeRolesButton;
    }

    public BorderPane getRoot() {
        return root;
    }
    public QuestionListView getForumListView() {
		return questionListView;
	}

    // ===================
    // 		 SETTERS 
 	// ===================

    public void setUserLabel(String text) {
        userLabel.setText("Hello, " + text + "!");
    }

    public void setInitialLayout(Stage primaryStage) {
        // Header bar with label + buttons
        HBox header = new HBox(10, userLabel, logoutButton, changeRolesButton);
        header.setStyle("-fx-padding: 10; -fx-alignment: center-left;");

        root.setTop(header);

        // Embed ForumListView in the center (empty for now, controller will attach)
        questionListView = new QuestionListView();
        root.setCenter(questionListView.getLayout());

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Forum Page");
        primaryStage.show();
    }
}
