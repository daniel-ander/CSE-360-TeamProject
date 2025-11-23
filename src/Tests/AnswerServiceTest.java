package Tests;

import Database.Services.AnswerService;
import Database.Repositories.Interfaces.IAnswerRepository;
import Models.Answer;
import Exceptions.DBExceptions.DataAccessException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AnswerService class.
 */
public class AnswerServiceTest {

    private IAnswerRepository mockRepo;
    private AnswerService answerService;

    /**
     * Sets up a mocked repository and initializes the AnswerService before each test.
     */
    @BeforeEach
    public void setUp() {
        mockRepo = mock(IAnswerRepository.class);
        answerService = new AnswerService(mockRepo);
    }

    /**
     * Tests adding a valid answer returns success and calls the repository once.
     */
    @Test
    public void AddAnswer_WithValidAnswer_ReturnsSuccess() throws Exception {
        Answer a = new Answer(1, 1, "Author", "Mock Answer");

        when(mockRepo.insertAnswer(a)).thenReturn(a);

        Answer result = answerService.addAnswer(a);

        assertNotNull(result);
        verify(mockRepo, times(1)).insertAnswer(a);
    }
    
    /**
     * Tests adding an answer when a SQLException occurs throws a DataAccessException.
     */
    @Test
    public void AddAnswer_WhenSQLException_ThrowsDataAccessException() throws Exception {
        Answer a = new Answer(1, 1, "Author", "Mock Answer");
        when(mockRepo.insertAnswer(a)).thenThrow(new SQLException("DB error"));

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            answerService.addAnswer(a);
        });
        assertTrue(ex.getMessage().contains("Failed to add answer"));
        verify(mockRepo, times(1)).insertAnswer(a);
    }

    /**
     * Tests retrieving all answers returns a list of answers.
     */
    @Test
    public void GetAllAnswers_ReturnsList() throws Exception {
        List<Answer> answers = Arrays.asList(
                new Answer(1, 1, 0, 1, "Author1", "A1", new Timestamp(0), new Timestamp(0), BigDecimal.valueOf(0), 0, true, false, false),
                new Answer(2, 1, 0, 2, "Author2", "A2", new Timestamp(0), new Timestamp(0), BigDecimal.valueOf(0), 0, false, false, false)
        );
        when(mockRepo.getAll()).thenReturn(answers);

        List<Answer> result = answerService.getAllAnswers();
        
        assertEquals(2, result.size());
        verify(mockRepo, times(1)).getAll();
    }

    /**
     * Tests retrieving all answers for a specific question returns the correct list.
     */
    @Test
    public void GetAllAnswersForQuestion_ReturnsList() throws Exception {
        List<Answer> answers = Arrays.asList(
                new Answer(1, 1, 0, 1, "Author", "A1", new Timestamp(0), new Timestamp(0), BigDecimal.valueOf(0), 0, true, false, false)
        );
        when(mockRepo.getAnswersByQuestionId(1)).thenReturn(answers);

        List<Answer> result = answerService.getAllAnswersForQuestion(1);
        
        assertEquals(1, result.size());
        verify(mockRepo, times(1)).getAnswersByQuestionId(1);
    }

    /**
     * Tests retrieving correct answers returns only answers marked as correct.
     */
    @Test
    public void GetCorrectAnswers_ReturnsList() throws Exception {
        List<Answer> answers = Arrays.asList(
                new Answer(1, 1, 0, 1, "Author", "A1", new Timestamp(0), new Timestamp(0), BigDecimal.valueOf(0), 0, true, false, false)
        );
        when(mockRepo.getCorrectAnswers()).thenReturn(answers);

        List<Answer> result = answerService.getCorrectAnswers();
        
        assertEquals(1, result.size());
        verify(mockRepo, times(1)).getCorrectAnswers();
    }

    /**
     * Tests searching for answers by text returns the correct list.
     */
    @Test
    public void GetAnswersBySearch_ReturnsList() throws Exception {
        List<Answer> answers = Arrays.asList(
                new Answer(1, 1, 0, 1, "Author", "A1", new Timestamp(0), new Timestamp(0), BigDecimal.valueOf(0), 0, true, false, false)
        );
        when(mockRepo.getAnswersBySearch("A1")).thenReturn(answers);

        List<Answer> result = answerService.getAnswersBySearch("A1");
        
        assertEquals(1, result.size());
        verify(mockRepo, times(1)).getAnswersBySearch("A1");
    }

    /**
     * Tests retrieving an answer by ID returns the correct answer.
     */
    @Test
    public void GetAnswerById_ReturnsAnswer() throws Exception {
        Answer a = new Answer(1, 1, 0, 1, "Author", "A1", new Timestamp(0), new Timestamp(0), BigDecimal.valueOf(0), 0, true, false, false);
        when(mockRepo.getAnswerById(1)).thenReturn(a);

        Answer result = answerService.getAnswerById(1);
        
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(mockRepo, times(1)).getAnswerById(1);
    }

    /**
     * Tests updating an answer returns true and updates the answer content and correctness.
     */
    @Test
    public void UpdateAnswer_ReturnsTrue() throws Exception {
        Answer a = new Answer(1, 1, 0, 1, "Author", "Old", new Timestamp(0), new Timestamp(0), BigDecimal.valueOf(0), 0, false, false, false);
        when(mockRepo.getAnswerById(1)).thenReturn(a);
        when(mockRepo.updateAnswer(1, "New", true)).thenReturn(true);

        boolean result = answerService.updateAnswer(1, "New", true);
        
        assertTrue(result);
        assertEquals("New", a.getBodyText());
        assertTrue(a.isCorrect());
        verify(mockRepo, times(1)).updateAnswer(1, "New", true);
    }

    /**
     * Tests updating an answer with empty text throws IllegalArgumentException.
     */
    @Test
    public void UpdateAnswer_WithEmptyText_ThrowsIllegalArgument() throws Exception {
        Answer a = new Answer(1, 1, 0, 1, "Author", "Old", new Timestamp(0), new Timestamp(0), BigDecimal.valueOf(0), 0, false, false, false);
        when(mockRepo.getAnswerById(1)).thenReturn(a);

        assertThrows(IllegalArgumentException.class, () -> {
            answerService.updateAnswer(1, "   ", true);
        });
    }

    /**
     * Tests updating a non-existent answer returns false.
     */
    @Test
    public void UpdateAnswer_Nonexistent_ReturnsFalse() throws Exception {
        when(mockRepo.getAnswerById(999)).thenReturn(null);

        boolean result = answerService.updateAnswer(999, "New", true);
        assertFalse(result);
    }

    /**
     * Tests deleting an answer by ID returns true.
     */
    @Test
    public void DeleteAnswer_ReturnsTrue() throws Exception {
        when(mockRepo.deleteAnswerById(1)).thenReturn(true);

        boolean result = answerService.deleteAnswer(1);
        
        assertTrue(result);
        verify(mockRepo, times(1)).deleteAnswerById(1);
    }

    /**
     * Tests deleting answers by question ID returns true.
     */
    @Test
    public void DeleteByQuestionId_ReturnsTrue() throws Exception {
        when(mockRepo.deleteByQuestionId(1)).thenReturn(true);

        boolean result = answerService.deleteByQuestionId(1);
        
        assertTrue(result);
        verify(mockRepo, times(1)).deleteByQuestionId(1);
    }
    
}
