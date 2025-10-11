package Controllers;

import javafx.scene.control.ListCell;

public class QuestionListCell extends ListCell<QuestionDisplayViewModel> {
    @Override
    protected void updateItem(QuestionDisplayViewModel data, boolean empty) {
        super.updateItem(data, empty);
        if (empty || data == null) {
            setText(null);
        } else {
            setText(data.getQuestion().getTitle() + " - " + data.getUserStatus());
        }
    }
}