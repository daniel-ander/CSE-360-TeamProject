package Tests;

import Models.Answer;
import Models.AnswerRepository;
import Models.Question;
import Models.QuestionRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuestionRepositoryTest {
	
	// READ TESTS
    @Test
    public void GetById_WithValidQuestionId_ReturnsCorrectQuestion() {
        QuestionRepository qs = new QuestionRepository();
        qs.addQuestion(new Question(1, "Hello?"));
        
        assertTrue(qs.getQuestionById(1) != null);
    }
    
    @Test
    public void GetById_WithInvalidQuestionId_ReturnsNull() {
        QuestionRepository qs = new QuestionRepository();
        qs.addQuestion(new Question(1, "Hello?"));
        
        assertTrue(qs.getQuestionById(2) == null);
    }
    
    @Test
    public void GetAnswers_WithValidQuestionId_ReturnsCorrectQuestion() {
        QuestionRepository qs = new QuestionRepository();
        AnswerRepository as = new AnswerRepository();
        qs.addQuestion(new Question(11, "Hello?"));
        as.addAnswer(new Answer(1, 11, "test", true, "Trevor"));
        
        assertTrue(qs.getAnswers(11, as).getAll().size() == 1);
    }
    
    @Test
    public void GetAnswers_WithInvalidQuestionId_ReturnsNull() {
        QuestionRepository qs = new QuestionRepository();
        AnswerRepository as = new AnswerRepository();
        qs.addQuestion(new Question(11, "Hello?"));
        as.addAnswer(new Answer(1, 11, "test", true, "Trevor"));
        
        assertTrue(qs.getAnswers(12, as).getAll().size() == 0);
    }
    
    @Test
    public void GetQuestionsBySearch_WhenStringIsFound_ReturnsCorrectQuestions() {
        QuestionRepository qs = new QuestionRepository();
        qs.addQuestion(new Question(11, "Hello?"));
        
        assertTrue(qs.getQuestionsBySearch("Hello").getAll().size() == 1);
    }
    
    @Test
    public void GetQuestionsBySearch_WhenStringIsNotFound_ReturnsNoQuestions() {
        QuestionRepository qs = new QuestionRepository();
        qs.addQuestion(new Question(11, "Hello?"));
        
        assertTrue(qs.getQuestionsBySearch("Trainssss").getAll().size() == 0);
    }
    
    @Test
    public void GetAnsweredQuestions_ReturnsCorrectQuestions() {
        QuestionRepository qs = new QuestionRepository();
        AnswerRepository as = new AnswerRepository();
        qs.addQuestion(new Question(11, "Question1?"));
        qs.addQuestion(new Question(12, "Question2?"));
        as.addAnswer(new Answer(1, 11, "test", true, "Trevor"));
        
        assertTrue(qs.getAnsweredQuestions(as).getAll().getFirst().getText() == "Question1?");
    }
    
    @Test
    public void GetUnansweredQuestions_ReturnsCorrectQuestions() {
        QuestionRepository qs = new QuestionRepository();
        AnswerRepository as = new AnswerRepository();
        qs.addQuestion(new Question(11, "Question1?"));
        qs.addQuestion(new Question(12, "Question2?"));
        as.addAnswer(new Answer(1, 11, "test", true, "Trevor"));
        
        assertTrue(qs.getUnansweredQuestions(as).getAll().getFirst().getText() == "Question2?");
    }
    
    // UPDATE TESTS
    @Test
    public void UpdateQuestion_WithValidText_ReturnsTrue() {
        QuestionRepository qs = new QuestionRepository();
        qs.addQuestion(new Question(1, "Old"));
        boolean updated = qs.updateQuestion(1, "Updated");
        
        assertTrue(updated);
        assertEquals("Updated", qs.getQuestionById(1).getText());
    }

    @Test
    public void UpdateQuestion_WithInvalidText_ReturnsFalse() {
        QuestionRepository qs = new QuestionRepository();
        qs.addQuestion(new Question(1, "Old"));
        
        Exception testError = assertThrows(IllegalArgumentException.class,
                () -> qs.updateQuestion(1, " "));
        assertEquals("Question text cannot be empty.", testError.getMessage());
    }
    
    // DELETE TESTS
    @Test
    public void DeleteQuestion_WithValidId_ReturnsTrue() {
        QuestionRepository qs = new QuestionRepository();
        AnswerRepository as = new AnswerRepository();
        qs.addQuestion(new Question(1, "Temp"));
        as.addAnswer(new Answer(1, 1, "test answer", true, "Trevor"));
        
        assertTrue(qs.deleteQuestion(1, as));
        assertTrue(qs.getQuestionById(1) == null);
    }
    
    @Test
    public void DeleteQuestion_WithInvalidId_ReturnsFalse() {
        QuestionRepository qs = new QuestionRepository();
        AnswerRepository as = new AnswerRepository();
        qs.addQuestion(new Question(1, "Temp"));
        
        assertFalse(qs.deleteQuestion(2, as));
    }
}
