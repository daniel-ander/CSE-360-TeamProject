package Tests;

import Models.Question;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuestionTest {
	
	// CREATE TESTS
    @Test
    public void Constructor__WithValidInputs_CreatesQuestion() {
        Question q = new Question(1, "Title", "This is a question text");
        
        assertEquals(-1, q.getId()); // -1 since not yet in DB
        assertEquals(1, q.getAuthorId());
        assertEquals("Title", q.getTitleText());
        assertEquals("This is a question text", q.getText());
        assertFalse(q.getAnsweredStatus());
        assertFalse(q.getResolvedStatus());
        assertFalse(q.getDeleted());
    }

    @Test
    public void Constructor_WithEmptyString_ThrowsException() {
        Exception testError = assertThrows(IllegalArgumentException.class,
                () -> new Question(2, "Title", " "));
            
            assertEquals("Text is empty", testError.getMessage());
    }
    
    // UPDATE TESTS
    @Test
    public void SetText_WithValidString_ChangesText() {
        Question q = new Question(1, "Title", "Old question");
        
        q.setText("New question");
        
        assertEquals("New question", q.getText());
    }

    @Test
    public void SetText_WithEmptyString_ThrowsException() {
        Question q = new Question(1, "Title", "QuestionText");
        
        Exception testError = assertThrows(IllegalArgumentException.class,
                () -> q.setText(" "));
        
        assertEquals("Text is empty", testError.getMessage());
    }
}
