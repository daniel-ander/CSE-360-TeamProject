package Database.Repositories;

import Models.Answer;
import Utils.Debug;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Database.IGeneralDBManager;
import Database.Repositories.Interfaces.IAnswerRepository;


public class AnswerRepository implements IAnswerRepository {
	IGeneralDBManager generalDBManager;
	
	public AnswerRepository(IGeneralDBManager generalDBManager) {
		this.generalDBManager = generalDBManager;
	}
	
	// ===================
    // 		 CREATE 
 	// ===================

	@Override
	public boolean insertAnswer(Answer a) throws SQLException {
		String insertAnswer = "INSERT INTO ForumAnswers (questionId, userId, body) VALUES (?, ?, ?)";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(insertAnswer, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setInt(1, a.getQuestionId());
			pstmt.setInt(2, a.getAuthorId());
			pstmt.setString(3, a.getText());
			pstmt.executeUpdate();
			
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int answerId = generatedKeys.getInt(1);
					if (Debug.isEnabled) System.out.println("Inserted answer ID: " + answerId + " for question ID: ");
					a.setId(answerId);
				} else {
					throw new SQLException("Inserting answer failed, no ID obtained.");
				}
			} 
		}
		return true; // Answer created
	}
	@Override
	public int insertReply(int questionId, int parentAnswerId, int currentUserId, String text) throws SQLException {
		String insertReply = "INSERT INTO ForumAnswers (questionId, parentAnswerId, userId, body) VALUES (?, ?, ?, ?)";
		try (Connection connection = generalDBManager.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(insertReply, Statement.RETURN_GENERATED_KEYS)) {
				pstmt.setInt(1, questionId);
				pstmt.setInt(2, parentAnswerId);
				pstmt.setInt(3, currentUserId);
				pstmt.setString(4, text);
				pstmt.executeUpdate();
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						int answerId = generatedKeys.getInt(1);
						if (Debug.isEnabled) System.out.println("Inserted reply ID: " + answerId + " for question ID: " + questionId + " and parent answer ID: " + parentAnswerId);
						return answerId;
					} else {
						throw new SQLException("Inserting reply failed, no ID obtained.");
					}
				}
		}
	}
	
	// ===================
    // 		 READ 
 	// ===================
	
	@Override
	public List<Answer> getAll() throws SQLException {
		List<Answer> allAnswers = new ArrayList<>();
		String getAllAnswers = "SELECT * FROM ForumAnswers WHERE isDeleted = FALSE";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAllAnswers)){
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Answer a = new Answer(
						rs.getInt("id"),
						rs.getInt("questionId"),
						rs.getInt("parentAnswerId"),
						rs.getInt("userId"),
						rs.getString("body"),
						rs.getTimestamp("timestamp"),
						rs.getInt("upvoteCount"),
						rs.getBoolean("isCorrect"),
						rs.getBoolean("isDeleted")
				);
				allAnswers.add(a);
			}
		}
		return allAnswers;
	}
	
	@Override
	public List<Answer> getAllAnswersForQuestion(int questionId) throws SQLException {
		List<Answer> allAnswersForQuestion = new ArrayList<>();
		//TODO: we might need this to check parentId depending on how we want to display answers
		//		for now we will just get all answers for a question
		String getAllAnswersForQuestion = "SELECT * FROM ForumAnswers WHERE questionId = ? AND isDeleted = FALSE";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAllAnswersForQuestion)){
			pstmt.setInt(1, questionId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Answer a = new Answer(
						rs.getInt("id"),
						rs.getInt("questionId"),
						rs.getInt("parentAnswerId"),
						rs.getInt("userId"),
						rs.getString("body"),
						rs.getTimestamp("timestamp"),
						rs.getInt("upvoteCount"),
						rs.getBoolean("isCorrect"),
						rs.getBoolean("isDeleted")
				);
				allAnswersForQuestion.add(a);
			}
		}
		return allAnswersForQuestion;
	}
	@Override
	public List<Answer> getRepliesToAnswerById(int parentAnswerId) throws SQLException {
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
    // 		 UPDATE 
 	// ===================
    
	@Override
    public boolean updateAnswer(int id, String newText, boolean newCorrect) throws SQLException {
		// TODO: - Perform query
		return true;
	}
	
	// ===================
    // 		 DESTROY 
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