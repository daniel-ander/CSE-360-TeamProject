package Database.Services;

import Models.Question;
import Utils.Debug;

import java.sql.SQLException;

import java.util.List;

import Database.Repositories.Interfaces.IQuestionRepository;

import Database.Services.Interfaces.IQuestionService;

import Exceptions.DBExceptions.*;

/**
 * Manages question objects.
 */
public class QuestionService implements IQuestionService {
	
	private final IQuestionRepository questionRepository;
	
    /**
     * Creates a new QuestionService with the given repository.
     */
	public QuestionService(IQuestionRepository questionRepository) {
		this.questionRepository = questionRepository;
	}
	
    /**
     * Adds a new question to the database.
     */
	@Override
	public boolean addQuestion(Question q) throws DataAccessException, DuplicateRecordIDException {
		try {
			questionRepository.insertQuestion(q);
			if (Debug.isEnabled) System.out.println("Question added successfully.");
		} catch (SQLException e) {
			throw new DataAccessException("Could not add the question to the database due to: " + e);
		}
		
		return true;
	}
	
    /**
     * Retrieves all questions from the database.
     */
	@Override
	public List<Question> getAllQuestions(){
		try {			
			if (Debug.isEnabled) System.out.println("Questions fetched successfully.");
			return questionRepository.getAllQuestions();

		} catch (SQLException e) {
			throw new DataAccessException("Could not get all questions from the database due to: " + e);
		}
	}
	
    /**
     * Retrieves a limited number of questions from the database.
     */
	@Override
	public List<Question> getLimitedAmountOfQuestions(int amount) {
		try {			
			if (Debug.isEnabled) System.out.println("Questions fetched successfully.");
			return questionRepository.getLimitedAmountOfQuestions(amount);

		} catch (SQLException e) {
			throw new DataAccessException("Could not get all questions from the database due to: " + e);
		}
	}
	
    /**
     * Retrieves a single question by its ID.
     */
	@Override
	public Question getQuestionById(int targetId) throws DataAccessException {
		try {
			return questionRepository.getQuestionById(targetId);
		} catch (SQLException e) {
			throw new DataAccessException("Could not get question by id :" + targetId + " from the database due to: " + e);
		}
	}
	
    /**
     * Updates the title and text of a question by its ID.
     */
	@Override
	public boolean updateQuestion(int id, String newTitle, String newText) throws DataAccessException, IllegalArgumentException {
		// TODO: - May want to place some safeguards here before a question can be updated
		// by checking if the user has permission to do so.
		if (newText == null || newText.trim().isEmpty()) {
		    throw new IllegalArgumentException("Question text cannot be empty.");
		}
		
		try {
			return questionRepository.updateQuestion(id, newTitle, newText);
		} catch (SQLException e) {
			throw new DataAccessException("Could not update question by id :" + id + " from the database due to: " + e);
		}
	}
	
    /**
     * Deletes a question by its ID.
     */
	@Override
	public boolean deleteQuestion(int id) throws DataAccessException {
		// TODO: - May want to place some safeguards here before a question can be deleted
		// by checking if the user has permission to do so.
		try {
			return questionRepository.deleteQuestion(id);
		} catch (SQLException e) {
			throw new DataAccessException("Could not delete question by id :" + id + " from the database due to: " + e);
		}
	}

    /**
     * Retrieves all questions created by a specific user.
     */
	@Override
	public List<Question> getQuestionsFromUser(int userId) throws DataAccessException {
		try {
			return questionRepository.getQuestionsFromUser(userId);
		} catch (SQLException e) {
			throw new DataAccessException("Could not get questions for user id: " + userId + " due to: " + e);
		}
	}
	
}