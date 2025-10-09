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
	public boolean insertReply(Answer a) throws SQLException {
		String insertReply = "INSERT INTO ForumAnswers (questionId, parentAnswerId, userId, body) VALUES (?, ?, ?, ?)";
		try (Connection connection = generalDBManager.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(insertReply, Statement.RETURN_GENERATED_KEYS)) {
				pstmt.setInt(1, a.getQuestionId());
				pstmt.setInt(2, a.getParentAnswerId());
				pstmt.setInt(3, a.getAuthorId());
				pstmt.setString(4, a.getText());
				pstmt.executeUpdate();
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						int answerId = generatedKeys.getInt(1);
						if (Debug.isEnabled) System.out.println("Inserted reply ID: " + answerId + " for question ID: " + a.getQuestionId() + " and parent answer ID: " + a.getParentAnswerId());
						a.setId(answerId);
					} else {
						throw new SQLException("Inserting reply failed, no ID obtained.");
					}
				}
		}
		return true; // Reply created
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
	public List<Answer> getAnswersByQuestionId(int questionId) throws SQLException {
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
		List<Answer> repliesToAnswerById = new ArrayList<>();
		String getRepliesToAnswerById = "SELECT * FROM ForumAnswers WHERE parentAnswerId = ? AND isDeleted = FALSE";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getRepliesToAnswerById)){
			pstmt.setInt(1, parentAnswerId);
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
				repliesToAnswerById.add(a);
			}
		}
		
		return repliesToAnswerById;
	}
	
	
	@Override
	public Answer getAnswerById(int answerId) throws SQLException {
		String getAnswerById = "SELECT * FROM ForumAnswers WHERE id = ? AND isDeleted = FALSE";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAnswerById)){
			pstmt.setInt(1, answerId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
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
				return a;
			}
		}
		return null; // Answer not found
	}
	
	@Override
	public List<Answer> getCorrectAnswers() throws SQLException {
		List<Answer> correctAnswers = new ArrayList<>();
    	String getCorrectAnswers = "SELECT * FROM ForumAnswers WHERE isCorrect = TRUE AND isDeleted = FALSE";
    	try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getCorrectAnswers)){
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
				correctAnswers.add(a);
			}
    	}
    	return correctAnswers;
    }
    
	@Override
    public List<Answer> getAnswersBySearch(String searchString) throws SQLException {
    	List<Answer> answersBySearch = new ArrayList<>();
    	String getAnswersBySearch = "SELECT * FROM ForumAnswers WHERE body LIKE ? AND isDeleted = FALSE";
    	try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAnswersBySearch)){
			pstmt.setString(1, "%" + searchString + "%");
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
				answersBySearch.add(a);
			}
    	}
    	return answersBySearch;
    }
    
	
	// ===================
    // 		 UPDATE 
 	// ===================
    
	@Override
    public boolean updateAnswer(int id, String newText, boolean newCorrect) throws SQLException {
		String updateAnswer = "UPDATE ForumAnswers SET body = ?, isCorrect = ? WHERE id = ? AND isDeleted = FALSE";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(updateAnswer)){
			pstmt.setString(1, newText);
			pstmt.setBoolean(2, newCorrect);
			pstmt.setInt(3, id);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected == 0) {
				if (Debug.isEnabled) System.out.println("No answer found with ID: " + id + " to update.");
				return false; // No answer found to update
			}
		}
		return true;
	}
	
	// ===================
    // 		 DESTROY 
 	// ===================
	
	@Override
	public boolean deleteAnswerById(int id) throws SQLException {
		String deleteAnswerById = "UPDATE ForumAnswers SET isDeleted = TRUE WHERE id = ?";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(deleteAnswerById)){
			pstmt.setInt(1, id);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected == 0) {
				if (Debug.isEnabled) System.out.println("No answer found with ID: " + id + " to delete.");
				return false; // No answer found to delete
			}
		}
		return true;
	}
	
	@Override
	public boolean deleteByQuestionId(int questionId) throws SQLException {
		String deleteByQuestionId = "UPDATE ForumAnswers SET isDeleted = TRUE WHERE questionId = ?";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(deleteByQuestionId)){
			pstmt.setInt(1, questionId);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected == 0) {
				if (Debug.isEnabled) System.out.println("No answers found for question ID: " + questionId + " to delete.");
				return false; // No answers found to delete
			}
		}
		return true;
	}

}