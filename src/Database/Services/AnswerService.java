package Database.Services;

import Models.Answer;
import Utils.Debug;

import java.sql.SQLException;

import java.util.List;

import Database.Repositories.Interfaces.IAnswerRepository;
import Database.Services.Interfaces.IAnswerService;

import Exceptions.DBExceptions.*;

public class AnswerService implements IAnswerService {

	private final IAnswerRepository answerRepository;
	
	public AnswerService(IAnswerRepository answerRepository) {
		this.answerRepository = answerRepository;
	}

	// ===================
    // 		 CREATE 
 	// ===================
	
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
	
	// ===================
    // 		 READ 
 	// ===================
	
	@Override
	public List<Answer> getAllAnswers() throws DataAccessException {
		try {
			return answerRepository.getAll();
		} catch (SQLException e) {
			throw new DataAccessException("Failed to get answers from database due to: " + e);
		}
	}
	
	@Override
	public List<Answer> getAllAnswersForQuestion(int questionId) throws DataAccessException {
		try {
			return answerRepository.getAnswersByQuestionId(questionId);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to get answers for question id: " + questionId + " from database due to: " + e);
		}
	}
	
	@Override
	public List<Answer> getCorrectAnswers() throws DataAccessException {
		try {
			return answerRepository.getCorrectAnswers();
		} catch (SQLException e) {
			throw new DataAccessException("Failed to get correct answers from database due to: " + e);
		}
	}

	@Override
	public List<Answer> getAnswersBySearch(String searchString) throws DataAccessException {
		try {
			return answerRepository.getAnswersBySearch(searchString);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to get correct answers from database due to: " + e);
		}
	}
	
	@Override
	public Answer getAnswerById(int answerId) throws DataAccessException {
		try {
			return answerRepository.getAnswerById(answerId);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to get answer by id: " + answerId + " from database due to: " + e);
		}
	}
	
	// ===================
    // 		 UPDATE 
 	// ===================
	
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
	
	// ===================
    // 		 DESTROY 
 	// ===================
	
	@Override
	public boolean deleteAnswer(int id) throws DataAccessException {
		try {
			return answerRepository.deleteAnswerById(id);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to delete answer by id: " + id + " from database due to: " + e);
		}
	}

	@Override
	public boolean deleteByQuestionId(int questionId) throws DataAccessException {
		try {
			return answerRepository.deleteByQuestionId(questionId);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to delete answers with question id: " + questionId + " from database due to: " + e);
		}
	}

	@Override
	public List<Answer> getAnswersByQuestionID(int questionId) throws DataAccessException {
		try {
			return answerRepository.getAnswersByQuestionId(questionId);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to get answers with question id: " + questionId + " from database due to: " + e);
		}
	}
	
	@Override
	public List<Answer> getAnswersByUser(int userId) throws DataAccessException {
		try {
			return answerRepository.getAnswersByUser(userId);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to get answers by user id: " + userId + " from database due to: " + e);
		}
	}

}
