package Tests;

import Models.Answer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnswerTest {
	
	// CREATE TESTS
    @Test
    public void Constructor__WithValidInputs_CreatesAnswer() {
    	Answer a = new Answer(-1, 1, "Author", "This is an answer text");
    	
    	assertNull(a.getId());
    	assertEquals(-1, a.getQuestionId());
    	assertEquals("This is an answer text", a.getBodyText());
    	assertFalse(a.isCorrect());
    }

    @Test
    public void Constructor_WithEmptyString_ThrowsException() {
        Exception testError = assertThrows(IllegalArgumentException.class,
            () -> new Answer(2, 1, "Author", " "));
        
        assertEquals("Text is empty", testError.getMessage());
    }

    // UPDATE TESTS
    @Test
    public void SetText_WithValidString_ChangesText() {
    	Answer a = new Answer(1, 1, "Author", "Old answer");
        
        a.setText("New answer");
        
        assertEquals("New answer", a.getBodyText());
    }

    @Test
    public void SetText_WithEmptyString_ThrowsException() {
    	Answer a = new Answer(1, 1, "Author", "Old answer");
        
        Exception testError = assertThrows(IllegalArgumentException.class, 
    		() -> a.setText(" "));
        
        assertEquals("Text is empty", testError.getMessage());
    }
}
