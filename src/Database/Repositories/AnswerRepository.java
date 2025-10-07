package Database.Repositories;

import Models.Answer;
import Models.Question;

import java.sql.SQLException;

import java.util.List;

import Database.IGeneralDBManager;
import Database.Repositories.Interfaces.IAnswerRepository;


public class AnswerRepository implements IAnswerRepository {
	private final IGeneralDBManager generalDBManager;
	
	public AnswerRepository(IGeneralDBManager generalDBManager) {
		this.generalDBManager = generalDBManager;
	}
	
	// ===================
    // CREATE Section
 	// ===================

	@Override
	public int insertAnswer(int questionId, int currentUserId, String text) throws SQLException {
		// TODO: - Perform query
		return 1; // We need to return the ID of the newly-created answer
	}
	
	// ===================
    // READ Section
 	// ===================
	
	@Override
	public List<Answer> getAll() throws SQLException {
		// TODO: - Perform query
		return null;
	}
	
	@Override
	public List<Answer> getAllAnswersForQuestion(int questionId) throws SQLException {
		// TODO: - Perform query
		return null;
	}
	
	@Override
	public Answer getAnswerById(int answerId) throws SQLException {
		// TODO: - Perform query
		return null;
	}
	
	@Override
	public List<Answer> getCorrectAnswers() throws SQLException {
    	// TODO: - Perform query
    	return null;
    }
    
	@Override
    public List<Answer> getAnswersBySearch(String searchString) throws SQLException {
    	// TODO: - Perform query
    	return null;
    }
    
	@Override
    public List<Answer> getAnswersByQuestionId(int questionId) throws SQLException {
    	// TODO: - Perform query
    	return null;
    }
	
	// ===================
    // UPDATE Section
 	// ===================
    
	@Override
    public boolean updateAnswer(int id, String newText, boolean newCorrect) throws SQLException {
		// TODO: - Perform query
		return true;
	}
	
	// ===================
    // DESTROY Section
 	// ===================
	
	@Override
	public boolean deleteAnswerById(int id) throws SQLException {
		// TODO: - Perform query
		return true;
	}
	
	@Override
	public boolean deleteByQuestionId(int questionId) throws SQLException {
		// TODO: - Perform query
		return true;
	}

}