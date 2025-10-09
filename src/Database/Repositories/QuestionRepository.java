package Database.Repositories;

import Models.Question;
import Utils.Debug;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Database.IGeneralDBManager;
import Database.Repositories.Interfaces.IQuestionRepository;

public class QuestionRepository implements IQuestionRepository {
	
	IGeneralDBManager generalDBManager;
	
	public QuestionRepository(IGeneralDBManager generalDBManager) {
		this.generalDBManager = generalDBManager;
	}
	
	// ===================
    // 		 CREATE 
 	// ===================
	
	@Override
	public boolean insertQuestion(Question q) throws SQLException {
		String insertQuestion = "INSERT INTO ForumQuestions (userId, title, body) VALUES (?, ?, ?)";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(insertQuestion, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setInt(1, q.getAuthorId());
			pstmt.setString(2,q.getTitle());
			pstmt.setString(3,q.getText());
			pstmt.executeUpdate();
			
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int questionId = generatedKeys.getInt(1);
					if (Debug.isEnabled) System.out.println("Inserted question ID: " + questionId);
					q.setId(questionId);
				} else {
					throw new SQLException("Inserting question failed, no ID obtained.");
				}
			}
			 
		}
		return true; // Question created
	}
	
	// ===================
    // 		  READ 
 	// ===================
	
	@Override
	public List<Question> getAllQuestions() throws SQLException {
		List<Question> allQuestions = new ArrayList<>();
		String getAllQuestions = "SELECT * FROM ForumQuestions WHERE isDeleted = FALSE";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAllQuestions)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Question q = new Question(
						rs.getInt("id"),
						rs.getInt("userId"),
						rs.getString("title"),
						rs.getString("body"),
						rs.getBoolean("answered"), 
						rs.getBoolean("resolved"),
						rs.getBoolean("isDeleted"),
						rs.getInt("upvoteCount")
				);
				allQuestions.add(q);
			}
		}
		
		return allQuestions;
	}
	
	@Override
	public Question getQuestionById(int searchId) throws SQLException {
		String getQuestionById = "SELECT * FROM ForumQuestions WHERE id = ? AND isDeleted = FALSE";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getQuestionById)) {
			pstmt.setInt(1, searchId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Question q = new Question(
						rs.getInt("id"),
						rs.getInt("userId"),
						rs.getString("title"),
						rs.getString("body"),
						rs.getBoolean("answered"), 
						rs.getBoolean("resolved"),
						rs.getBoolean("isDeleted"),
						rs.getInt("upvoteCount")
				);
				return q;
			}
		}
		return null;
	}
	
	@Override
	public List<Question> getAnsweredQuestions() throws SQLException {
		List<Question> answeredQuestions = new ArrayList<>();
		String getAnsweredQuestions = "SELECT * FROM ForumQuestions WHERE answered = TRUE AND isDeleted = FALSE";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAnsweredQuestions)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Question q = new Question(
						rs.getInt("id"),
						rs.getInt("userId"),
						rs.getString("title"),
						rs.getString("body"),
						rs.getBoolean("answered"), 
						rs.getBoolean("resolved"),
						rs.getBoolean("isDeleted"),
						rs.getInt("upvoteCount")
				);
				answeredQuestions.add(q);
			}
		}
		return answeredQuestions;
	}
	
	@Override
	public List<Question> getUnansweredQuestions() throws SQLException {
		List<Question> unansweredQuestions = new ArrayList<>();
		String getUnansweredQuestions = "SELECT * FROM ForumQuestions WHERE answered = FALSE AND isDeleted = FALSE";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getUnansweredQuestions)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Question q = new Question(
						rs.getInt("id"),
						rs.getInt("userId"),
						rs.getString("title"),
						rs.getString("body"),
						rs.getBoolean("answered"), 
						rs.getBoolean("resolved"),
						rs.getBoolean("isDeleted"),
						rs.getInt("upvoteCount")
				);
				unansweredQuestions.add(q);
			}
		}
		return unansweredQuestions;
	}
	
	// TODO: Likely need a way to return Answered/Unanswered by User/other characteristics
	
	@Override
	public List<Question> getQuestionsBySearch(String searchString) throws SQLException {
		List<Question> questions = new ArrayList<>();
		String getQuestionsBySearch = "SELECT * FROM ForumQuestions WHERE (title LIKE ? OR body LIKE ?) AND isDeleted = FALSE";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getQuestionsBySearch)) {
			String searchPattern = "%" + searchString + "%";
			pstmt.setString(1, searchPattern);
			pstmt.setString(2, searchPattern);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Question q = new Question(
						rs.getInt("id"),
						rs.getInt("userId"),
						rs.getString("title"),
						rs.getString("body"),
						rs.getBoolean("answered"), 
						rs.getBoolean("resolved"),
						rs.getBoolean("isDeleted"),
						rs.getInt("upvoteCount")
				);
				questions.add(q);
			}
		}
		return questions;
	}
	
	@Override
	public List<Question> getQuestionsFromUser(int userId) throws SQLException {
		List<Question> questions = new ArrayList<>();
		String getQuestionsFromUser = "SELECT * FROM ForumQuestions WHERE userId = ? AND isDeleted = FALSE";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getQuestionsFromUser)) {
			pstmt.setInt(1, userId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Question q = new Question(
						rs.getInt("id"),
						rs.getInt("userId"),
						rs.getString("title"),
						rs.getString("body"),
						rs.getBoolean("answered"), 
						rs.getBoolean("resolved"),
						rs.getBoolean("isDeleted"),
						rs.getInt("upvoteCount")
				);
				questions.add(q);
			}
		}
		return questions;
	}
	
	// ===================
    // 		 UPDATE 
 	// ===================
	
	@Override
	public boolean updateQuestion(int id, String newText) throws SQLException {
		String updateQuestion = "UPDATE ForumQuestions SET body = ?, lastUpdated = CURRENT_TIMESTAMP WHERE id = ? AND isDeleted = FALSE";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(updateQuestion)) {
			pstmt.setString(1, newText);
			pstmt.setInt(2, id);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				if (Debug.isEnabled) System.out.println("Question with ID " + id + " updated successfully.");
				return true;
			} else {
				if (Debug.isEnabled) System.out.println("No question found with ID " + id + " to update.");
			}
		}
		return false;
	}
	
	// ===================
    // 		DESTROY 
 	// ===================
	
	
	@Override
	public boolean deleteQuestion(int id) throws SQLException {
		String deleteQuestion = "UPDATE ForumQuestions SET isDeleted = TRUE WHERE id = ?";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(deleteQuestion)) {
			pstmt.setInt(1, id);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				if (Debug.isEnabled) System.out.println("Question with ID " + id + " marked as deleted.");
				return true;
			} else {
				if (Debug.isEnabled) System.out.println("No question found with ID " + id + " to delete.");
			}
		}
		return false;
	}
}