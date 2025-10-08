package Database;
import java.sql.*;


/**
 * The GeneralDBManager class is responsible for managing the connection to the database,
 * and general actions related to the meta functions of the database
 */


public class GeneralDBManager implements IGeneralDBManager{

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:./database/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 
	
	// TODO: - Not entirely sure what this is.
	private static int forumPostIdNumber = 0;
	
	// ===================
    // 		 CREATE 
 	// ===================

	@Override
	public void initializeDBIfNeeded() throws SQLException {
		try {
			// TODO: - Should probably display an alert if an error arises.
			Class.forName(JDBC_DRIVER); // Necessary for database
			
			createTables();
		} catch (ClassNotFoundException e) { // feels like this catch belongs here rather than the implementer
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	@Override
	public void createTables() throws SQLException {
	    String userTableQuery = "CREATE TABLE IF NOT EXISTS cse360users ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "userName VARCHAR(255) UNIQUE, "
	            + "password VARCHAR(255), "
	            + "role VARCHAR(225))";

	    //TODO: - Invitation codes should probably have a foreign key to the user that created it and used it
	    //      - or an entirely new table that tracks which user created which invitation code
	    String invitationCodesTableQuery = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    
	    String forumQuestionsTableQuery = "CREATE TABLE IF NOT EXISTS ForumQuestions ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "userId INT, "
	            + "title VARCHAR(255), "
	            + "body TEXT, "
	            + "lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "answered BOOLEAN DEFAULT FALSE, "
	            + "resolved BOOLEAN DEFAULT FALSE, "
	            + "isDeleted BOOLEAN DEFAULT FALSE, "
	            + "upvoteCount INT DEFAULT 0, "
	            + "FOREIGN KEY (userId) REFERENCES cse360users(id))";
	    
	    String forumAnswersTableQuery = "CREATE TABLE IF NOT EXISTS ForumAnswers ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "questionId INT, "
	            + "parentAnswerId INT DEFAULT -1, "
	            + "userId INT, "
	            + "body TEXT, "
	            + "isCorrect BOOLEAN DEFAULT FALSE, "
	            + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "isDeleted BOOLEAN DEFAULT FALSE, "
	            + "upvoteCount INT DEFAULT 0, "
	            + "FOREIGN KEY (questionId) REFERENCES ForumQuestions(id), "
	            + "FOREIGN KEY (parentAnswerId) REFERENCES ForumAnswers(id), "
	            + "FOREIGN KEY (userId) REFERENCES cse360users(id))";
	    
	    //TODO: - Other tables need to be added

	    try (Connection connection = getConnection();
	         Statement stmt = connection.createStatement()) {

	        stmt.execute(userTableQuery);
	        stmt.execute(invitationCodesTableQuery);
	        stmt.execute(forumQuestionsTableQuery);
	        stmt.execute(forumAnswersTableQuery);
	    }
	}
	
	// ===================
    // 		 READ
 	// ===================

	@Override
	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		try (Connection connection = getConnection();
		         Statement stmt = connection.createStatement()) {
			ResultSet resultSet = stmt.executeQuery(query);
			
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		}
		return true;
	}
	
	@Override
	public int getNextForumPostId() {
		forumPostIdNumber += 1;
		return forumPostIdNumber;
		// TODO: This method should be updated to fetch the next ID from the database once the forum posts table is implemented.

	}
	
	public Connection getConnection() throws SQLException {
		System.out.println("Connecting to database...");
		return DriverManager.getConnection(DB_URL, USER, PASS);
	}

}
