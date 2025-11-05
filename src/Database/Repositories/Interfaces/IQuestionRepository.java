package Database.Repositories.Interfaces;

import java.sql.SQLException;
import java.util.List;

import Models.Question;

public interface IQuestionRepository {
	
	public List<Question> getAllQuestions() throws SQLException;
	public List<Question> getLimitedAmountOfQuestions(int amount) throws SQLException;
	
	public Question getQuestionById(int searchId) throws SQLException;
	
	
	public List<Question> getAnsweredQuestions() throws SQLException;
	public List<Question> getUnansweredQuestions() throws SQLException;
	public List<Question> getQuestionsBySearch(String searchString) throws SQLException;
	public List<Question> getQuestionsFromUser(int userId) throws SQLException;
	
	public boolean insertQuestion(Question q) throws SQLException;
	public boolean updateQuestion(int id, String newTitle, String newText) throws SQLException;
	public boolean deleteQuestion(int id) throws SQLException;
}
