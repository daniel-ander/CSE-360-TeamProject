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

/**
 * The AnswerRepository class provides database operations for Answer objects.
 * It implements CRUD functionality (create, read, update, delete) and helper methods
 * to interact with the answers table in the database.
 */
public class AnswerRepository implements IAnswerRepository {
	private final IGeneralDBManager generalDBManager;
	
    /**
     * Creates a new AnswerRepository with the given database manager.
     */
	public AnswerRepository(IGeneralDBManager generalDBManager) {
		this.generalDBManager = generalDBManager;
	}
	
	/**
     * Inserts a new answer into the database and returns the populated Answer object.
     */
	@Override
	public Answer insertAnswer(Answer a) throws SQLException {
	    String insertSQL = String.format(
	        "INSERT INTO %s (questionId, parentAnswerId, userId, body, isReview) VALUES (?, ?, ?, ?, ?)",
	        Answer.tableName
	    );

	    try (Connection connection = generalDBManager.getConnection();
	         PreparedStatement pstmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

	        pstmt.setInt(1, a.getQuestionId());
	        if (a.getParentAnswerId() == null)
	            pstmt.setNull(2, java.sql.Types.INTEGER);
	        else
	            pstmt.setInt(2, a.getParentAnswerId());

	        pstmt.setInt(3, a.getAuthorId());
	        pstmt.setString(4, a.getBodyText());
	        pstmt.setBoolean(5, a.getIsReview());
	        pstmt.executeUpdate();

	        try (ResultSet keys = pstmt.getGeneratedKeys()) {
	            if (keys.next())
	                a.setId(keys.getInt(1));
	        }

	        // Fetch the full row from the database
	        String selectSQL = String.format(
	            "SELECT id, questionId, parentAnswerId, userId, authorName, body, isCorrect, created, lastUpdated, isDeleted, isReview, upvoteCount " +
	            "FROM %s WHERE id = ?",
	            Answer.tableName
	        );

	        try (PreparedStatement selectStmt = connection.prepareStatement(selectSQL)) {
	            selectStmt.setInt(1, a.getId());
	            try (ResultSet rs = selectStmt.executeQuery()) {
	                if (rs.next()) {
	                    a.setCreatedTimestamp(rs.getTimestamp("created"));
	                    a.setLastUpdatedTimestamp(rs.getTimestamp("lastUpdated"));
	                    a.setIsCorrect(rs.getBoolean("isCorrect"));
	                    a.setIsDeleted(rs.getBoolean("isDeleted"));
	                    a.setIsReview(rs.getBoolean("isReview"));
	                    a.setUpvoteCount(rs.getInt("upvoteCount"));
	                }
	            }
	        }
	    }

	    return a;
	}

	
    /**
     * Retrieves all answers from the database.
	 * TODO: we likely want to retrieve all and let the UI handle filtering
	 * All of these queries currently filter deleted answers
     */	
	@Override
	public List<Answer> getAll() throws SQLException {
		List<Answer> allAnswers = new ArrayList<>();
		String getAllAnswers = String.format("SELECT * FROM %s", Answer.tableName);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAllAnswers)){
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Answer a = filledAnswerModel(rs);
				
				allAnswers.add(a);
			}
		}
		return allAnswers;
	}
	
    /**
     * Retrieves all answers for a given question ID.
     */
	@Override
	public List<Answer> getAnswersByQuestionId(int questionId) throws SQLException {
		List<Answer> allAnswersForQuestion = new ArrayList<>();
		//TODO: we might need this to check parentId depending on how we want to display answers
		//		for now we will just get all answers for a question
		String getAllAnswersForQuestion = String.format("SELECT * FROM %s WHERE questionId = ?", Answer.tableName);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAllAnswersForQuestion)){
			pstmt.setInt(1, questionId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Answer a = filledAnswerModel(rs);
				
				allAnswersForQuestion.add(a);
			}
		}
		return allAnswersForQuestion;
	}
	
    /**
     * Retrieves all replies to a given parent answer ID.
     */
	@Override
	public List<Answer> getRepliesToAnswerById(int parentAnswerId) throws SQLException {
		List<Answer> repliesToAnswerById = new ArrayList<>();
		String getRepliesToAnswerById = String.format("SELECT * FROM %s WHERE parentAnswerId = ?", Answer.tableName);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getRepliesToAnswerById)){
			pstmt.setInt(1, parentAnswerId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Answer a = filledAnswerModel(rs);
				repliesToAnswerById.add(a);
			}
		}
		
		return repliesToAnswerById;
	}
	
    /**
     * Retrieves a single answer by its ID.
     */
	@Override
	public Answer getAnswerById(int answerId) throws SQLException {
		String getAnswerById = String.format("SELECT * FROM %s WHERE id = ?", Answer.tableName);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAnswerById)){
			pstmt.setInt(1, answerId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Answer a = filledAnswerModel(rs);
				return a;
			}
		}
		return null; // Answer not found
	}
	
    /**
     * Retrieves all answers marked as correct.
     */
	@Override
	public List<Answer> getCorrectAnswers() throws SQLException {
		List<Answer> correctAnswers = new ArrayList<>();
    	String getCorrectAnswers = String.format("SELECT * FROM %s WHERE isCorrect = TRUE", Answer.tableName);
    	try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getCorrectAnswers)){
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Answer a = filledAnswerModel(rs);
				correctAnswers.add(a);
			}
    	}
    	return correctAnswers;
    }
    
    /**
     * Retrieves answers that match a search string in their body text.
     */
	@Override
    public List<Answer> getAnswersBySearch(String searchString) throws SQLException {
    	List<Answer> answersBySearch = new ArrayList<>();
    	String getAnswersBySearch = String.format("SELECT * FROM %s WHERE body LIKE ?", Answer.tableName);
    	try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAnswersBySearch)){
			pstmt.setString(1, "%" + searchString + "%");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Answer a = filledAnswerModel(rs);
				answersBySearch.add(a);
			}
    	}
    	return answersBySearch;
    }
    
    /**
     * Updates the text and correctness flag of an answer by its ID.
     */
	@Override
    public boolean updateAnswer(int id, String newText, boolean newCorrect) throws SQLException {
		String updateAnswer = String.format("UPDATE %s SET body = ?, isCorrect = ? WHERE id = ?", Answer.tableName);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(updateAnswer)){
			pstmt.setString(1, newText);
			pstmt.setBoolean(2, newCorrect);
			pstmt.setInt(3, id);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected == 0) {
				Debug.write("No answer found with ID: " + id + " to update.");
				return false; // No answer found to update
			}
		}
		return true;
	}
	
    /**
     * Marks an answer as deleted by its ID.
     */
	@Override
	public boolean deleteAnswerById(int id) throws SQLException {
		String deleteAnswerById = String.format("UPDATE %s SET isDeleted = TRUE WHERE id = ?", Answer.tableName);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(deleteAnswerById)){
			pstmt.setInt(1, id);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected == 0) {
				Debug.write("No answer found with ID: " + id + " to delete.");
				return false; // No answer found to delete
			}
		}
		return true;
	}
	
    /**
     * Marks all answers for a given question ID as deleted.
     */
	@Override
	public boolean deleteByQuestionId(int questionId) throws SQLException {
		String deleteByQuestionId = String.format("UPDATE %s SET isDeleted = TRUE WHERE questionId = ?", Answer.tableName);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(deleteByQuestionId)){
			pstmt.setInt(1, questionId);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected == 0) {
				Debug.write("No answers found for question ID: " + questionId + " to delete.");
				return false; // No answers found to delete
			}
		}
		return true;
	}
	
	
	private Answer filledAnswerModel(ResultSet rs) throws SQLException {
		return new Answer(
				rs.getInt("id"),
				rs.getInt("questionId"),
				rs.getInt("parentAnswerId") == 0 ? null : rs.getInt("parentAnswerId"),
				rs.getInt("userId"),
				rs.getString("authorName"),
				rs.getString("body"),
				rs.getTimestamp("created"),
				rs.getTimestamp("lastUpdated"),
				rs.getInt("upvoteCount"),
				rs.getBoolean("isCorrect"),
				rs.getBoolean("isDeleted"),
				rs.getBoolean("isReview")
		);
	}

	/**
	 * Gets all answers from user id.
	 */
	@Override
	public List<Answer> getAnswersByUser(int userId) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}