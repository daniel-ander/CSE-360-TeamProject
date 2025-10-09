package Database.Services.Interfaces;

import Models.Answer;

import java.sql.SQLException;
import java.util.List;

import Exceptions.DBExceptions.DataAccessException;

public interface IAnswerService {	
	public List<Answer> getAllAnswers() throws SQLException;
	public List<Answer> getAllAnswersForQuestion(int questionId) throws SQLException;
	public List<Answer> getCorrectAnswers() throws SQLException;
	public List<Answer> getAnswersBySearch(String searchString) throws SQLException;
	
	public Answer getAnswerById(int answerId) throws SQLException;
	public boolean addAnswer(Answer a) throws DataAccessException;
	
	public boolean updateAnswer(int id, String newText, boolean newCorrect) throws SQLException;
	public boolean deleteAnswer(int id) throws SQLException;
	public boolean deleteByQuestionId(int questionId) throws SQLException;
}