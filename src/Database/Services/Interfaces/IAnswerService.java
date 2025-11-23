package Database.Services.Interfaces;

import Models.Answer;

import java.math.BigDecimal;

import java.util.List;

import Exceptions.DBExceptions.DataAccessException;

public interface IAnswerService {	
	public List<Answer> getAllAnswers() throws DataAccessException;
	public List<Answer> getAllAnswersForQuestion(int questionId) throws DataAccessException;
	public List<Answer> getCorrectAnswers() throws DataAccessException;
	public List<Answer> getAnswersBySearch(String searchString) throws DataAccessException;
	public List<Answer> getAnswersByQuestionID(int questionId) throws DataAccessException;
	public List<Answer> getAnswersByUser(int userId) throws DataAccessException;
	
	public Answer getAnswerById(int answerId) throws DataAccessException;
	public Answer addAnswer(Answer a) throws DataAccessException;
	
	public boolean updateAnswer(int id, String newText, boolean newCorrect) throws DataAccessException;
	public boolean deleteAnswer(int id) throws DataAccessException;
	public boolean deleteByQuestionId(int questionId) throws DataAccessException;
	public void removeRating(int answerId, int userId) throws DataAccessException;
	public BigDecimal getAverageRating(int answerId) throws DataAccessException;
	public void addRating(int answerId, int userId, BigDecimal rating) throws DataAccessException;
	public void updateRating(int answerId, int userId, BigDecimal rating) throws DataAccessException;
}