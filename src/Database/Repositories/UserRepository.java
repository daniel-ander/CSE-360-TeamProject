package Database.Repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import Database.IGeneralDBManager;
import Database.Repositories.Interfaces.IUserRepository;
import java.sql.Statement;

import java.sql.Connection;

import Models.User;

public class UserRepository implements IUserRepository {
	
	private final IGeneralDBManager generalDBManager;
	
	public UserRepository(IGeneralDBManager generalDBManager) {
		this.generalDBManager = generalDBManager;
	}
	
	// ===================
    // 		 CREATE 
 	// ===================
	
	@Override
	public int insertNewUser(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
		
		try (Connection connection = generalDBManager.getConnection(); 
		     PreparedStatement pstmt = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole().name());
			pstmt.executeUpdate();

			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int userId = generatedKeys.getInt(1);
					System.out.println("Inserted user ID: " + userId);
					return userId;
				} else {
					throw new SQLException("Inserting user failed, no ID obtained.");
				}
			}
		}
	}
	
	// ===================
    // 		 READ
 	// ===================
	
	@Override
	public boolean doesUserExist(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (Connection connection = generalDBManager.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole().name());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	@Override
	public boolean doesUserExistByName(String userName) throws SQLException {
		String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
		try (Connection connection = generalDBManager.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	@Override
	public String getUserRole(String userName) throws SQLException {
		String query = "SELECT role FROM cse360users WHERE userName = ?";
		try (Connection connection = generalDBManager.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("role"); // Return the role if user exists
	        }
	    } 
	    return null; // If no user exists or an error occurs
	}
	
	@Override
	public List<String> getAllUsernames() throws SQLException {
		List<String> usernames = new ArrayList<>();
		String query = "SELECT userName FROM cse360users";
		try (Connection connection = generalDBManager.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	usernames.add((rs.getString("userName")));
	        }
	    }
		return usernames;
	}
	
	// ===================
    // 		 UPDATE 
 	// ===================
	
	@Override
	public void updateUserRole(String userName, String roleString) throws SQLException {
	    String query = "UPDATE cse360users SET role = ? WHERE userName = ?";
	    try (Connection connection = generalDBManager.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, roleString);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    }
	}
	
	@Override
	public String updatePasswordForUser(String userName, String newPassword) throws SQLException {
	    String sql = "UPDATE cse360users SET password = ? WHERE userName = ?";
	    try (Connection connection = generalDBManager.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, newPassword);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    } 
	    return newPassword;
	}

	// ===================
    // 		 DESTROY 
 	// ===================

	@Override
	public boolean deleteUser(String targetUser) throws SQLException {
	    String query = "DELETE FROM cse360users WHERE userName = ?";
	    try (Connection connection = generalDBManager.getConnection(); 
	         PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, targetUser);
	        pstmt.executeUpdate();
	        return true;
	    }
	}
	
	// ===================
    // 		 OTHER 
 	// ===================

	@Override
	public void printAllRows() throws SQLException {
		String query = "SELECT id, userName, password, role FROM cse360users";
		try (Connection connection = generalDBManager.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(query);
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

	    } 
	}
		
}