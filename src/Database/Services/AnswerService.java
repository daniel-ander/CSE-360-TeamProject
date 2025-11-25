package Database.Services;

import Models.Answer;
import Models.DBModels.Rating;
import Utils.Debug;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.List;

import Database.Repositories.Interfaces.IAnswerRepository;
import Database.Services.Interfaces.IAnswerService;

import Exceptions.DBExceptions.*;

/**
 * The AnswerService class provides business logic for managing Answer objects.
 */
public class AnswerService implements IAnswerService {

	private final IAnswerRepository answerRepository;
	
    /**
     * Creates a new AnswerService with the given repository.
     */
	public AnswerService(IAnswerRepository answerRepository) {
		this.answerRepository = answerRepository;
	}

    /**
     * Adds a new answer to the database.
     */
	@Override
	public Answer addAnswer(Answer a) throws DataAccessException, DuplicateRecordIDException {
		try {
			Answer createdAnswer = answerRepository.insertAnswer(a);
			if (Debug.isEnabled) System.out.println("Answer added successfully.");
			return createdAnswer;
		} catch (SQLException e) {
			throw new DataAccessException("Failed to add answer to database due to: " + e);
		}
	}
	
    /**
     * Retrieves all answers from the database.
     */
	@Override
	public List<Answer> getAllAnswers() throws DataAccessException {
		try {
			return answerRepository.getAll();
		} catch (SQLException e) {
			throw new DataAccessException("Failed to get answers from database due to: " + e);
		}
	}
	
    /**
     * Retrieves all answers for a specific question ID.
     */
	@Override
	public List<Answer> getAllAnswersForQuestion(int questionId) throws DataAccessException {
		try {
			return answerRepository.getAnswersByQuestionId(questionId);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to get answers for question id: " + questionId + " from database due to: " + e);
		}
	}
	
    /**
     * Retrieves all answers marked as correct.
     */
	@Override
	public List<Answer> getCorrectAnswers() throws DataAccessException {
		try {
			return answerRepository.getCorrectAnswers();
		} catch (SQLException e) {
			throw new DataAccessException("Failed to get correct answers from database due to: " + e);
		}
	}
	
    /**
     * Retrieves answers that match a search string in their body text.
     */
	@Override
	public List<Answer> getAnswersBySearch(String searchString) throws DataAccessException {
		try {
			return answerRepository.getAnswersBySearch(searchString);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to get correct answers from database due to: " + e);
		}
	}
	
    /**
     * Retrieves a single answer by its ID.
     */
	@Override
	public Answer getAnswerById(int answerId) throws DataAccessException {
		try {
			return answerRepository.getAnswerById(answerId);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to get answer by id: " + answerId + " from database due to: " + e);
		}
	}
	
    /**
     * Updates the text and correctness flag of an answer.
     * Throws IllegalArgumentException if the new text is empty.
     * Returns false if the answer does not exist.
     */
	@Override
	public boolean updateAnswer(int id, String newText, boolean newCorrect) throws DataAccessException {
		if (newText == null || newText.trim().isEmpty()) {
			throw new IllegalArgumentException("Answer text cannot be empty.");
		}

		Answer a = getAnswerById(id);
		if (a == null) {
			return false;
		}

		try {
			answerRepository.updateAnswer(id, newText, newCorrect);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to update answer in database due to: " + e);
		}
		
		a.setText(newText);
		a.setIsCorrect(newCorrect);
		
		return true;
	}
	
    /**
     * Deletes an answer by its ID.
     */
	@Override
	public boolean deleteAnswer(int id) throws DataAccessException {
		try {
			return answerRepository.deleteAnswerById(id);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to delete answer by id: " + id + " from database due to: " + e);
		}
	}

    /**
     * Deletes all answers associated with a specific question ID.
     */
	@Override
	public boolean deleteByQuestionId(int questionId) throws DataAccessException {
		try {
			return answerRepository.deleteByQuestionId(questionId);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to delete answers with question id: " + questionId + " from database due to: " + e);
		}
	}

    /**
     * Retrieves answers by question ID
     */
	@Override
	public List<Answer> getAnswersByQuestionID(int questionId) throws DataAccessException {
		try {
			return answerRepository.getAnswersByQuestionId(questionId);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to get answers with question id: " + questionId + " from database due to: " + e);
		}
	}
	
    /**
     * Retrieves all answers authored by a specific user.
     */
	@Override
	public List<Answer> getAnswersByUser(int userId) throws DataAccessException {
		try {
			return answerRepository.getAnswersByUser(userId);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to get answers by user id: " + userId + " from database due to: " + e);
		}
	}
	
	@Override
	public BigDecimal getRating(int answerId, int userId) throws DataAccessException {
		try {
			return answerRepository.getRating(answerId, userId);
		} catch(SQLException e) {
			throw new DataAccessException("Failed to fetch rating between user: " + userId + " and answer " + answerId + " from database due to: " + e);
		}
	}
	
	@Override
	public void addRating(int answerId, int userId, BigDecimal rating) throws DataAccessException {
		try {
			answerRepository.addRating(answerId, userId, rating);
		} catch(SQLException e) {
			throw new DataAccessException("Failed to add rating between user: " + userId + " and answer " + answerId + " from database due to: " + e);
		}
	}
	
	@Override
	public void updateRating(int answerId, int userId, BigDecimal rating) throws DataAccessException {
		try {
			answerRepository.updateRating(answerId, userId, rating);
		} catch(SQLException e) {
			throw new DataAccessException("Failed to add rating between user: " + userId + " and answer " + answerId + " from database due to: " + e);
		}
	}
	
	@Override
	public void removeRating(int answerId, int userId) throws DataAccessException {
		try {
			answerRepository.deleteRating(answerId, userId);
		} catch(SQLException e) {
			throw new DataAccessException("Failed to delete rating between user: " + userId + " and answer " + answerId + " from database due to: " + e);
		}
	}
	
	@Override
	public BigDecimal getAverageRating(int answerId) throws DataAccessException {
		try {
			return answerRepository.getAnswerRatingInfo(answerId).getAverageRating();
		} catch(SQLException e) {
			throw new DataAccessException("Failed to get average raing for answer " + answerId + " from database due to: " + e);
		}
	}

}