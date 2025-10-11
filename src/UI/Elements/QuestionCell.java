package UI.Elements;

import Controllers.QuestionDisplayViewModel;
import Models.Question;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class QuestionCell extends ListCell<QuestionDisplayViewModel> {
    private final VBox layout = new VBox();
    private final Label titleLabel = new Label();
    private final Label textLabel = new Label();
    private final Label statusLabel = new Label();
    private final Label permissionLabel = new Label(); // This is for illustrative purposes

    public QuestionCell() {
        titleLabel.setWrapText(true);
        textLabel.setWrapText(true);
        permissionLabel.setWrapText(true);

        layout.getChildren().addAll(titleLabel, textLabel, statusLabel, permissionLabel);
        layout.setFillWidth(true);
        layout.setMaxWidth(Double.MAX_VALUE);

        // Attach stylesheet
        layout.getStylesheets().add(
            getClass().getResource("/Styles/styles.css").toExternalForm()
        );

        // Assign style classes
        layout.getStyleClass().add("question-cell");
        titleLabel.getStyleClass().add("question-title");
        textLabel.getStyleClass().add("question-body");
        statusLabel.getStyleClass().add("question-status");
    }

    @Override
    protected void updateItem(QuestionDisplayViewModel vm, boolean empty) {
        super.updateItem(vm, empty);
        if (empty || vm.getQuestion() == null) {
            setGraphic(null);
        } else {
            titleLabel.setText(vm.getQuestion().getTitle());
            textLabel.setText(truncate(vm.getQuestion().getText(), 150));
            statusLabel.setText(formatStatus(vm.getQuestion()));
            permissionLabel.setText(String.join(", ", vm.getPermissionList()));
            setGraphic(layout);
        }
    }

    private String truncate(String text, int limit) {
        return text.length() <= limit ? text : text.substring(0, limit) + "...";
    }

    private String formatStatus(Question q) {
        var parts = new java.util.ArrayList<String>();
        if (q.getAnswered()) parts.add("Answered");
        if (q.getResolved()) parts.add("Resolved");
        parts.add("Upvotes: " + q.getUpvoteCount());
        return String.join(" • ", parts);
    }
}
