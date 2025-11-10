// language: java
package Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Message {
    public static String tableName = "messages";

    private Integer messageId;
    private Integer reviewId;
    private Integer senderId;
    private Integer receiverId;
    private Integer parentMessageId;
    private String messageText;
    private Timestamp sentTimestamp;

    // Optional username fields (populated when repository selects them)
    private String senderUsername;
    private String receiverUsername;

    // Non-DB constructor
    public Message(Integer senderId, Integer reviewId, Integer receiverId,
                   Integer parentMessageId, String messageText) {
        this(null, reviewId, senderId, receiverId, parentMessageId, messageText, null);
    }
    // Constructor for when we have all the data (e.g., when retrieving from DB)
    public Message(Integer messageId, Integer reviewId, Integer senderId,
                   Integer receiverId, Integer parentMessageId,
                   String messageText, Timestamp sentTimestamp) {
        this.messageId = messageId;
        this.reviewId = reviewId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.parentMessageId = parentMessageId;
        this.messageText = messageText;
        this.sentTimestamp = sentTimestamp;
    }

    // Factory from ResultSet
    public static Message fromResultSet(ResultSet rs) throws SQLException {
        Integer id = rs.getObject("message_id") == null ? null : rs.getInt("message_id");
        Integer reviewId = rs.getObject("review_id") == null ? null : rs.getInt("review_id");
        Integer senderId = rs.getObject("sender_id") == null ? null : rs.getInt("sender_id");
        Integer receiverId = rs.getObject("receiver_id") == null ? null : rs.getInt("receiver_id");
        Integer parentId = rs.getObject("parent_message_id") == null ? null : rs.getInt("parent_message_id");
        String text = rs.getString("message_text");
        Timestamp ts = rs.getTimestamp("sent_timestamp");

        Message m = new Message(id, reviewId, senderId, receiverId, parentId, text, ts);

        // If repository included username aliases, read them (may be null)
        try {
            String sUser = null;
            String rUser = null;
            // Use column labels that repository will alias
            if (hasColumn(rs, "sender_username")) sUser = rs.getString("sender_username");
            if (hasColumn(rs, "receiver_username")) rUser = rs.getString("receiver_username");
            m.setSenderUsername(sUser);
            m.setReceiverUsername(rUser);
        } catch (Exception ignored) {
            // no-op if aliases not present
        }

        return m;
    }

    // helper to check presence of column in ResultSet (avoids SQLException if not selected)
    private static boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    // ===================
    // Getters
    // ===================

    public Integer getId() {
        return messageId;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public Integer getParentMessageId() {
        return parentMessageId;
    }

    public String getContent() {
        return messageText;
    }

    public Timestamp getSentTimestamp() {
        return sentTimestamp;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    // ===================
    // SETTERS
    // ===================

    public void setContent(String content) {
        if (content == null || content.strip().isEmpty()) {
            throw new IllegalArgumentException("Content is empty");
        }
        this.messageText = content;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public void setParentMessageId(Integer parentMessageId) {
        this.parentMessageId = parentMessageId;
    }

    public void setSentTimestamp(Timestamp sentTimestamp) {
        this.sentTimestamp = sentTimestamp;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public void setId(Integer id) {
        this.messageId = id;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }
}
