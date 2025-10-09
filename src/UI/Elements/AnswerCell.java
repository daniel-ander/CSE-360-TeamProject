package UI.Elements;

import Models.Answer;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class AnswerCell extends ListCell<Answer> {
    private final VBox layout = new VBox(4);
    private final Label textLabel = new Label();
    private final Label metaLabel = new Label();

    public AnswerCell() {
        textLabel.setWrapText(true);
        textLabel.setFont(Font.font("System", FontWeight.NORMAL, 13));

        metaLabel.setFont(Font.font("System", FontWeight.NORMAL, 10));
        metaLabel.setStyle("-fx-text-fill: gray;");

        layout.getChildren().addAll(textLabel, metaLabel);
        layout.setFillWidth(true);
    }

    @Override
    protected void updateItem(Answer a, boolean empty) {
        super.updateItem(a, empty);
        if (empty || a == null) {
            setGraphic(null);
        } else {
            textLabel.setText(a.getText());
            metaLabel.setText(formatMeta(a));

            // simple indentation for threaded replies
            double indent = (a.getParentAnswerId() > 0) ? 25 : 0;
            layout.setPadding(new Insets(0, 0, 0, indent));

            setGraphic(layout);
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
