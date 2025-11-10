package Controllers.Messages;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RecentInteractionsViewModel {

    private final IntegerProperty userIdProperty = new SimpleIntegerProperty();
    private final StringProperty usernameProperty = new SimpleStringProperty();
    private final StringProperty lastMessageProperty = new SimpleStringProperty();
    private final StringProperty timestampProperty = new SimpleStringProperty();
    private final IntegerProperty unreadCountProperty = new SimpleIntegerProperty();

    public RecentInteractionsViewModel() { }

    public RecentInteractionsViewModel(int userId, String username, String lastMessage, String timestamp, int unreadCount) {
        setUserId(userId);
        setUsername(username);
        setLastMessage(lastMessage);
        setTimestamp(timestamp);
        setUnreadCount(unreadCount);
    }
    // This will need to hook into the messages model
    // all of the data should come from that table
    // the recency of the interaction will go by the
    // timestamp of the last message exchanged between users
    
    
    // 
    
    
    // Properties
    public IntegerProperty getUserIdProperty() {
        return userIdProperty;
    }

    public StringProperty getUsernameProperty() {
        return usernameProperty;
    }

    public StringProperty getLastMessageProperty() {
        return lastMessageProperty;
    }

    public StringProperty getTimestampProperty() {
        return timestampProperty;
    }

    public IntegerProperty getUnreadCountProperty() {
        return unreadCountProperty;
    }

    // Value getters
    public int getUserId() {
        return userIdProperty.get();
    }

    public String getUsername() {
        return usernameProperty.get();
    }

    public String getLastMessage() {
        return lastMessageProperty.get();
    }

    public String getTimestamp() {
        return timestampProperty.get();
    }

    public int getUnreadCount() {
        return unreadCountProperty.get();
    }

    // Setters
    public void setUserId(int userId) {
        this.userIdProperty.set(userId);
    }

    public void setUsername(String username) {
        this.usernameProperty.set(username);
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessageProperty.set(lastMessage);
    }

    public void setTimestamp(String timestamp) {
        this.timestampProperty.set(timestamp);
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCountProperty.set(unreadCount);
    }

	public String getLastMessagePreview() {
		// TODO Auto-generated method stub
		return getLastMessage();
	}
}