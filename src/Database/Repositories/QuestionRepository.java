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
		String insertQuestion = String.format("INSERT INTO %s (userId, title, body, answered, resolved, isDeleted, upvoteCount) VALUES (?, ?, ?, ?, ?, ?, ?)", Question.tableName);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(insertQuestion, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setInt(1, q.getAuthorId());
			pstmt.setString(2,q.getTitleText());
			pstmt.setString(3,q.getBodyText());
			pstmt.setBoolean(4, q.getAnsweredStatus());
			pstmt.setBoolean(5, q.getResolvedStatus());
			pstmt.setBoolean(6, q.getIsDeleted());
			pstmt.setInt(7, q.getUpvoteCount());
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
		String getAllQuestions = String.format("SELECT * FROM %s", Question.tableName);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAllQuestions)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Question q = new Question(
						rs.getInt("id"),
						rs.getInt("userId"),
						rs.getString("title"),
						rs.getString("body"),
						rs.getTimestamp("created"),
						rs.getTimestamp("lastUpdated"),
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
	public List<Question> getLimitedAmountOfQuestions(int amount) throws SQLException {
		List<Question> questions = new ArrayList<>();
		String getAllQuestions = String.format("SELECT * FROM %s LIMIT %s", Question.tableName, amount);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAllQuestions)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Question q = new Question(
						rs.getInt("id"),
						rs.getInt("userId"),
						rs.getString("title"),
						rs.getString("body"),
						rs.getTimestamp("created"),
						rs.getTimestamp("lastUpdated"),
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
	public Question getQuestionById(int searchId) throws SQLException {
		String getQuestionById = String.format("SELECT * FROM %s WHERE id = ?", Question.tableName);
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
						rs.getTimestamp("created"),
						rs.getTimestamp("lastUpdated"),
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
		String getAnsweredQuestions = String.format("SELECT * FROM %s WHERE answered = TRUE", Question.tableName);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAnsweredQuestions)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Question q = new Question(
						rs.getInt("id"),
						rs.getInt("userId"),
						rs.getString("title"),
						rs.getString("body"),
						rs.getTimestamp("created"),
						rs.getTimestamp("lastUpdated"),
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
		String getUnansweredQuestions = String.format("SELECT * FROM %s WHERE answered = FALSE", Question.tableName);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getUnansweredQuestions)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Question q = new Question(
						rs.getInt("id"),
						rs.getInt("userId"),
						rs.getString("title"),
						rs.getString("body"),
						rs.getTimestamp("created"),
						rs.getTimestamp("lastUpdated"),
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
		String getQuestionsBySearch = String.format("SELECT * FROM %s WHERE (title LIKE ? OR body LIKE ?)", Question.tableName);
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
						rs.getTimestamp("created"),
						rs.getTimestamp("lastUpdated"),
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
		String getQuestionsFromUser = String.format("SELECT * FROM %s WHERE userId = ?", Question.tableName);
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
						rs.getTimestamp("created"),
						rs.getTimestamp("lastUpdated"),
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
	public boolean updateQuestion(int id, String newTitle, String newText) throws SQLException {
		String updateQuestion = String.format("UPDATE %s SET title = ?, body = ?, lastUpdated = CURRENT_TIMESTAMP WHERE id = ?", Question.tableName);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(updateQuestion)) {
			pstmt.setString(1, newTitle);
			pstmt.setString(2, newText);
			pstmt.setInt(3, id);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				if (Debug.isEnabled) System.out.println("Question with ID " + id + " updated successfully to title: " + newTitle + " and body: " + newText);
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
		String deleteQuestion = String.format("UPDATE %s SET isDeleted = TRUE WHERE id = ?", Question.tableName);
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