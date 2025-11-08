package Controllers.Messages;

import Models.Message;
import Database.Services.MessageService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Handles the data and logic for the right side of the Messages page.
 * 
 * Each MessagesViewModel represents a single message thread originating from a review (an Answer marked as isReview).
 * It interacts with the MessageService to retrieve and send messages tied to that review.
 * 
 * Data:
 *  - Populates from the Message table.
 *  - Each reply in the thread is a Message record with review_id referencing the review Answer.
 * 
 * Structure:
 *  - Holds an ObservableList<MessageViewModel> for the thread messages.
 *  - Updates when a new message is sent or when the selected review changes.
 * 
 * Display:
 *  - The visual layout of each message is handled by MessageCell.
 */


public class MessagesViewModel {
	
	private final IntegerProperty reviewIdProperty = new SimpleIntegerProperty();
	private final StringProperty senderProperty = new SimpleStringProperty();
	private final StringProperty recipientProperty = new SimpleStringProperty();
	private final StringProperty messageTextProperty = new SimpleStringProperty();
	private final StringProperty timestampProperty = new SimpleStringProperty();
	
	public MessagesViewModel() { }
	
	public MessagesViewModel(int reviewId, String sender, String recipient, String messageText, String timestamp) {
		setReviewId(reviewId);
		setSender(sender);
		setRecipient(recipient);
		setMessageText(messageText);
		setTimestamp(timestamp);
	}
	
	// Properties
	public IntegerProperty getReviewIdProperty() {
		return reviewIdProperty;
	}
	public StringProperty getSenderProperty() {
		return senderProperty;
	}
	public StringProperty getRecipientProperty() {
		return recipientProperty;
	}
	public StringProperty getMessageTextProperty() {
		return messageTextProperty;
	}
	public StringProperty getTimestampProperty() {
		return timestampProperty;
	}
	// Value getters
	public int getReviewId() {
		return reviewIdProperty.get();
	}
	public String getSender() {
		return senderProperty.get();
	}
	public String getRecipient() {
		return recipientProperty.get();
	}
	public String getMessageText() {
		return messageTextProperty.get();
	}
	public String getTimestamp() {
		return timestampProperty.get();
	}
	// Value setters
	public void setReviewId(int reviewId) {
		this.reviewIdProperty.set(reviewId);
	}
	public void setSender(String sender) {
		this.senderProperty.set(sender);
	}
	public void setRecipient(String recipient) {
		this.recipientProperty.set(recipient);
	}
	public void setMessageText(String messageText) {
		this.messageTextProperty.set(messageText);
	}
	public void setTimestamp(String timestamp) {
		this.timestampProperty.set(timestamp);
	}	
	
	

}
