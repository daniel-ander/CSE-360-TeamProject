package Database.Services.Interfaces;

import Models.Answer;
import Utils.RatingStats;

import java.math.BigDecimal;

import java.util.List;

import Exceptions.DBExceptions.DataAccessException;

public interface IAnswerService {	
	// ===================
    // 		CREATE 
 	// ===================
	
	// Answers
	public Answer addAnswer(Answer a) throws DataAccessException;
	
	// Ratings
	public void addRating(int answerId, int userId, BigDecimal rating) throws DataAccessException;
	
	// ===================
    // 		READ
 	// ===================
	
	// Answers
	public List<Answer> getAllAnswers() throws DataAccessException;
	public List<Answer> getAllAnswersForQuestion(int questionId) throws DataAccessException;
	public List<Answer> getCorrectAnswers() throws DataAccessException;
	public List<Answer> getAnswersBySearch(String searchString) throws DataAccessException;
	public List<Answer> getAnswersByQuestionID(int questionId) throws DataAccessException;
	public List<Answer> getAnswersByUser(int userId) throws DataAccessException;
	
	public Answer getAnswerById(int answerId) throws DataAccessException;
	
	// Ratings
	public BigDecimal getAverageRating(int answerId) throws DataAccessException;
	public BigDecimal getRatingForUser(int answerId, int userId) throws DataAccessException;
	
	public RatingStats getReviewerStats(int reviewerId) throws DataAccessException;
	
	// ===================
    // 		UPDATE
 	// ===================
	
	// Answers
	public boolean updateAnswer(int id, String newText, boolean newCorrect) throws DataAccessException;
	
	// Ratings
	public RatingStats updateRating(int answerId, int userId, BigDecimal rating) throws DataAccessException;
	
	// ===================
    // 		DESTROY
 	// ===================
	
	// Answers
	public boolean deleteAnswer(int id) throws DataAccessException;
	public boolean deleteByQuestionId(int questionId) throws DataAccessException;
	
	// Ratings
	public RatingStats removeRating(int answerId, int userId) throws DataAccessException;
}