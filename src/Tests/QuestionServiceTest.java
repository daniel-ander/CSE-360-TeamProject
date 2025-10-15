package Tests;


import Models.Question;
import Database.Repositories.Interfaces.IQuestionRepository;
import Database.Services.QuestionService;
import Exceptions.DBExceptions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class QuestionServiceTest {
	
    private IQuestionRepository questionRepo;
    private QuestionService questionService;

    @BeforeEach
    public void setUp() {
        questionRepo = mock(IQuestionRepository.class);
        questionService = new QuestionService(questionRepo);
    }
    
    // CREATE TESTS
    @Test
    public void AddQuestion_WithValidQuestion_AddsSuccessfully() throws SQLException {
        Question q = new Question(1, "Title", "Text");
        when(questionRepo.insertQuestion(q)).thenReturn(true);

        boolean result = questionService.addQuestion(q);

        assertTrue(result);
        verify(questionRepo, times(1)).insertQuestion(q);
    }
    
    @Test
    public void AddQuestion_WhenDuplicateId_ThrowsDuplicateRecordIDException() throws Exception {
        Question q = new Question(1, "Title", "Text");
        when(questionRepo.getQuestionById(q.getId())).thenReturn(q);

        assertThrows(DuplicateRecordIDException.class, () -> questionService.addQuestion(q));
        verify(questionRepo, never()).insertQuestion(any());
    }

    @Test
    public void AddQuestion_WhenSQLException_ThrowsDataAccessException() throws Exception {
        Question q = new Question(1, "Title", "Text");
        when(questionRepo.getQuestionById(q.getId())).thenReturn(null);
        doThrow(new SQLException("DB Error")).when(questionRepo).insertQuestion(any());

        DataAccessException ex = assertThrows(DataAccessException.class, () -> questionService.addQuestion(q));
        assertTrue(ex.getMessage().contains("Could not add the question"));
    }
	
	// READ TESTS
    @Test
    public void GetById_WithValidQuestionId_ReturnsQuestion() throws SQLException {
        Question q = new Question(1, "HelloTitle?", "HelloText?");
        when(questionRepo.getQuestionById(1)).thenReturn(q);
        
        Question result = questionService.getQuestionById(1);

        assertNotNull(result);
        assertEquals("HelloTitle?", result.getTitle());
        assertEquals("HelloText?", result.getText());
        assertEquals(-1, result.getId());
        verify(questionRepo, times(1)).getQuestionById(1);
    }
    
    @Test
    public void GetById_WithInvalidQuestionId_ReturnsNull() {
        Question result = questionService.getQuestionById(1); // id 1 is not valid, returns null
        
        assertNull(result);
    }
    
    @Test
    public void GetById_WhenSQLException_ThrowsDataAccessException() throws Exception {
        when(questionRepo.getQuestionById(1)).thenThrow(new SQLException("DB error"));

        assertThrows(DataAccessException.class, () -> questionService.getQuestionById(1));
    }

    @Test
    public void GetAllQuestions_ReturnsList() throws Exception {
        List<Question> sampleList = new ArrayList<>();
        sampleList.add(new Question(1, 1, "T", "B", false, false, false, 0));
        when(questionRepo.getAllQuestions()).thenReturn(sampleList);

        List<Question> result = questionService.getAllQuestions();

        assertEquals(1, result.size());
        verify(questionRepo, times(1)).getAllQuestions();
    }

    @Test
    public void GetAllQuestions_WhenSQLException_ThrowsDataAccessException() throws Exception {
        when(questionRepo.getAllQuestions()).thenThrow(new SQLException("DB error"));

        assertThrows(DataAccessException.class, () -> questionService.getAllQuestions());
    }

    
    // UPDATE TESTS
    @Test
    public void UpdateQuestion_WithValidData_ReturnsTrue() throws SQLException {
    	when(questionRepo.updateQuestion(1, "Updated Text")).thenReturn(true);

        boolean result = questionService.updateQuestion(1, "Updated Text");
        
        assertTrue(result);
        verify(questionRepo, times(1)).updateQuestion(1, "Updated Text");
    }

    @Test
    public void UpdateQuestion_WithEmptyText_ThrowsIllegalArgumentException() throws SQLException {
    	assertThrows(IllegalArgumentException.class, () -> questionService.updateQuestion(1,  " "));
        verify(questionRepo, never()).updateQuestion(anyInt(), anyString());
    }
    
    @Test
    public void UpdateQuestion_WhenSQLException_ThrowsDataAccessException() throws Exception {
    	doThrow(new SQLException("Test DB Fail")).when(questionRepo).updateQuestion(anyInt(), anyString());
    	assertThrows(DataAccessException.class, () -> questionService.updateQuestion(1,  "Test Text"));
    }
    
    // DELETE TESTS
    @Test
    public void DeleteQuestion_WithValidId_ReturnsTrue() throws SQLException {
    	when(questionRepo.deleteQuestion(1)).thenReturn(true);
    	
    	boolean result = questionService.deleteQuestion(1);
    	
    	assertTrue(result);
    	verify(questionRepo, times(1)).deleteQuestion(1);
    }
    
    @Test
    public void DeleteQuestion_WhenSQLException_ThrowsDataAccessException() throws SQLException {
    	doThrow(new SQLException("Test DB Fail")).when(questionRepo).deleteQuestion(1);

    	assertThrows(DataAccessException.class, () -> questionService.deleteQuestion(1));
    }
}
