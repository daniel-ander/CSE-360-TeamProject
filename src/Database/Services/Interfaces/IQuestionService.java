package Database.Services.Interfaces;

import Models.Question;

import java.sql.SQLException;
import java.util.List;

public interface IQuestionService {
	
	public List<Question> getAllQuestions();
	
	public Question getQuestionById(int targetId) throws SQLException;
	
	public boolean addQuestion(Question q) throws SQLException;
	public boolean updateQuestion(int id, String newText) throws SQLException;
	public boolean deleteQuestion(int id) throws SQLException;
}