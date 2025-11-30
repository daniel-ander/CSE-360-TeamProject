package Tests;

import Models.Question;
import Models.DBModels.UserDTO;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import Tests.Utils.*;


/**
 * Unit tests for the Question model class.
 */
public class QuestionTest {
	
	private UserDTO userDTOModel = Utils.getUserDTOModel();
	
    /**
     * Tests that the constructor creates a Question object with valid inputs.
     */
    @Test
    public void Constructor__WithValidInputs_CreatesQuestion() {
        Question q = new Question(userDTOModel, "Title", "This is a question text");
        
        assertNull(q.getId()); // null since not yet in DB
        assertEquals(1, q.getAuthor().getId());
        assertEquals("Title", q.getTitleText());
        assertEquals("This is a question text", q.getBodyText());
        assertFalse(q.getAnsweredStatus());
        assertFalse(q.getResolvedStatus());
        assertFalse(q.getIsDeleted());
    }

    /**
     * Tests that the constructor throws an exception when given an empty string for text.
     */
    @Test
    public void Constructor_WithEmptyString_ThrowsException() {
        Exception testError = assertThrows(IllegalArgumentException.class,
                () -> new Question(userDTOModel, "Title", " "));
            
            assertEquals("Text is empty", testError.getMessage());
    }
    
    /**
     * Tests that setText updates the question body when given a valid string.
     */
    @Test
    public void SetText_WithValidString_ChangesText() {
        Question q = new Question(userDTOModel, "Title", "Old question");
        
        q.setText("New question");
        
        assertEquals("New question", q.getBodyText());
    }

    /**
     * Tests that setText throws an exception when given an empty string.
     */
    @Test
    public void SetText_WithEmptyString_ThrowsException() {
        Question q = new Question(userDTOModel, "Title", "QuestionText");
        
        Exception testError = assertThrows(IllegalArgumentException.class,
                () -> q.setText(" "));
        
        assertEquals("Text is empty", testError.getMessage());
    }
}
