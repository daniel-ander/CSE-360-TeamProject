package UI.Pages;

import UI.Interfaces.IInitializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

// instructor page to manage reviewer requests
public class InstructorRequestsPage implements IInitializable {
    private final VBox layout = new VBox(10);
    private final Scene scene = new Scene(layout, 1000, 600); // ui scene for dual list views
    private final Label titleLabel = new Label("Manage Reviewer Requests");
    private final ListView<String> requestsList = new ListView<>();
    private final Button approveButton = new Button("Approve");
    private final Button denyButton = new Button("Deny");
    private final Button backButton = new Button("Back");

    // performance + combined posts list (questions and answers authored by selected student)
    private final ListView<String> postsList = new ListView<>();
    // details view shows full context (question + answer details) or list of answers for a question
    private final ListView<String> detailsList = new ListView<>();

    public InstructorRequestsPage() {
        scene.getStylesheets().add(getClass().getResource("/Styles/styles.css").toExternalForm());
    }

    // getters
    public Scene getScene() { return scene; }
    public VBox getLayout() { return layout; }
    public ListView<String> getRequestsList() { return requestsList; }
    public Button getApproveButton() { return approveButton; }
    public Button getDenyButton() { return denyButton; }
    public Button getBackButton() { return backButton; }
    public ListView<String> getPostsList() { return postsList; }
    public ListView<String> getDetailsList() { return detailsList; }

    // layout
    public void setInitialLayout(Stage primaryStage) {
        titleLabel.getStyleClass().add("main-label");
        HBox top = new HBox(10, titleLabel, backButton);
        HBox.setHgrow(titleLabel, javafx.scene.layout.Priority.ALWAYS);
        VBox left = new VBox(8, new Label("Pending Requests"), requestsList, new HBox(8, approveButton, denyButton));
        left.setPrefWidth(300);
        VBox right = new VBox(8, new Label("Selected Student Posts"), postsList, new Label("Full Details"), detailsList);
        right.setPrefWidth(650);
        HBox center = new HBox(12, left, right);
        layout.getChildren().addAll(top, center);
        // scene to primary stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Instructor - Manage Requests");
    }

    // helpers to update list contents
    public void setRequests(List<String> items) {
        ObservableList<String> obs = FXCollections.observableArrayList(items);
        requestsList.setItems(obs);
    }
    // populate posts list
    public void setQuestions(List<String> items) {
        ObservableList<String> obs = FXCollections.observableArrayList(items);
        postsList.setItems(obs);
    }
    // populate details list
    public void setAnswers(List<String> items) {
        ObservableList<String> obs = FXCollections.observableArrayList(items);
        detailsList.setItems(obs);
    }
}
