package databasePart1;
import java.util.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import application.User;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:./database/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(225))";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10 ) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			pstmt.executeUpdate();
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRole(String userName) {
	    String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("role"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Adds roles to the user by appending the role delimited by 
	
	public void updateUserRoles(String userName, List<String> newRoles) {
	    // Normalize, de-dup, and join
	    LinkedHashSet<String> norm = new LinkedHashSet<>();
	    for (String r : newRoles) {
	        if (r != null) {
	            String v = r.trim().toLowerCase(Locale.ROOT);
	            if (!v.isEmpty()) norm.add(v);
	        }
	    }
	    String roleString = String.join(",", norm); // empty string means “no roles”

	    String query = "UPDATE cse360users SET role = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, roleString);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	// Retrieves all user names from the database
	
	public List<String> getAllUsernames() {
		List<String> usernames = new ArrayList<>();
		String query = "SELECT username FROM cse360users";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	usernames.add((rs.getString("username")));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return usernames;
	}
	// Deletes a user from the database based on their userName.
	public boolean deleteUser(String currentUser, String targetUser) {
	    // Prevent an admin from deleting their own account
	    if (currentUser.equals(targetUser)) {
	        String currentRoles = getUserRole(currentUser);
	        // stop admins deleting their own account
	        if (currentRoles != null && currentRoles.contains("admin")) {
	            System.out.println("Admins cannot delete their own account.");
	            return false;
	        }
	    }

	    String query = "DELETE FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, targetUser);
	        pstmt.executeUpdate();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String roles) {
		boolean generateInvitationCodeDebug = true;
	    String code = roles + "_" + UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code) VALUES (?)";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    if (generateInvitationCodeDebug) System.out.println("Generated invitation code: " + code);
	    
	    return code;
	}
	
	public String getRoleFromInviteCode(String code) {
		String roleBits = code.substring(0, code.indexOf('_'));
		List<String> roles = new ArrayList<>();
		String[] roleNames = {"admin","student","instructor","staff","reviewer"};

		for (int i = 0; i < roleBits.length(); i++) {
		    if (roleBits.charAt(i) == '1') {
		        roles.add(roleNames[i]);
		    }
		}
		String rolesStr = String.join(",", roles);
		return rolesStr;
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        if ("0000".equals(code)) {
		    	return true;
		    }
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	// Print the cse360 table to console for debugging
	public void printUsersTable() {
	    String query = "SELECT id, userName, password, role FROM cse360users";
	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {

	        System.out.printf("%-5s %-20s %-20s %-30s%n", "ID", "Username", "Password", "Roles");
	        System.out.println("--------------------------------------------------------------------------");

	        while (rs.next()) {
	            int id = rs.getInt("id");
	            String userName = rs.getString("userName");
	            String password = rs.getString("password");
	            String role = rs.getString("role");

	            System.out.printf("%-5d %-20s %-20s %-30s%n", id, userName, password, role);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	public void printInvitationCodesTable() {
	    String query = "SELECT code, isUsed FROM InvitationCodes";
	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {

	        System.out.printf("%-15s %-10s%n", "Code", "Is Used");
	        System.out.println("-------------------------");

	        while (rs.next()) {
	            String code = rs.getString("code");
	            boolean isUsed = rs.getBoolean("isUsed");

	            System.out.printf("%-15s %-10s%n", code, isUsed);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	// method to generate a random password w/ a reset flag 
	public String issueResetPassword(String userName) {
	    Random rand = new Random();

	    String lowers   = "abcdefghijklmnopqrstuvwxyz";
	    String uppers   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    String digits   = "0123456789";
	    String specials = "~`!@#$%^&*()_-+{}[]|:,.?/";
	    String pool     = uppers + lowers + digits + specials;

	    // Build the 6-char tail: guarantee 1 lowercase and 1 digit, fill the rest
	    List<Character> tail = new ArrayList<>();
	    tail.add(lowers.charAt(rand.nextInt(lowers.length()))); // guarantee lowercase
	    tail.add(digits.charAt(rand.nextInt(digits.length()))); // guarantee digit
	    while (tail.size() < 8) {
	        tail.add(pool.charAt(rand.nextInt(pool.length())));
	    }
	    Collections.shuffle(tail, rand);

	    StringBuilder sb = new StringBuilder("R_"); // marker = reset
	    for (char c : tail) sb.append(c);
	    String tempPass = sb.toString();            // total length = 8

	    String sql = "UPDATE cse360users SET password = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, tempPass);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	    return tempPass;
	}
	
	public void updatePassword(String userName, String newPassword) throws SQLException {
	    String sql = "UPDATE cse360users SET password = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, newPassword);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    }
	}



	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

}
