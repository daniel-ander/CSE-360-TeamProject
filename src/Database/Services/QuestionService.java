package Database.Services;

import Models.Question;

import java.sql.SQLException;

import java.util.List;

import Database.Repositories.Interfaces.IQuestionRepository;

import Database.Services.Interfaces.IQuestionService;

import Exceptions.DBExceptions.*;


public class QuestionService implements IQuestionService {
	
	private final IQuestionRepository questionRepository;
	
	public QuestionService(IQuestionRepository questionRepository) {
		this.questionRepository = questionRepository;
	}
	
	// ===================
    // 		 CREATE 
 	// ===================
	
	@Override
	public boolean addQuestion(Question q) throws DataAccessException, DuplicateRecordIDException {
		boolean doesQuestionExist = (getQuestionById(q.getId()) != null);
		
		if (doesQuestionExist) {
			throw new DuplicateRecordIDException("Question with this id already exists!");
		}
		
		try {
			questionRepository.insertQuestion(q);
		} catch (SQLException e) {
			throw new DataAccessException("Could not add the question to the database due to: " + e);
		}
		
		return true;
	}
	
	// ===================
    // 		 READ 
 	// ===================
	
	@Override
	public List<Question> getAllQuestions(){
		try {
			return questionRepository.getAllQuestions();
		} catch (SQLException e) {
			throw new DataAccessException("Could not get all questions from the database due to: " + e);
		}
	}
	
	@Override
	public Question getQuestionById(int targetId) throws DataAccessException {
		try {
			return questionRepository.getQuestionById(targetId);
		} catch (SQLException e) {
			throw new DataAccessException("Could not get question by id :" + targetId + " from the database due to: " + e);
		}
	}
	
	// ===================
    // 		 UPDATE 
 	// ===================
	
	@Override
	public boolean updateQuestion(int id, String newText) throws DataAccessException, IllegalArgumentException {
		// TODO: - May want to place some safeguards here before a question can be updated
		// by checking if the user has permission to do so.
		if (newText == null || newText.trim().isEmpty()) {
		    throw new IllegalArgumentException("Question text cannot be empty.");
		}
		
		try {
			return questionRepository.updateQuestion(id, newText);
		} catch (SQLException e) {
			throw new DataAccessException("Could not update question by id :" + id + " from the database due to: " + e);
		}
	}
	
	// ===================
    // 		 DESTROY 
 	// ===================
	
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
	
}