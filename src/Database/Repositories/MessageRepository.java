// language: java
package Database.Repositories;

import Database.IGeneralDBManager;
import Database.Repositories.Interfaces.IMessageRepository;
import Models.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The MessageRepository class provides database operations for Message objects.
 * It implements CRUD functionality (create, read, update, delete) and helper methods
 * to interact with the messages table in the database.
 */
public class MessageRepository implements IMessageRepository {

    private final IGeneralDBManager generalDBManager;

    /**
     * Creates a new MessageRepository with the given database manager.
     */
    public MessageRepository(IGeneralDBManager generalDBManager) {
        this.generalDBManager = generalDBManager;
    }

    /**
     * Inserts a new message into the database and returns the populated Message object.
     */
    @Override
    public Message insertMessage(Message m) throws SQLException {
        String insertSql = "INSERT INTO " + Message.tableName +
                " (review_id, sender_id, receiver_id, parent_message_id, message_text) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = generalDBManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

            if (m.getReviewId() == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, m.getReviewId());
            if (m.getSenderId() == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, m.getSenderId());
            if (m.getReceiverId() == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, m.getReceiverId());
            if (m.getParentMessageId() == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, m.getParentMessageId());
            ps.setString(5, m.getContent());
            ps.executeUpdate();

            int id;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Insert succeeded but no generated key returned.");
                id = keys.getInt(1);
            }

            // Fetch the full row (includes DB defaults like sent_timestamp and username aliases)
            String fetchSql = "SELECT m.*, s.userName AS sender_username, r.userName AS receiver_username FROM "
                    + Message.tableName + " m "
                    + "LEFT JOIN " + Models.User.tableName + " s ON m.sender_id = s.id "
                    + "LEFT JOIN " + Models.User.tableName + " r ON m.receiver_id = r.id "
                    + "WHERE m.message_id = ?";

            try (PreparedStatement ps2 = connection.prepareStatement(fetchSql)) {
                ps2.setInt(1, id);
                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (rs2.next()) return Message.fromResultSet(rs2);
                    throw new SQLException("Inserted message not found on fetch.");
                }
            }
        }
    }

    /**
     * Retrieves all messages for a given review ID, ordered by timestamp.
     */
    @Override
    public List<Message> getMessagesByReviewId(int reviewId) throws SQLException {
        // include usernames where available
    	String sql = "SELECT m.*, s.userName AS sender_username, r.userName AS receiver_username FROM "
    	        + Message.tableName + " m "
    	        + "LEFT JOIN " + Models.User.tableName + " s ON m.sender_id = s.id "
    	        + "LEFT JOIN " + Models.User.tableName + " r ON m.receiver_id = r.id "
    	        + "WHERE m.review_id = ? ORDER BY m.sent_timestamp ASC";
        try (Connection connection = generalDBManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Message> list = new ArrayList<>();
                while (rs.next()) list.add(Message.fromResultSet(rs));
                return list;
            }
        }
    }

    /**
     * Retrieves all messages for a given username, ordered by timestamp.
     */
    @Override
    public List<Message> getMessagesForUsername(String username) throws SQLException {
        // return sender/receiver usernames as well
    	String sql = "SELECT m.*, s.userName AS sender_username, r.userName AS receiver_username FROM "
    	        + Message.tableName + " m "
    	        + "LEFT JOIN " + Models.User.tableName + " s ON m.sender_id = s.id "
    	        + "LEFT JOIN " + Models.User.tableName + " r ON m.receiver_id = r.id "
    	        + "WHERE s.userName = ? OR r.userName = ? ORDER BY m.sent_timestamp ASC";
        try (Connection connection = generalDBManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                List<Message> list = new ArrayList<>();
                while (rs.next()) list.add(Message.fromResultSet(rs));
                return list;
            }
        }
    }

    /**
     * Updates the content of a message by its ID.
     */
    @Override
    public boolean updateMessage(int messageId, String newContent) throws SQLException {
        String sql = "UPDATE " + Message.tableName + " SET message_text = ? WHERE message_id = ?";
        try (Connection connection = generalDBManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newContent);
            ps.setInt(2, messageId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a message from the database by its ID.
     */
    @Override
    public boolean deleteMessageById(int messageId) throws SQLException {
        String sql = "DELETE FROM " + Message.tableName + " WHERE message_id = ?";
        try (Connection connection = generalDBManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, messageId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves a single message by its ID.
     */
    @Override
    public Message getMessageById(int messageId) throws SQLException {
        String sql = "SELECT m.*, s.username AS sender_username, r.username AS receiver_username FROM " + Message.tableName +
                     " m LEFT JOIN users s ON m.sender_id = s.id LEFT JOIN users r ON m.receiver_id = r.id WHERE message_id = ?";
        try (Connection connection = generalDBManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, messageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Message.fromResultSet(rs);
                return null;
            }
        }
    }
}
