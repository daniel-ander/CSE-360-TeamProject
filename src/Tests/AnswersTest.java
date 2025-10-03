package Tests;

import Models.Answer;
import Models.Answers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnswersTest {
	// CREATE TESTS
	@Test
	public void AddAnswer_WithUniqueId_ReturnsSuccesfully() {
		Answers as = new Answers();
		as.addAnswer(new Answer(1, -1, "unique1", true, "Trevor"));
		as.addAnswer(new Answer(2, -1, "unique2", true, "Trevor"));
		
		assertTrue(as.getAll().size() == 2);
	}
	@Test
	public void AddAnswer_WithCopyId_ThrowsException() {
		Answers as = new Answers();
		as.addAnswer(new Answer(1, -1, "unique1", true, "Trevor"));
		
        Exception testError = assertThrows(IllegalArgumentException.class,
                () -> as.addAnswer(new Answer(1, -1, "unique2", true, "Trevor")));
        assertEquals("Answer with this id already exists!", testError.getMessage());
	}
	
	// READ TESTS
    @Test
    public void GetAll_ReturnsAllAnswers() {
		Answers as = new Answers();
		as.addAnswer(new Answer(1, -1, "unique1", true, "Trevor"));
		as.addAnswer(new Answer(2, -1, "unique2", false, "Trevor"));
		
		assertTrue(as.getAll().size() == 2);
    }
    
    @Test
    public void GetById_WithValidAnswerId_ReturnsCorrectAnswer() {
        Answers as = new Answers();
        as.addAnswer(new Answer(1, -1, "Hello?", true, "Trevor"));
        assertTrue(as.getAnswerById(1) != null);
    }
    @Test
    public void GetById_WithInvalidAnswerId_ReturnsNull() {
        Answers as = new Answers();
        as.addAnswer(new Answer(1, -1, "Hello?", true, "Trevor"));
        assertTrue(as.getAnswerById(2) == null);
    }
    
    @Test
    public void GetCorrectAnswers_ReturnsOnlyCorrectAnswers(){
		Answers as = new Answers();
		as.addAnswer(new Answer(1, -1, "unique1", true, "Trevor"));
		as.addAnswer(new Answer(2, -1, "unique2", false, "Trevor"));
		
		Answers result = as.getCorrectAnswers();
		
		assertTrue(result.getAll().getFirst().getText() == "unique1");
		assertTrue(result.getAll().size() == 1);
    }
    
    @Test
    public void GetAnswersBySearch_WhenStringIsFound_ReturnsCorrectAnswers() {
    	Answers as = new Answers();
    	as.addAnswer(new Answer(1, -1, "Hello", true, "Trevor"));
        
        assertTrue(as.getAnswersBySearch("Hello").getAll().size() == 1);
    }
    
    @Test
    public void GetAnswersBySearch_WhenStringIsNotFound_ReturnsNoAnswers() {
    	Answers as = new Answers();
    	as.addAnswer(new Answer(1, -1, "unique1", true, "Trevor"));
        
        assertTrue(as.getAnswersBySearch("Trainssss").getAll().size() == 0);
    }

    // UPDATE TESTS
    @Test
    public void UpdateAnswer_WithValidId_ReturnsTrue() {
        Answers as = new Answers();
        as.addAnswer(new Answer(1, -1, "Old", true, "Trevor"));
        boolean updated = as.updateAnswer(1, "Updated", true);
        assertTrue(updated);
        assertEquals("Updated", as.getAnswerById(1).getText());
    }
    @Test
    public void UpdateAnswer_WithInvalidId_ReturnsFalse() {
        Answers as = new Answers();
        as.addAnswer(new Answer(1, -1, "Old", true, "Trevor"));
        boolean updated = as.updateAnswer(2, "Updated", true);
        assertFalse(updated);
    }
    
    // DELETE TESTS
    @Test
    public void DeleteAnswer_WithValidId_ReturnsTrue() {
        Answers as = new Answers();
        as.addAnswer(new Answer(1, -1, "Temp", true, "Trevor"));
        assertTrue(as.deleteAnswer(1));
        assertFalse(as.getAnswerById(1) != null);
    }
    @Test
    public void DeleteAnswer_WithInvalidId_ReturnsFalse() {
        Answers as = new Answers();
        as.addAnswer(new Answer(1, -1, "Temp", true, "Trevor"));
        assertFalse(as.deleteAnswer(2));
    }
    
    @Test
    public void DeleteQuestionWithAnswers_WithValidId_ReturnsTrue() {
        Answers as = new Answers();
        as.addAnswer(new Answer(1, 1, "test answer", true, "Trevor"));
        assertTrue(as.deleteByQuestionId(1));
        assertTrue(as.getAnswerById(1) == null);
    }
}
