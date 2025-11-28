package Tests;


import Models.Question;
import Models.DBModels.UserDTO;
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

import Tests.Utils.*;


/**
 * Unit tests for the QuestionService class.
 */
public class QuestionServiceTest {
	
    private IQuestionRepository questionRepo;
    private QuestionService questionService;
    private UserDTO userDTOModel = Utils.getUserDTOModel();

    /**
     * Sets up a mocked repository and initializes the QuestionService before each test.
     */
    @BeforeEach
    public void setUp() {
        questionRepo = mock(IQuestionRepository.class);
        questionService = new QuestionService(questionRepo);
    }
    
    /**
     * Tests adding a valid question returns true and calls the repository once.
     */
    @Test
    public void AddQuestion_WithValidQuestion_AddsSuccessfully() throws SQLException {
        Question q = new Question(userDTOModel, "Title", "Text");
//        q.setId(-1);
        when(questionRepo.insertQuestion(q)).thenReturn(true);

        boolean result = questionService.addQuestion(q);

        assertTrue(result);
        verify(questionRepo, times(1)).insertQuestion(q);
    }

    /**
     * Tests adding a question throws a DataAccessException
     * when a SQLException occurs in the repository.
     */
    @Test
    public void AddQuestion_WhenSQLException_ThrowsDataAccessException() throws Exception {
        Question q = new Question(userDTOModel, "Title", "Text");
        q.setId(-1);
        when(questionRepo.getQuestionById(q.getId())).thenReturn(null);
        doThrow(new SQLException("DB Error")).when(questionRepo).insertQuestion(any());

        DataAccessException ex = assertThrows(DataAccessException.class, () -> questionService.addQuestion(q));
        assertTrue(ex.getMessage().contains("Could not add the question"));
    }
	
    /**
     * Tests retrieving a question by valid ID returns the expected question.
     */
    @Test
    public void GetById_WithValidQuestionId_ReturnsQuestion() throws SQLException {
        Question q = new Question(userDTOModel, "HelloTitle?", "HelloText?");
        when(questionRepo.getQuestionById(1)).thenReturn(q);
        
        Question result = questionService.getQuestionById(1);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(1, result.getAuthor().getId());
        assertEquals("HelloTitle?", result.getTitleText());
        assertEquals("HelloText?", result.getBodyText());
        verify(questionRepo, times(1)).getQuestionById(1);
    }
    
    /**
     * Tests retrieving a question by invalid ID returns null.
     */
    @Test
    public void GetById_WithInvalidQuestionId_ReturnsNull() {
        Question result = questionService.getQuestionById(1); // id 1 is not valid, returns null
        
        assertNull(result);
    }
    
    /**
     * Tests retrieving a question throws a DataAccessException
     * when a SQLException occurs in the repository.
     */
    @Test
    public void GetById_WhenSQLException_ThrowsDataAccessException() throws Exception {
        when(questionRepo.getQuestionById(1)).thenThrow(new SQLException("DB error"));

        assertThrows(DataAccessException.class, () -> questionService.getQuestionById(1));
    }

    /**
     * Tests retrieving all questions returns a list of questions.
     */
    @Test
    public void GetAllQuestions_ReturnsList() throws Exception {
        List<Question> sampleList = new ArrayList<>();
        sampleList.add(new Question(1, userDTOModel, "T", "B", null, null, false, false, false, 0));
        when(questionRepo.getAllQuestions()).thenReturn(sampleList);

        List<Question> result = questionService.getAllQuestions();

        assertEquals(1, result.size());
        verify(questionRepo, times(1)).getAllQuestions();
    }

    /**
     * Tests retrieving all questions throws a DataAccessException
     * when a SQLException occurs in the repository.
     */
    @Test
    public void GetAllQuestions_WhenSQLException_ThrowsDataAccessException() throws Exception {
        when(questionRepo.getAllQuestions()).thenThrow(new SQLException("DB error"));

        assertThrows(DataAccessException.class, () -> questionService.getAllQuestions());
    }

    /**
     * Tests updating a question with valid data returns true.
     */
    @Test
    public void UpdateQuestion_WithValidData_ReturnsTrue() throws SQLException {
    	when(questionRepo.updateQuestion(1, "Updated Title", "Updated Text")).thenReturn(true);

        boolean result = questionService.updateQuestion(1, "Updated Title", "Updated Text");
        
        assertTrue(result);
        verify(questionRepo, times(1)).updateQuestion(1, "Updated Title", "Updated Text");
    }

    /**
     * Tests updating a question with empty text throws IllegalArgumentException.
     */
    @Test
    public void UpdateQuestion_WithEmptyText_ThrowsIllegalArgumentException() throws SQLException {
    	assertThrows(IllegalArgumentException.class, () -> questionService.updateQuestion(1, "Updated Title", " "));
        verify(questionRepo, never()).updateQuestion(anyInt(), anyString(), anyString());
    }
    
    /**
     * Tests updating a question throws a DataAccessException
     * when a SQLException occurs in the repository.
     */
    @Test
    public void UpdateQuestion_WhenSQLException_ThrowsDataAccessException() throws Exception {
    	doThrow(new SQLException("Test DB Fail")).when(questionRepo).updateQuestion(anyInt(), anyString(),  anyString());
    	assertThrows(DataAccessException.class, () -> questionService.updateQuestion(1, "Updated Title", "Test Text"));
    }
    
    /**
     * Tests deleting a question with a valid ID returns true.
     */
    @Test
    public void DeleteQuestion_WithValidId_ReturnsTrue() throws SQLException {
    	when(questionRepo.deleteQuestion(1)).thenReturn(true);
    	
    	boolean result = questionService.deleteQuestion(1);
    	
    	assertTrue(result);
    	verify(questionRepo, times(1)).deleteQuestion(1);
    }
    
    /**
     * Tests deleting a question throws a DataAccessException
     * when a SQLException occurs in the repository.
     */
    @Test
    public void DeleteQuestion_WhenSQLException_ThrowsDataAccessException() throws SQLException {
    	doThrow(new SQLException("Test DB Fail")).when(questionRepo).deleteQuestion(1);

    	assertThrows(DataAccessException.class, () -> questionService.deleteQuestion(1));
    }
}
