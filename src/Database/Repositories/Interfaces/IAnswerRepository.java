package Database.Repositories.Interfaces;

import java.sql.SQLException;
import java.util.List;

import Models.Answer;

public interface IAnswerRepository {
	
	public List<Answer> getAll() throws SQLException;
	public List<Answer> getAllAnswersForQuestion(int questionId) throws SQLException;
	public List<Answer> getCorrectAnswers() throws SQLException;
    public List<Answer> getAnswersBySearch(String searchString) throws SQLException;
    public List<Answer> getAnswersByQuestionId(int questionId) throws SQLException;
    public List<Answer> getRepliesToAnswerById(int parentAnswerId) throws SQLException;
	
	public int insertAnswer(int questionId, int currentUserId, String text) throws SQLException;
	public int insertReply(int questionId, int parentAnswerId, int currentUserId, String text) throws SQLException;
	
	public boolean updateAnswer(int id, String newText, boolean newCorrect) throws SQLException;
	public boolean deleteAnswerById(int id) throws SQLException;
	public boolean deleteByQuestionId(int questionId) throws SQLException;
	
	public Answer getAnswerById(int answerId) throws SQLException;
	
}
