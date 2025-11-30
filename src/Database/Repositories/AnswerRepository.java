package Database.Repositories;

import Models.Answer;
import Models.User;
import Models.DBModels.Rating;
import Utils.RatingStats;
import Utils.Debug;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import Models.DBModels.UserDTO;
import Models.Interfaces.ISharedUser;
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
	    String insertAnswerQuery = String.format(
	        "INSERT INTO %s (questionId, parentAnswerId, userId, body, isReview) VALUES (?, ?, ?, ?, ?)",
	        Answer.tableName
	    );

	    try (Connection connection = generalDBManager.getConnection();
	         PreparedStatement pstmt = connection.prepareStatement(insertAnswerQuery, Statement.RETURN_GENERATED_KEYS)) {

	        pstmt.setInt(1, a.getQuestionId());
	        if (a.getParentAnswerId() == null)
	            pstmt.setNull(2, java.sql.Types.INTEGER);
	        else
	            pstmt.setInt(2, a.getParentAnswerId());

	        pstmt.setInt(3, a.getAuthor().getId());
	        pstmt.setString(4, a.getBodyText());
	        pstmt.setBoolean(5, a.getIsReview());
	        pstmt.executeUpdate();

	        try (ResultSet keys = pstmt.getGeneratedKeys()) {
	            if (keys.next())
	                a.setId(keys.getInt(1));
	        }

	        // Fetch the full row from the database
	        String selectSQL = String.format(
	            "SELECT id, questionId, parentAnswerId, userId, body, isCorrect, created, lastUpdated, isDeleted, isReview, upvoteCount " +
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
	        if (a.getIsReview()) {
	            try (Connection conn = generalDBManager.getConnection();
	                 PreparedStatement ps = conn.prepareStatement(
	                     "INSERT INTO messages (review_id, sender_id, receiver_id, message_text) VALUES (?, ?, ?, ?)"
	                 )) {

	                // review_id = this answer’s id
	                ps.setInt(1, a.getId());
	                ps.setInt(2, a.getAuthor().getId());

	                // receiver = target user (question or parent answer’s author)
	                Integer receiverId = null;

	                if (a.getParentAnswerId() != null) {
	                    // review is on another answer → send to that answer’s author
	                    String q = "SELECT userId FROM " + Answer.tableName + " WHERE id = ?";
	                    try (PreparedStatement find = conn.prepareStatement(q)) {
	                        find.setInt(1, a.getParentAnswerId());
	                        try (ResultSet r = find.executeQuery()) {
	                            if (r.next()) receiverId = r.getInt("userId");
	                        }
	                    }
	                } else {
	                    // review is on a question → send to question’s author
	                    String q = "SELECT userId FROM questions WHERE id = ?";
	                    try (PreparedStatement find = conn.prepareStatement(q)) {
	                        find.setInt(1, a.getQuestionId());
	                        try (ResultSet r = find.executeQuery()) {
	                            if (r.next()) receiverId = r.getInt("userId");
	                        }
	                    }
	                }

	                if (receiverId == null) return a; // skip if no valid receiver

	                ps.setInt(3, receiverId);
	                ps.setString(4, a.getBodyText());
	                ps.executeUpdate();
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
		String getAllAnswersQuery = String.format(
			    "SELECT a.*, " +
			    "u.id AS user_id, " +
			    "u.userName AS user_name, " +
			    "u.role AS user_role " +
			    "FROM %s a " +
			    "JOIN %s u ON a.userId = u.id",
			    Answer.tableName,
			    User.tableName
			);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAllAnswersQuery)){
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Answer a = filledAnswerModel(rs);
				
				allAnswers.add(a);
			}
		}
		return allAnswers;
	}
	
	/**
	 * Gets all answers from user id.
	 */
	@Override
	public List<Answer> getAnswersByUser(int userId) throws SQLException {
        List<Answer> answersByUser = new ArrayList<>();
        String getAnswersByUserQuery = String.format(
        	    "SELECT a.*, " +
        	    "u.id AS user_id, " +
        	    "u.userName AS user_name, " +
        	    "u.role AS user_role " +
        	    "FROM %s a " +
        	    "JOIN %s u ON a.userId = u.id " +
        	    "WHERE a.userId = ?",
        	    Answer.tableName,
        	    User.tableName
        	);
        try (Connection connection = generalDBManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(getAnswersByUserQuery)){
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Answer a = filledAnswerModel(rs);
                answersByUser.add(a);
            }
        }
        return answersByUser;
    }
	
    /**
     * Retrieves all answers for a given question ID.
     */
	@Override
	public List<Answer> getAnswersByQuestionId(int questionId) throws SQLException {
		List<Answer> allAnswersForQuestion = new ArrayList<>();
		//TODO: we might need this to check parentId depending on how we want to display answers
		//		for now we will just get all answers for a question
		String getAllAnswersForQuestionQuery = String.format(
			    "SELECT a.*, " +
			    "u.id AS user_id, " +
			    "u.userName AS user_name, " +
			    "u.role AS user_role " +
			    "FROM %s a " +
			    "JOIN %s u ON a.userId = u.id " +
			    "WHERE a.questionId = ?",
			    Answer.tableName,
			    User.tableName
		);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAllAnswersForQuestionQuery)){
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
		String getRepliesToAnswerByIdQuery = String.format(
			    "SELECT a.*, " +
			    "u.id AS user_id, " +
			    "u.userName AS user_name, " +
			    "u.role AS user_role " +
			    "FROM %s a " +
			    "JOIN %s u ON a.userId = u.id " +
			    "WHERE a.parentAnswerId = ?",
			    Answer.tableName,
			    User.tableName
		);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getRepliesToAnswerByIdQuery)){
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
		String getAnswerByIdQuery = String.format(
			    "SELECT a.*, " +
			    "u.id AS user_id, " +
			    "u.userName AS user_name, " +
			    "u.role AS user_role " +
			    "FROM %s a " +
			    "JOIN %s u ON a.userId = u.id " +
			    "WHERE a.id = ?",
			    Answer.tableName,
			    User.tableName
			);

		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAnswerByIdQuery)){
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
		String getCorrectAnswersQuery = String.format(
			    "SELECT a.*, " +
			    "u.id AS user_id, " +
			    "u.userName AS user_name, " +
			    "u.role AS user_role " +
			    "FROM %s a " +
			    "JOIN %s u ON a.userId = u.id " +
			    "WHERE a.isCorrect = TRUE",
			    Answer.tableName,
			    User.tableName
			);

    	try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getCorrectAnswersQuery)){
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
    	String getAnswersBySearchQuery = String.format(
    		    "SELECT a.*, " +
    		    "u.id AS user_id, " +
    		    "u.userName AS user_name, " +
    		    "u.role AS user_role " +
    		    "FROM %s a " +
    		    "JOIN %s u ON a.userId = u.id " +
    		    "WHERE a.body LIKE ?",
    		    Answer.tableName,
    		    User.tableName
    		);

    	try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getAnswersBySearchQuery)){
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
		String updateAnswerQuery = String.format("UPDATE %s SET body = ?, isCorrect = ? WHERE id = ?", Answer.tableName);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(updateAnswerQuery)){
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
		String deleteAnswerByIdQuery = String.format("UPDATE %s SET isDeleted = TRUE WHERE id = ?", Answer.tableName);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(deleteAnswerByIdQuery)){
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
		String deleteByQuestionIdQuery = String.format("UPDATE %s SET isDeleted = TRUE WHERE questionId = ?", Answer.tableName);
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(deleteByQuestionIdQuery)){
			pstmt.setInt(1, questionId);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected == 0) {
				Debug.write("No answers found for question ID: " + questionId + " to delete.");
				return false; // No answers found to delete
			}
		}
		return true;
	}
	
	@Override
	public BigDecimal getRating(int answerId, int userId) throws SQLException {
	    String getRatingQuery = "SELECT rating FROM " + Rating.tableName + 
	                 " WHERE userId = ? AND answerId = ?";

	    try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(getRatingQuery)){
	    	pstmt.setInt(1, userId);
	    	pstmt.setInt(2, answerId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getBigDecimal("rating");  
	            } else {
	                return null;
	            }
	        }
	    }
	}
	
	/**
     * Adds a row to the ratings table for particular answer and user
     */
	@Override
	public RatingStats addRating(int answerId, int userId, BigDecimal rating) throws SQLException {
		String addRatingQuery  = String.format("INSERT INTO %s (userId, answerId, rating) VALUES (?, ?, ?)", Rating.tableName);
		
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(addRatingQuery)){
			pstmt.setInt(1, userId);
			pstmt.setInt(2, answerId);
			pstmt.setBigDecimal(3, rating); 

			pstmt.executeUpdate();
			
			return updateAnswerRatingInfo(answerId);
		}
	}
	
	/**
     * Adds a row to the ratings table for particular answer and user
     */
	@Override
	public RatingStats updateRating(int answerId, int userId, BigDecimal rating) throws SQLException {
	    // Update the existing rating for this user and answer
	    String updateRatingQuery = String.format(
	        "UPDATE %s SET rating = ? WHERE userId = ? AND answerId = ?",
	        Rating.tableName
	    );

	    try (Connection connection = generalDBManager.getConnection();
	         PreparedStatement pstmt = connection.prepareStatement(updateRatingQuery)) {

	        pstmt.setBigDecimal(1, rating);
	        pstmt.setInt(2, userId);
	        pstmt.setInt(3, answerId);

	        if(pstmt.executeUpdate() == 0) { // If for some reason the rating didn't already exist
	        	addRating(answerId, userId, rating);
	        } 
	        
	        return updateAnswerRatingInfo(answerId);
	    }
	}

	
	/**
     * Updates the average rating and upvoteCount fields on the answer
     */
	@Override
	public RatingStats updateAnswerRatingInfo(int answerId) throws SQLException {
		String addRatingQuery  = String.format("UPDATE %s SET averageRating = ?, upvoteCount = ? WHERE id = ?", Answer.tableName);
		RatingStats averageRatingAndCount = getAnswerRatingInfo(answerId); 
		
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(addRatingQuery)){
			pstmt.setBigDecimal(1, averageRatingAndCount.getAverageRating());
			pstmt.setInt(2, averageRatingAndCount.getUpvoteCount());
			pstmt.setInt(3, answerId);

			pstmt.executeUpdate();
		}
		
		return averageRatingAndCount;
	}
	
	/**
     * Deletes a row from the ratings table for particular answer and user
     */
	@Override
	public RatingStats deleteRating(int answerId, int userId) throws SQLException {
		String deleteRatingQuery  = String.format("DELETE FROM %s WHERE userId = ? AND answerId = ?", Rating.tableName);
		
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(deleteRatingQuery)){
			pstmt.setInt(1, userId);
			pstmt.setInt(2, answerId);

			if(pstmt.executeUpdate() > 0) {
				Debug.write("Rating removed");
			} else {
				Debug.write("No such rating to modify");
			}
			
			return updateAnswerRatingInfo(answerId);
		}
	}
	
	/**
     * Gets the average rating info for answer - which includes the rating and the upvote count
     */
	@Override
	public RatingStats getAnswerRatingInfo(int answerId) throws SQLException {
		String query = String.format(
			    "SELECT a.id AS answerId, " +
			    "COALESCE(AVG(r.rating), 0) AS averageRating, " +
			    "COUNT(r.rating) AS upvoteCount " + 
			    "FROM %s a " +
			    "LEFT JOIN %s r ON a.id = r.answerId " +
			    "WHERE a.id = ? " +
			    "GROUP BY a.id",
			    Answer.tableName, Rating.tableName
			);

	    try (Connection connection = generalDBManager.getConnection();
	         PreparedStatement pstmt = connection.prepareStatement(query)) {

	        pstmt.setInt(1, answerId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                BigDecimal avg = rs.getBigDecimal("averageRating");
	                int upvotes = rs.getInt("upvoteCount");

	                Debug.write("Fetched average rating and upvotes for answer: " + answerId);
	                
	                return new RatingStats(
	                    avg != null ? avg : BigDecimal.ZERO,
	                    upvotes
	                );
	            } else {
	                Debug.write("Answer not found, returning defaults for answer: " + answerId);
	                return new RatingStats(BigDecimal.ZERO, 0);
	            }
	        }
	    }
	}
	
	@Override
	public RatingStats getReviewerStats(int reviewerId) throws SQLException {
		String reviewerStatsQuery = String.format(
			    "SELECT AVG(r.rating) AS avgRating, " +
			    "       COUNT(r.id) AS totalVotes " +
			    "FROM %s r " +
			    "JOIN %s a ON a.id = r.answerId " +
			    "WHERE a.isReview = TRUE " +
			    "  AND a.userId = ?",
			    Rating.tableName,
			    Answer.tableName
			);
	    
//	    String reviewerStatsQuery = String.format(
//		        "SELECT AVG(r.rating) AS avgRating, "
//		      + "       COUNT(DISTINCT a.id) AS totalReviews "
//		      + "FROM %s r "
//		      + "JOIN %s a ON a.id = r.answerId "
//		      + "JOIN %s u ON u.id = a.userId "
//		      + "WHERE u.role = 'reviewer' "
//		      + "  AND a.isReview = TRUE "
//		      + "  AND u.id = ?",
//		        Rating.tableName,
//		        Answer.tableName,
//		        User.tableName
//		    );

	    try (Connection connection = generalDBManager.getConnection();
	         PreparedStatement pstmt = connection.prepareStatement(reviewerStatsQuery)) {

	        pstmt.setInt(1, reviewerId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                BigDecimal averageRating = rs.getBigDecimal("avgRating");
	                int totalVotes = rs.getInt("totalVotes");

	                if (averageRating == null) {
	                    averageRating = BigDecimal.ZERO;
	                }

	                return new RatingStats(averageRating, totalVotes);
	            }
	        }
	    }

	    return new RatingStats(BigDecimal.ZERO, 0);
	}


	private Answer filledAnswerModel(ResultSet rs) throws SQLException {
		UserDTO user = new UserDTO(
		        rs.getInt("user_id"),
		        rs.getString("user_name"),
		        ISharedUser.roleFromString(rs.getString("user_role"))
		    );
		
		return new Answer(
				rs.getInt("id"),
				rs.getInt("questionId"),
				rs.getInt("parentAnswerId") == 0 ? null : rs.getInt("parentAnswerId"),
				user,
				rs.getString("body"),
				rs.getTimestamp("created"),
				rs.getTimestamp("lastUpdated"),
				rs.getBigDecimal("averageRating"),
				rs.getInt("upvoteCount"),
				rs.getBoolean("isCorrect"),
				rs.getBoolean("isDeleted"),
				rs.getBoolean("isReview")
		);
	}

}