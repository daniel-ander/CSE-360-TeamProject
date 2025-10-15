package UI.Elements;

import Models.Answer;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AnswerCell extends ListCell<Answer> {
    private final VBox layout = new VBox(4);
    private final Label textLabel = new Label();
    private final Label metaLabel = new Label();

    public AnswerCell() {
        textLabel.setWrapText(true);

        layout.getChildren().addAll(textLabel, metaLabel);
        layout.setFillWidth(true);
        layout.getStylesheets().add(
            getClass().getResource("/Styles/styles.css").toExternalForm()
        );

        layout.getStyleClass().add("answer-cell");
        textLabel.getStyleClass().add("answer-text");
        metaLabel.getStyleClass().add("answer-meta");
    }
    @Override
    protected void updateItem(Answer a, boolean empty) {
        super.updateItem(a, empty);
        if (empty || a == null) {
            setGraphic(null);
        } else {
            textLabel.setText(a.getText());
            metaLabel.setText(formatMeta(a));

            // Create wrapper so padding shifts the entire block
            HBox wrapper = new HBox(layout);
            wrapper.setPadding(new Insets(0, 0, 0, a.getReplyDepth() * 25));
            wrapper.setFillHeight(true);

            setGraphic(wrapper);
        }
    }

	private String formatMeta(Answer a) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID ").append(a.getId())
          .append(" • Upvotes: ").append(a.getUpvoteCount());
        if (a.isCorrect()) sb.append(" • Correct");
        if (a.getIsDeleted()) sb.append(" • Deleted");
        if (a.getParentAnswerId() > 0) sb.append(" • Reply to ").append(a.getParentAnswerId());
        return sb.toString();
    }
}
