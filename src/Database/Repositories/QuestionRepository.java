package Database.Repositories;

import Models.Question;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import Database.IGeneralDBManager;
import Database.Repositories.Interfaces.IQuestionRepository;

public class QuestionRepository implements IQuestionRepository {
	private List<Question> questions = new ArrayList<>();
	
	IGeneralDBManager generalDBManager;
	
	public QuestionRepository(IGeneralDBManager generalDBManager) {
		this.generalDBManager = generalDBManager;
	}
	
	// ===================
    // 		 CREATE 
 	// ===================
	
	@Override
	public boolean insertQuestion(Question q) throws SQLException {
		// TODO: - Perform query
		return true; // Question created
	}
	
	// ===================
    // 		  READ 
 	// ===================
	
	@Override
	public List<Question> getAllQuestions() throws SQLException {
		List<Question> questions = new ArrayList<>();
		// TODO: - Perform query
		return questions;
	}
	
	@Override
	public Question getQuestionById(int searchId) throws SQLException {
		// TODO: - Perform query
		return null;
	}
	
	@Override
	public List<Question> getAnsweredQuestions() throws SQLException {
		List<Question> questions = new ArrayList<>();
		// TODO: - Perform query
		return questions;
	}
	
	@Override
	public List<Question> getUnansweredQuestions() throws SQLException {
		List<Question> questions = new ArrayList<>();
		// TODO: - Perform query
		return questions;
	}
	
	@Override
	public List<Question> getQuestionsBySearch(String searchString) throws SQLException {
		List<Question> questions = new ArrayList<>();
		// TODO: - Perform query
		return questions;
	}
	
	@Override
	public List<Question> getQuestionsFromUser(int userId) throws SQLException {
		List<Question> questions = new ArrayList<>();
		// TODO: - Perform query
		return questions;
	}
	
	// ===================
    // 		 UPDATE 
 	// ===================
	
	@Override
	public boolean updateQuestion(int id, String newText) throws SQLException {
		if (newText == null || newText.trim().isEmpty()) {
		    throw new IllegalArgumentException("Question text cannot be empty.");
		}
		
		Question q = getQuestionById(id);
		if (q == null) {
			return false;
		}
		q.setText(newText);
		// TODO: - Perform query
		return false;
	}
	
	// ===================
    // 		DESTROY 
 	// ===================
	
	
	@Override
	public boolean deleteQuestion(int id) throws SQLException {
		// TODO: - Perform query
		return true;
	}
}