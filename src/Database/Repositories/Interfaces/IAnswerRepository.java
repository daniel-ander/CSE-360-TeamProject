package Database.Repositories.Interfaces;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import Models.Answer;
import Utils.AnswerRatingInfo;

public interface IAnswerRepository {
	
	public List<Answer> getAll() throws SQLException;
	public List<Answer> getAnswersByQuestionId(int questionId) throws SQLException;
	public List<Answer> getAnswersByUser(int userId) throws SQLException;
	public List<Answer> getCorrectAnswers() throws SQLException;
    public List<Answer> getAnswersBySearch(String searchString) throws SQLException;
    public List<Answer> getRepliesToAnswerById(int parentAnswerId) throws SQLException;
	
	public Answer insertAnswer(Answer a) throws SQLException;
	
	public boolean updateAnswer(int id, String newText, boolean newCorrect) throws SQLException;
	public boolean deleteAnswerById(int id) throws SQLException;
	public boolean deleteByQuestionId(int questionId) throws SQLException;
	
	public Answer getAnswerById(int answerId) throws SQLException;

	public void deleteRating(int answerId, int userId) throws SQLException;

	public void addRating(int answerId, int userId, BigDecimal rating) throws SQLException;
	public void updateAnswerRatingInfo(int answerId) throws SQLException;

	public AnswerRatingInfo getAnswerRatingInfo(int answerId) throws SQLException;
	public void updateRating(int answerId, int userId, BigDecimal rating) throws SQLException;
	public BigDecimal getRating(int userId, int answerId) throws SQLException;
	
}
