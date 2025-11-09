package UI.Elements;

import Controllers.Messages.MessagesViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

public class MessageCell extends ListCell<MessagesViewModel> {
	
	// UI elements we want to display in each cell
	private final Label senderLabel = new Label();
	private final Label messageTextLabel = new Label();
	private final Label timestampLabel = new Label();
	private final VBox content;
	
	public MessageCell() {
		// Configure UI elements (fonts, colors, layout, etc.) link from styles
		
		// Add what to display in each cell here
		content = new VBox(senderLabel, messageTextLabel, timestampLabel);
		content.setAlignment(Pos.CENTER_LEFT);
		content.setSpacing(5);
		content.setPadding(new Insets(10));
	}
	
	@Override
	protected void updateItem(MessagesViewModel vm, boolean empty) {
		super.updateItem(vm, empty);
		if (vm == null || empty) {
			setGraphic(null);
		} else {
			// Set the text for each label from the ViewModel
			senderLabel.setText(vm.getSender());
			messageTextLabel.setText(vm.getMessageText());
			timestampLabel.setText(vm.getTimestamp());
			setGraphic(content);
		}
	}
	

}