package databasePart1;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.sql.Connection;

import Models.User;

public class UserDBManager implements IUserDBManager {
	
	private final DatabaseHelper dbHelper;
	//private final Connection connection;
	
	public UserDBManager(DatabaseHelper dbHelper) throws SQLException {
		this.dbHelper = dbHelper;
	}

	@Override
	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		// TODO Auto-generated method stub
		String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
		try (Connection connection = dbHelper.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole().name());
			pstmt.executeUpdate();
		}
	}

	@Override
	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (Connection connection = dbHelper.getConnection(); 
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
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
		String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
		try (Connection connection = dbHelper.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
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

	@Override
	public String getUserRole(String userName) {
		String query = "SELECT role FROM cse360users WHERE userName = ?";
		try (Connection connection = dbHelper.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(query)) {
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

	@Override
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
	    try (Connection connection = dbHelper.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, roleString);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	@Override
	public List<String> getAllUsernames() {
		List<String> usernames = new ArrayList<>();
		String query = "SELECT userName FROM cse360users";
		try (Connection connection = dbHelper.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	usernames.add((rs.getString("userName")));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return usernames;
	}

	@Override
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
	    try (Connection connection = dbHelper.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, targetUser);
	        pstmt.executeUpdate();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	@Override
	public void printUsersTable() {
		String query = "SELECT id, userName, password, role FROM cse360users";
		try (Connection connection = dbHelper.getConnection(); 
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

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		
	}

	@Override
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
	    try (Connection connection = dbHelper.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, tempPass);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	    return tempPass;
	}

	@Override
	public void updatePassword(String userName, String newPassword) throws SQLException {
		String sql = "UPDATE cse360users SET password = ? WHERE userName = ?";
		try (Connection connection = dbHelper.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, newPassword);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    }
	}
		
}