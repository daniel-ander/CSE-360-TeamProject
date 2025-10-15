package Tests;

import Database.Services.AnswerService;
import Database.Repositories.Interfaces.IAnswerRepository;
import Models.Answer;
import Exceptions.DBExceptions.DataAccessException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AnswerServiceTest {

    private IAnswerRepository mockRepo;
    private AnswerService answerService;

    @BeforeEach
    public void setUp() {
        mockRepo = mock(IAnswerRepository.class);
        answerService = new AnswerService(mockRepo);
    }

    // =====================
    //       CREATE
    // =====================
    @Test
    public void AddAnswer_WithValidAnswer_ReturnsSuccess() throws Exception {
        Answer a = new Answer(1, 1, "Mock Answer");

        when(mockRepo.insertAnswer(a)).thenReturn(true);

        boolean result = answerService.addAnswer(a);
        
        assertTrue(result);
        verify(mockRepo, times(1)).insertAnswer(a);
    }

    @Test
    public void AddAnswer_WhenSQLException_ThrowsDataAccessException() throws Exception {
        Answer a = new Answer(1, 1, "Mock Answer");
        when(mockRepo.insertAnswer(a)).thenThrow(new SQLException("DB error"));

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            answerService.addAnswer(a);
        });
        assertTrue(ex.getMessage().contains("Failed to add answer"));
        verify(mockRepo, times(1)).insertAnswer(a);
    }

    // =====================
    //        READ
    // =====================
    @Test
    public void GetAllAnswers_ReturnsList() throws Exception {
        List<Answer> answers = Arrays.asList(
                new Answer(1, 1, 0, 1, "A1", new Timestamp(0), 0, true, false, 0),
                new Answer(2, 1, 0, 2, "A2", new Timestamp(0), 0, false, false, 0)
        );
        when(mockRepo.getAll()).thenReturn(answers);

        List<Answer> result = answerService.getAllAnswers();
        
        assertEquals(2, result.size());
        verify(mockRepo, times(1)).getAll();
    }

    @Test
    public void GetAllAnswersForQuestion_ReturnsList() throws Exception {
        List<Answer> answers = Arrays.asList(
                new Answer(1, 1, 0, 1, "A1", new Timestamp(0), 0, true, false, 0)
        );
        when(mockRepo.getAnswersByQuestionId(1)).thenReturn(answers);

        List<Answer> result = answerService.getAllAnswersForQuestion(1);
        
        assertEquals(1, result.size());
        verify(mockRepo, times(1)).getAnswersByQuestionId(1);
    }

    @Test
    public void GetCorrectAnswers_ReturnsList() throws Exception {
        List<Answer> answers = Arrays.asList(
                new Answer(1, 1, 0, 1, "A1", new Timestamp(0), 0, true, false, 0)
        );
        when(mockRepo.getCorrectAnswers()).thenReturn(answers);

        List<Answer> result = answerService.getCorrectAnswers();
        
        assertEquals(1, result.size());
        verify(mockRepo, times(1)).getCorrectAnswers();
    }

    @Test
    public void GetAnswersBySearch_ReturnsList() throws Exception {
        List<Answer> answers = Arrays.asList(
                new Answer(1, 1, 0, 1, "A1", new Timestamp(0), 0, true, false, 0)
        );
        when(mockRepo.getAnswersBySearch("A1")).thenReturn(answers);

        List<Answer> result = answerService.getAnswersBySearch("A1");
        
        assertEquals(1, result.size());
        verify(mockRepo, times(1)).getAnswersBySearch("A1");
    }

    @Test
    public void GetAnswerById_ReturnsAnswer() throws Exception {
        Answer a = new Answer(1, 1, 0, 1, "A1", new Timestamp(0), 0, true, false, 0);
        when(mockRepo.getAnswerById(1)).thenReturn(a);

        Answer result = answerService.getAnswerById(1);
        
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(mockRepo, times(1)).getAnswerById(1);
    }

    // =====================
    //        UPDATE
    // =====================
    @Test
    public void UpdateAnswer_ReturnsTrue() throws Exception {
        Answer a = new Answer(1, 1, 0, 1, "Old", new Timestamp(0), 0, false, false, 0);
        when(mockRepo.getAnswerById(1)).thenReturn(a);
        when(mockRepo.updateAnswer(1, "New", true)).thenReturn(true);

        boolean result = answerService.updateAnswer(1, "New", true);
        
        assertTrue(result);
        assertEquals("New", a.getText());
        assertTrue(a.isCorrect());
        verify(mockRepo, times(1)).updateAnswer(1, "New", true);
    }

    @Test
    public void UpdateAnswer_WithEmptyText_ThrowsIllegalArgument() throws Exception {
        Answer a = new Answer(1, 1, 0, 1, "Old", new Timestamp(0), 0, false, false, 0);
        when(mockRepo.getAnswerById(1)).thenReturn(a);

        assertThrows(IllegalArgumentException.class, () -> {
            answerService.updateAnswer(1, "   ", true);
        });
    }

    @Test
    public void UpdateAnswer_Nonexistent_ReturnsFalse() throws Exception {
        when(mockRepo.getAnswerById(999)).thenReturn(null);

        boolean result = answerService.updateAnswer(999, "New", true);
        assertFalse(result);
    }

    // =====================
    //       DELETE
    // =====================
    @Test
    public void DeleteAnswer_ReturnsTrue() throws Exception {
        when(mockRepo.deleteAnswerById(1)).thenReturn(true);

        boolean result = answerService.deleteAnswer(1);
        
        assertTrue(result);
        verify(mockRepo, times(1)).deleteAnswerById(1);
    }

    @Test
    public void DeleteByQuestionId_ReturnsTrue() throws Exception {
        when(mockRepo.deleteByQuestionId(1)).thenReturn(true);

        boolean result = answerService.deleteByQuestionId(1);
        
        assertTrue(result);
        verify(mockRepo, times(1)).deleteByQuestionId(1);
    }
}
