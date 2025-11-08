// language: java
// File: CSE-360-TeamProject/src/Controllers/Messages/MessagesViewModel.java
package Controllers.Messages;

import Models.Message;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/*
 Updated: added convenience constructor to build a MessagesViewModel from Models.Message.
 If only ids are available for sender/receiver, they are converted to strings.
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

    // Convenience: construct from Models.Message (sender/receiver converted to id strings if no username available)
    public MessagesViewModel(Message m) {
        if (m.getReviewId() != null) setReviewId(m.getReviewId());
        setSender(m.getSenderId() == null ? "" : String.valueOf(m.getSenderId()));
        setRecipient(m.getReceiverId() == null ? "" : String.valueOf(m.getReceiverId()));
        setMessageText(m.getContent() == null ? "" : m.getContent());
        setTimestamp(m.getSentTimestamp() == null ? "" : m.getSentTimestamp().toString());
    }

    // Properties
    public IntegerProperty getReviewIdProperty() { return reviewIdProperty; }
    public StringProperty getSenderProperty() { return senderProperty; }
    public StringProperty getRecipientProperty() { return recipientProperty; }
    public StringProperty getMessageTextProperty() { return messageTextProperty; }
    public StringProperty getTimestampProperty() { return timestampProperty; }
    // Value getters
    public int getReviewId() { return reviewIdProperty.get(); }
    public String getSender() { return senderProperty.get(); }
    public String getRecipient() { return recipientProperty.get(); }
    public String getMessageText() { return messageTextProperty.get(); }
    public String getTimestamp() { return timestampProperty.get(); }
    // Value setters
    public void setReviewId(int reviewId) { this.reviewIdProperty.set(reviewId); }
    public void setSender(String sender) { this.senderProperty.set(sender); }
    public void setRecipient(String recipient) { this.recipientProperty.set(recipient); }
    public void setMessageText(String messageText) { this.messageTextProperty.set(messageText); }
    public void setTimestamp(String timestamp) { this.timestampProperty.set(timestamp); }
}
