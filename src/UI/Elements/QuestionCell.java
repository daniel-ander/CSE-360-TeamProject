package UI.Elements;

import Models.Question;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class QuestionCell extends ListCell<Question> {
    private final VBox layout = new VBox(4);
    private final Label titleLabel = new Label();
    private final Label textLabel = new Label();
    private final Label statusLabel = new Label();

    public QuestionCell() {
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        titleLabel.setWrapText(true);

        textLabel.setFont(Font.font(12));
        textLabel.setWrapText(true);

        statusLabel.setFont(Font.font(11));
        statusLabel.setStyle("-fx-text-fill: gray;");

        layout.getChildren().addAll(titleLabel, textLabel, statusLabel);
        layout.setFillWidth(true);
        layout.setMaxWidth(Double.MAX_VALUE);
    }

    @Override
    protected void updateItem(Question q, boolean empty) {
        super.updateItem(q, empty);
        if (empty || q == null) {
            setGraphic(null);
        } else {
            titleLabel.setText(q.getTitle());
            textLabel.setText(truncate(q.getText(), 150)); // preview
            statusLabel.setText(formatStatus(q));
            setGraphic(layout);
        }
    }

    private String truncate(String text, int limit) {
        return text.length() <= limit ? text : text.substring(0, limit) + "...";
    }

    private String formatStatus(Question q) {
        StringBuilder s = new StringBuilder();
        if (q.getAnswered()) s.append("Answered ");
        if (q.getResolved()) s.append("• Resolved ");
        s.append("• Upvotes: ").append(q.getUpvoteCount());
        return s.toString().trim();
    }
}
