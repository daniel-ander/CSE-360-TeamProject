package Database.Services;

import Models.Question;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import Database.Repositories.Interfaces.IQuestionRepository;

import Database.Services.Interfaces.IQuestionService;

import Exceptions.QuestionDBExceptions.DuplicateQuestionIDException;

public class QuestionService implements IQuestionService {
	private List<Question> questions;
	
	private final IQuestionRepository questionRepository;
	
	public QuestionService(IQuestionRepository questionRepository) {
		this.questionRepository = questionRepository;
	}
	
	// ===================
    // 		 CREATE 
 	// ===================
	
	@Override
	public boolean addQuestion(Question q) throws SQLException {
		boolean doesQuestionExist = (getQuestionById(q.getId()) != null);
		
		if (doesQuestionExist) {
			throw new DuplicateQuestionIDException("Question with this id already exists!");
		}
		
		questionRepository.insertQuestion(q);
		
		return true;
	}
	
	// ===================
    // 		 READ 
 	// ===================
	
	@Override
	public List<Question> getAllQuestions(){
		return new ArrayList<>(questions);
	}
	
	@Override
	public Question getQuestionById(int targetId) throws SQLException {
		return questionRepository.getQuestionById(targetId);
	}
	
	// ===================
    // 		 UPDATE 
 	// ===================
	
	@Override
	public boolean updateQuestion(int id, String newText) throws SQLException {
		// TODO: - May want to place some safeguards here before a question can be deleted
		// by checking if the user has permission to do so.
		if (newText == null || newText.trim().isEmpty()) {
		    throw new IllegalArgumentException("Question text cannot be empty.");
		}
		
		Question q = getQuestionById(id);
		if (q == null) {
			return false;
		}
		
		q.setText(newText);
		return true;
	}
	
	// ===================
    // 		 DESTROY 
 	// ===================
	
	@Override
	public boolean deleteQuestion(int id) throws SQLException {
		Question q = this.getQuestionById(id);
		
		if (q == null) {
			return false;
		}
		
		// TODO: - May want to place some safeguards here before a question can be deleted
		// by checking if the user has permission to do so.
		questionRepository.deleteQuestion(id);
		questions.remove(q);
		
		return true;
	}
}