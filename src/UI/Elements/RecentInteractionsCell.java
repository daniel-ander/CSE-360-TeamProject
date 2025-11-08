package UI.Elements;

import Controllers.Messages.RecentInteractionsViewModel;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

public class RecentInteractionsCell extends ListCell<RecentInteractionsViewModel> {
    private final Label usernameLabel = new Label();
    private final Label lastMessageLabel = new Label();
    private final VBox content;

    public RecentInteractionsCell() {
        usernameLabel.setFont(new Font("Arial", 16));
        lastMessageLabel.setFont(new Font("Arial", 12));
        lastMessageLabel.setTextFill(Color.GRAY);

        content = new VBox(usernameLabel, lastMessageLabel);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(3);
        content.setPadding(new Insets(5));
    }

    @Override
    protected void updateItem(RecentInteractionsViewModel vm, boolean empty) {
        super.updateItem(vm, empty);
        if (vm == null || empty) {
            setGraphic(null);
        } else {
            usernameLabel.setText(vm.getUsername());
            lastMessageLabel.setText(vm.getLastMessagePreview());
            setGraphic(content);
        }
    }
}
