// language: java
// File: CSE-360-TeamProject/src/Database/Services/MessageService.java
package Database.Services;

import Database.Repositories.Interfaces.IMessageRepository;
import Database.Services.Interfaces.IMessageService;
import java.util.List;
import Exceptions.DBExceptions.*;
import Models.Message;
import java.sql.SQLException;

/**
 * The MessageService class provides business logic for managing Message objects.
 */
public class MessageService implements IMessageService {

    private final IMessageRepository messageRepository;

    /**
     * Creates a new MessageService with the given repository.
     */
    public MessageService(IMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * Inserts a new message into the database.
     */
    @Override
    public Message insertMessage(Message message) throws DataAccessException {
        try {
            return messageRepository.insertMessage(message);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert message due to: " + e);
        }
    }

    /**
     * Retrieves all messages for a given username.
     */
    @Override
    public List<Message> getMessagesForUsername(String username) throws DataAccessException {
        try {
            return messageRepository.getMessagesForUsername(username);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get messages for username: " + username + " due to: " + e);
        }
    }

    /**
     * Retrieves all messages associated with a specific review ID.
     */
    @Override
    public List<Message> getMessagesByReviewId(int reviewId) throws DataAccessException {
        try {
            return messageRepository.getMessagesByReviewId(reviewId);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get messages for review id: " + reviewId + " due to: " + e);
        }
    }

    /**
     * Updates the content of a message by its ID.
     */
    @Override
    public boolean updateMessage(int messageId, String newContent) throws DataAccessException {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty.");
        }
        try {
            return messageRepository.updateMessage(messageId, newContent);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update message due to: " + e);
        }
    }

    /**
     * Deletes a message by its ID.
     */
    @Override
    public boolean deleteMessage(int messageId) throws DataAccessException {
        try {
            return messageRepository.deleteMessageById(messageId);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete message id: " + messageId + " due to: " + e);
        }
    }

	@Override
	public List<Message> getAllMessages() {
		// TODO Auto-generated method stub
		return null;
	}
}
