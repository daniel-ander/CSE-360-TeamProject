package Tests;

import Database.Services.AnswerService;
import Database.Repositories.Interfaces.IAnswerRepository;
import Models.Answer;
import Exceptions.DBExceptions.DataAccessException;
import Exceptions.DBExceptions.DuplicateRecordIDException;

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
    public void testAddAnswerSuccess() throws SQLException, DataAccessException, DuplicateRecordIDException {
        Answer a = new Answer(1, 1, "Mock Answer");

        when(mockRepo.insertAnswer(a)).thenReturn(true);

        boolean result = answerService.addAnswer(a);
        assertTrue(result);
        verify(mockRepo, times(1)).insertAnswer(a);
    }

    @Test
    public void testAddAnswerSQLExceptionThrowsDataAccessException() throws SQLException {
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
    public void testGetAllAnswersSuccess() throws SQLException, DataAccessException {
        List<Answer> answers = Arrays.asList(
                new Answer(1, 1, 0, 1, "A1", new Timestamp(System.currentTimeMillis()), 0, true, false, 0),
                new Answer(2, 1, 0, 2, "A2", new Timestamp(System.currentTimeMillis()), 0, false, false, 0)
        );
        when(mockRepo.getAll()).thenReturn(answers);

        List<Answer> result = answerService.getAllAnswers();
        assertEquals(2, result.size());
        verify(mockRepo, times(1)).getAll();
    }

    @Test
    public void testGetAllAnswersForQuestionSuccess() throws SQLException, DataAccessException {
        List<Answer> answers = Arrays.asList(
                new Answer(1, 1, 0, 1, "A1", new Timestamp(System.currentTimeMillis()), 0, true, false, 0)
        );
        when(mockRepo.getAnswersByQuestionId(1)).thenReturn(answers);

        List<Answer> result = answerService.getAllAnswersForQuestion(1);
        assertEquals(1, result.size());
        verify(mockRepo, times(1)).getAnswersByQuestionId(1);
    }

    @Test
    public void testGetCorrectAnswersSuccess() throws SQLException, DataAccessException {
        List<Answer> answers = Arrays.asList(
                new Answer(1, 1, 0, 1, "A1", new Timestamp(System.currentTimeMillis()), 0, true, false, 0)
        );
        when(mockRepo.getCorrectAnswers()).thenReturn(answers);

        List<Answer> result = answerService.getCorrectAnswers();
        assertEquals(1, result.size());
        verify(mockRepo, times(1)).getCorrectAnswers();
    }

    @Test
    public void testGetAnswersBySearchSuccess() throws SQLException, DataAccessException {
        List<Answer> answers = Arrays.asList(
                new Answer(1, 1, 0, 1, "A1", new Timestamp(System.currentTimeMillis()), 0, true, false, 0)
        );
        when(mockRepo.getAnswersBySearch("A1")).thenReturn(answers);

        List<Answer> result = answerService.getAnswersBySearch("A1");
        assertEquals(1, result.size());
        verify(mockRepo, times(1)).getAnswersBySearch("A1");
    }

    @Test
    public void testGetAnswerByIdSuccess() throws SQLException, DataAccessException {
        Answer a = new Answer(1, 1, 0, 1, "A1", new Timestamp(System.currentTimeMillis()), 0, true, false, 0);
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
    public void testUpdateAnswerSuccess() throws SQLException, DataAccessException {
        Answer a = new Answer(1, 1, 0, 1, "Old", new Timestamp(System.currentTimeMillis()), 0, false, false, 0);
        when(mockRepo.getAnswerById(1)).thenReturn(a);
        when(mockRepo.updateAnswer(1, "New", true)).thenReturn(true);

        boolean result = answerService.updateAnswer(1, "New", true);
        assertTrue(result);
        assertEquals("New", a.getText());
        assertTrue(a.isCorrect());
        verify(mockRepo, times(1)).updateAnswer(1, "New", true);
    }

    @Test
    public void testUpdateAnswerWithEmptyTextThrowsIllegalArgument() throws SQLException, DataAccessException {
        Answer a = new Answer(1, 1, 0, 1, "Old", new Timestamp(System.currentTimeMillis()), 0, false, false, 0);
        when(mockRepo.getAnswerById(1)).thenReturn(a);

        assertThrows(IllegalArgumentException.class, () -> {
            answerService.updateAnswer(1, "   ", true);
        });
    }

    @Test
    public void testUpdateAnswerNonexistentReturnsFalse() throws SQLException, DataAccessException {
        when(mockRepo.getAnswerById(999)).thenReturn(null);

        boolean result = answerService.updateAnswer(999, "New", true);
        assertFalse(result);
    }

    // =====================
    //       DELETE
    // =====================
    @Test
    public void testDeleteAnswerSuccess() throws SQLException, DataAccessException {
        when(mockRepo.deleteAnswerById(1)).thenReturn(true);

        boolean result = answerService.deleteAnswer(1);
        assertTrue(result);
        verify(mockRepo, times(1)).deleteAnswerById(1);
    }

    @Test
    public void testDeleteByQuestionIdSuccess() throws SQLException, DataAccessException {
        when(mockRepo.deleteByQuestionId(1)).thenReturn(true);

        boolean result = answerService.deleteByQuestionId(1);
        assertTrue(result);
        verify(mockRepo, times(1)).deleteByQuestionId(1);
    }
}
