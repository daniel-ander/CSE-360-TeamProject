// language: java
// File: CSE-360-TeamProject/src/Database/Repositories/Interfaces/IMessageRepository.java
package Database.Repositories.Interfaces;

import java.sql.SQLException;
import java.util.List;
import Models.Message;

public interface IMessageRepository {
    public Message insertMessage(Message m) throws SQLException;
    public List<Message> getMessagesByReviewId(int reviewId) throws SQLException;
    public List<Message> getMessagesForUsername(String username) throws SQLException;
    public boolean updateMessage(int messageId, String newContent) throws SQLException;
    public boolean deleteMessageById(int messageId) throws SQLException;
    public Message getMessageById(int messageId) throws SQLException;
}
