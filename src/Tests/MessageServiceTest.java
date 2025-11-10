package Tests;

import Database.Repositories.Interfaces.IMessageRepository;
import Database.Services.MessageService;
import Exceptions.DBExceptions.DataAccessException;
import Models.Message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the Message Service.
 */
public class MessageServiceTest {

    /** A mock {@link IMessageRepository} repository */
    private IMessageRepository mockMessageRepo;
    
    /** This is what is being tested */
    private MessageService messageService;

    
    /**
     * Initializes mock dependencies before each test.
     */
    @BeforeEach
    public void setup() {
    	mockMessageRepo = Mockito.mock(IMessageRepository.class);
    	messageService = new MessageService(mockMessageRepo);
    }

    /**
     * Tests that inserting a message correctly accesses the message repository.
     */
    @Test
    public void InsertMessage_ReturnsMessage() throws Exception {
        Message input = new Message(1, 2, 3, 4, 5, "hello", new Timestamp(System.currentTimeMillis()));
        when(mockMessageRepo.insertMessage(input)).thenReturn(input);

        Message result = messageService.insertMessage(input);

        assertEquals(input, result);
        verify(mockMessageRepo, times(1)).insertMessage(input);
    }
    
    /**
     * Tests that InsertMessage properly handles SQLExceptions.
     */
    @Test
    public void InsertMessage_WhenSQLException_ThrowsDataAccessException() throws SQLException {
        Message input = new Message(1, 2, 3, 4, 5, "hello", new Timestamp(System.currentTimeMillis()));
        when(mockMessageRepo.insertMessage(input)).thenThrow(new SQLException("Insert failed"));

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            messageService.insertMessage(input);
        });

        assertTrue(thrown.getMessage().contains("Failed to insert message"));
    }
    
    /**
     * Tests GetMessagesForUsername returns a correct list when given correct parameters
     */
    @Test
    public void GetMessagesForUsername_WhenFound_ReturnsList() throws Exception {
        String username = "daniel";
        List<Message> expected = Arrays.asList(
    		new Message(1, 2, 3, 4, 5, "hello1", new Timestamp(System.currentTimeMillis())),
    		new Message(1, 2, 3, 4, 5, "hello2", new Timestamp(System.currentTimeMillis()))
		);
        when(mockMessageRepo.getMessagesForUsername(username)).thenReturn(expected);

        List<Message> result = messageService.getMessagesForUsername(username);

        assertEquals(2, result.size());
        verify(mockMessageRepo, times(1)).getMessagesForUsername(username);
    }

    /**
     * Tests GetMessagesForUsername handles SQLExceptions correctly
     */
    @Test
    public void GetMessagesForUsername_WhenSQLException_ThrowsDataAccessException() throws Exception {
        String username = "Yevgeniy";
        when(mockMessageRepo.getMessagesForUsername(username))
        	.thenThrow(new SQLException("DB error"));

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            messageService.getMessagesForUsername(username);
        });

        assertTrue(thrown.getMessage().contains("Failed to get messages for username"));
    }

    /**
     * Tests GetMessagesByReviewId returns a correct list when given correct parameters
     */
    @Test
    public void GetMessagesByReviewId_WhenFound_ReturnsList() throws Exception {
        int reviewId = 5;
        List<Message> expected = List.of(new Message(1, 2, 3, 4, 5, "hello1", new Timestamp(System.currentTimeMillis())));
        when(mockMessageRepo.getMessagesByReviewId(reviewId)).thenReturn(expected);

        List<Message> result = messageService.getMessagesByReviewId(reviewId);

        assertEquals(1, result.size());
        verify(mockMessageRepo, times(1)).getMessagesByReviewId(reviewId);
    }

    /**
     * Tests GetMessagesByReviewId handles SQLExceptions correctly
     */
    @Test
    public void GetMessagesByReviewId_WhenSQLException_ThrowsDataAccessException() throws Exception {
        int reviewId = 5;
        when(mockMessageRepo.getMessagesByReviewId(reviewId))
        	.thenThrow(new SQLException("DB error"));

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            messageService.getMessagesByReviewId(reviewId);
        });

        assertTrue(thrown.getMessage().contains("Failed to get messages for review id"));
    }
    
    /**
     * Tests UpdateMessage correctly accesses message repository
     */
    @Test
    public void UpdateMessage_WhenMessageFound_ReturnsTrue() throws Exception {
        int messageId = 10;
        String newContent = "Updated message";
        when(mockMessageRepo.updateMessage(messageId, newContent)).thenReturn(true);

        boolean result = messageService.updateMessage(messageId, newContent);

        assertTrue(result);
        verify(mockMessageRepo, times(1)).updateMessage(messageId, newContent);
    }

    /**
     * Tests UpdateMessage throws exception when given empty content
     */
    @Test
    public void UpdateMessage_WithEmptyContent_ThrowsIllegalArgumentException() {
        int messageId = 10;
        
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.updateMessage(messageId, "   ");
        });
    }

    /**
     * Tests UpdateMessage throws exception when receiving and exception from repository
     */
    @Test
    public void UpdateMessage_WhenSQLException_ThrowsDataAccessException() throws Exception {
        int messageId = 10;
        String content = "Something";
        when(mockMessageRepo.updateMessage(messageId, content))
        	.thenThrow(new SQLException("DB failure"));

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            messageService.updateMessage(messageId, content);
        });

        assertTrue(thrown.getMessage().contains("Failed to update message"));
    }

    /**
     * Tests DeleteMessage successfully touches message repository.
     */
    @Test
    public void DeleteMessage_WithValidMessage_ReturnsTrue() throws Exception {
        int messageId = 55;
        when(mockMessageRepo.deleteMessageById(messageId)).thenReturn(true);

        boolean result = messageService.deleteMessage(messageId);

        assertTrue(result);
        verify(mockMessageRepo, times(1)).deleteMessageById(messageId);
    }

    /**
     * Tests DeleteMessage throws DataAccessException when receiving SQL Exception
     */
    @Test
    public void DeleteMessage_WhenSQLException_ThrowsDataAccessException() throws Exception {
        int messageId = 42;
        when(mockMessageRepo.deleteMessageById(messageId))
        	.thenThrow(new SQLException("Delete failed"));

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            messageService.deleteMessage(messageId);
        });

        assertTrue(thrown.getMessage().contains("Failed to delete message id"));
    }
}
