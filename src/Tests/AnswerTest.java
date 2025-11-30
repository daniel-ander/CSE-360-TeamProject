package Tests;

import Models.Answer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Answer model class.
 */
public class AnswerTest {
	
    /**
     * Tests that the constructor creates an Answer object with valid inputs.
     * Ensures fields are set correctly and default values are applied.
     */
    @Test
    public void Constructor__WithValidInputs_CreatesAnswer() {
    	Answer a = new Answer(-1, 1, "This is an answer text");
    	
    	assertNull(a.getId());
    	assertEquals(-1, a.getQuestionId());
    	assertEquals("This is an answer text", a.getBodyText());
    	assertFalse(a.isCorrect());
    }

    /**
     * Tests that the constructor throws an exception when given an empty string for text.
     */
    @Test
    public void Constructor_WithEmptyString_ThrowsException() {
        Exception testError = assertThrows(IllegalArgumentException.class,
            () -> new Answer(2, 1, " "));
        
        assertEquals("Text is empty", testError.getMessage());
    }

    /**
     * Tests that setText updates the answer body when given a valid string.
     */
    @Test
    public void SetText_WithValidString_ChangesText() {
    	Answer a = new Answer(1, 1, "Old answer");
        
        a.setText("New answer");
        
        assertEquals("New answer", a.getBodyText());
    }

    /**
     * Tests that setText throws an exception when given an empty string.
     */
    @Test
    public void SetText_WithEmptyString_ThrowsException() {
    	Answer a = new Answer(1, 1, "Old answer");
        
        Exception testError = assertThrows(IllegalArgumentException.class, 
    		() -> a.setText(" "));
        
        assertEquals("Text is empty", testError.getMessage());
    }
}
