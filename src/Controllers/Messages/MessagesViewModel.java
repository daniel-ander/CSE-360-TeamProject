// language: java
package Controllers.Messages;

import Models.Message;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/*
 Updated: prefer sender/recipient username when Message provides them.
*/

public class MessagesViewModel {

    private final IntegerProperty reviewIdProperty = new SimpleIntegerProperty();
    private final StringProperty senderProperty = new SimpleStringProperty();
    private final StringProperty recipientProperty = new SimpleStringProperty();
    private final StringProperty messageTextProperty = new SimpleStringProperty();
    private final StringProperty timestampProperty = new SimpleStringProperty();
    private final IntegerProperty senderIdProperty = new SimpleIntegerProperty();
    private final IntegerProperty receiverIdProperty = new SimpleIntegerProperty();
    private final IntegerProperty messageIdProperty = new SimpleIntegerProperty();

    public MessagesViewModel() { }

    public MessagesViewModel(int reviewId, String sender, String recipient, String messageText, String timestamp) {
        setReviewId(reviewId);
        setSender(sender);
        setRecipient(recipient);
        setMessageText(messageText);
        setTimestamp(timestamp);
    }

    // Convenience: construct from Models.Message (prefer usernames if available, else fallback to ids)
    public MessagesViewModel(Message m) {
        if (m.getReviewId() != null) setReviewId(m.getReviewId());
        if (m.getSenderId() != null) setSenderId(m.getSenderId());
        if (m.getReceiverId() != null) setReceiverId(m.getReceiverId());

        String sender = (m.getSenderUsername() != null && !m.getSenderUsername().isBlank()) 
            ? m.getSenderUsername() 
            : String.valueOf(m.getSenderId());

        String recipient = (m.getReceiverUsername() != null && !m.getReceiverUsername().isBlank()) 
            ? m.getReceiverUsername() 
            : String.valueOf(m.getReceiverId());

        setSender(sender);
        setRecipient(recipient);
        setMessageText(m.getContent() == null ? "" : m.getContent());
        setTimestamp(m.getSentTimestamp() == null ? "" : m.getSentTimestamp().toString());
    }


    // Properties
    public IntegerProperty getReviewIdProperty() { return reviewIdProperty; }
    public StringProperty getSenderProperty() { return senderProperty; }
    public StringProperty getRecipientProperty() { return recipientProperty; }
    public StringProperty getMessageTextProperty() { return messageTextProperty; }
    public StringProperty getTimestampProperty() { return timestampProperty; }
    public IntegerProperty getSenderIdProperty() { return senderIdProperty; }
    public IntegerProperty getReceiverIdProperty() { return receiverIdProperty; }
    public IntegerProperty getMessageIdProperty() { return messageIdProperty; }
    
    
    // Value getters
    public int getReviewId() { return reviewIdProperty.get(); }
    public int getSenderId() { return senderIdProperty.get(); }
    public String getSender() { return senderProperty.get(); }
    public String getRecipient() { return recipientProperty.get(); }
    public String getMessageText() { return messageTextProperty.get(); }
    public String getTimestamp() { return timestampProperty.get(); }
    public int getMessageId() { return messageIdProperty.get(); }
    public int getReceiverId() { return receiverIdProperty.get(); }
    // Value setters
    public void setReviewId(int reviewId) { this.reviewIdProperty.set(reviewId); }
    public void setSenderId(int senderId) { this.senderIdProperty.set(senderId); }
    public void setReceiverId(int receiverId) { this.receiverIdProperty.set(receiverId); }
    public void setMessageId(int messageId) { this.messageIdProperty.set(messageId); }
    public void setSender(String sender) { this.senderProperty.set(sender); }
    public void setRecipient(String recipient) { this.recipientProperty.set(recipient); }
    public void setMessageText(String messageText) { this.messageTextProperty.set(messageText); }
    public void setTimestamp(String timestamp) { this.timestampProperty.set(timestamp); }

}
