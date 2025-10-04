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

	    String invitationCodesTableQuery = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";

	    try (Connection connection = getConnection();
	         Statement stmt = connection.createStatement()) {

	        stmt.execute(userTableQuery);
	        stmt.execute(invitationCodesTableQuery);
	    }
	}

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
	
	public Connection getConnection() throws SQLException {
		System.out.println("Connecting to database...");
		return DriverManager.getConnection(DB_URL, USER, PASS);
	}

}
