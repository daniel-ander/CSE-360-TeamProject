// language: java
// File: CSE-360-TeamProject/src/Database/Repositories/MessageRepository.java
package Database.Repositories;

import Database.Repositories.Interfaces.IMessageRepository;
import Models.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository implements IMessageRepository {

    private final Connection conn;

    public MessageRepository(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Message insertMessage(Message m) throws SQLException {
        String sql = "INSERT INTO " + Message.tableName +
                " (review_id, sender_id, receiver_id, parent_message_id, message_text) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (m.getReviewId() == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, m.getReviewId());
            if (m.getSenderId() == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, m.getSenderId());
            if (m.getReceiverId() == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, m.getReceiverId());
            if (m.getParentMessageId() == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, m.getParentMessageId());
            ps.setString(5, m.getContent());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    return new Message(id, m.getReviewId(), m.getSenderId(), m.getReceiverId(), m.getParentMessageId(), m.getContent(), null);
                } else {
                    throw new SQLException("Insert succeeded but no generated key returned.");
                }
            }
        }
    }

    @Override
    public List<Message> getMessagesByReviewId(int reviewId) throws SQLException {
        String sql = "SELECT * FROM " + Message.tableName + " WHERE review_id = ? ORDER BY sent_timestamp ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Message> list = new ArrayList<>();
                while (rs.next()) list.add(Message.fromResultSet(rs));
                return list;
            }
        }
    }

    @Override
    public List<Message> getMessagesForUsername(String username) throws SQLException {
        // assumes a `users` table with columns `id` and `username`
        String sql = "SELECT m.* FROM " + Message.tableName + " m " +
                     "LEFT JOIN users s ON m.sender_id = s.id " +
                     "LEFT JOIN users r ON m.receiver_id = r.id " +
                     "WHERE s.username = ? OR r.username = ? ORDER BY m.sent_timestamp ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                List<Message> list = new ArrayList<>();
                while (rs.next()) list.add(Message.fromResultSet(rs));
                return list;
            }
        }
    }

    @Override
    public boolean updateMessage(int messageId, String newContent) throws SQLException {
        String sql = "UPDATE " + Message.tableName + " SET message_text = ? WHERE message_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newContent);
            ps.setInt(2, messageId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteMessageById(int messageId) throws SQLException {
        String sql = "DELETE FROM " + Message.tableName + " WHERE message_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, messageId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Message getMessageById(int messageId) throws SQLException {
        String sql = "SELECT * FROM " + Message.tableName + " WHERE message_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, messageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Message.fromResultSet(rs);
                return null;
            }
        }
    }
}
