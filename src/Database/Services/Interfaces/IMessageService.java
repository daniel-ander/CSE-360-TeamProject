// language: java
package Database.Services.Interfaces;

import Models.Message;
import Exceptions.DBExceptions.DataAccessException;

import java.util.List;

public interface IMessageService {
    // CREATE
    Message insertMessage(Message message) throws DataAccessException;

    // READ
    List<Message> getMessagesForUsername(String username) throws DataAccessException;
    List<Message> getMessagesByReviewId(int reviewId) throws DataAccessException;

    // UPDATE
    boolean updateMessage(int messageId, String newContent) throws DataAccessException;

    // DELETE
    boolean deleteMessage(int messageId) throws DataAccessException;
}
