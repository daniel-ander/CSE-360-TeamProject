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
import Models.DBModels.FavoredReviewer;
import Models.Interfaces.ISharedUser;

/**
 * The UserRepository class provides database operations for User objects.
 * It implements CRUD functionality (create, read) and helper methods
 * to interact with the users table in the database.
 */
public class UserRepository implements IUserRepository {
	
	private final IGeneralDBManager generalDBManager;
	
    /**
     * Creates a new UserRepository with the given database manager.
     */
	public UserRepository(IGeneralDBManager generalDBManager) {
		this.generalDBManager = generalDBManager;
	}
	
    /**
     * Inserts a new user into the database and returns the generated user ID.
     * Also sets the ID in the provided User object.
     */
	@Override
	public int insertNewUser(User user) throws SQLException {
		String insertUser = String.format("INSERT INTO %s (userName, password, role) VALUES (?, ?, ?)", User.tableName);
		
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
					user.setId(userId);
					return userId;
				} else {
					throw new SQLException("Inserting user failed, no ID obtained.");
				}
			}
		}
	}
	
    /**
     * Checks if a user exists in the database by matching username, password, and role.
     */
	@Override
	public boolean doesUserExist(User user) throws SQLException {
		String query = String.format("SELECT * FROM %s WHERE userName = ? AND password = ? AND role = ?", User.tableName);
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
	
    /**
     * Checks if a user exists in the database by username only.
     */
	@Override
	public boolean doesUserExistByName(String userName) throws SQLException {
		String query = String.format("SELECT COUNT(*) FROM %s WHERE userName = ?", User.tableName);
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
	
    /**
     * Retrieves the role of a user by their username.
     */
	@Override
	public String getUserRole(String userName) throws SQLException {
		String query = String.format("SELECT role FROM %s WHERE userName = ?", User.tableName);
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
	
    /**
     * Retrieves all usernames from the database.
     */
	@Override
	public List<String> getAllUsernames() throws SQLException {
		List<String> usernames = new ArrayList<>();
		String query = String.format("SELECT userName FROM %s", User.tableName);
		try (Connection connection = generalDBManager.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	usernames.add((rs.getString("userName")));
	        }
	    }
		return usernames;
	}
	
    /**
     * Retrieves a user by their username and password.
     * Returns a User object if found, otherwise null.
     */
	@Override
	public User getUserByNameAndPassword(String userName, String password) throws SQLException {
	    String query = String.format("SELECT * FROM %s WHERE userName = ? AND password = ?", User.tableName);
	    try (Connection connection = generalDBManager.getConnection();
	         PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        pstmt.setString(2, password);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            int id = rs.getInt("id");
	            String dbUserName = rs.getString("userName");
	            String dbPassword = rs.getString("password");
	            String roleString = rs.getString("role");
	            User.Role role = ISharedUser.roleFromString(roleString);

	            User user = new User(id, dbUserName, dbPassword, role);
	            return user;
	        }
	    }
	    return null; // If no user exists or an error occurs
	}

    /**
     * Retrieves the user ID for a given username.
     * Returns -1 if the user does not exist.
     */
	@Override
	public int getUserIdByName(String userName) throws SQLException {
		String query = String.format("SELECT id FROM %s WHERE userName = ?", User.tableName);
		try (Connection connection = generalDBManager.getConnection(); 
				PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getInt("id"); // Return the ID if user exists
	        }
	    }
		return -1; // If no user exists or an error occurs
	}
	
    /**
     * Retrieves a list of other users sorted by recent interaction.
     */
	@Override
	public List<String> getUsersSortedByRecentInteraction(String userName) throws SQLException {
        List<String> usersByInteraction = new ArrayList<>();
        // Return other users (simple fallback) sorted by id descending as a proxy for recent interaction
        String query = String.format("SELECT userName FROM %s WHERE userName <> ? ORDER BY id DESC", User.tableName);
        try (Connection connection = generalDBManager.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)){
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    usersByInteraction.add(rs.getString("userName"));
                }
            }
        }
        return usersByInteraction;
    }

    /**
     * Updates the role of a user in the database.
     */
	@Override
	public void updateUserRole(String userName, String roleString) throws SQLException {
        String query = String.format("UPDATE %s SET role = ? WHERE userName = ?", User.tableName);
        try (Connection connection = generalDBManager.getConnection(); 
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, roleString);
            pstmt.setString(2, userName);
            pstmt.executeUpdate();
        }
    }

    /**
     * Updates the password for a user in the database.
     * Returns the new password string.
     */
    @Override
    public String updatePasswordForUser(String userName, String newPassword) throws SQLException {
        String sql = String.format("UPDATE %s SET password = ? WHERE userName = ?", User.tableName);
        try (Connection connection = generalDBManager.getConnection(); 
                PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, userName);
            pstmt.executeUpdate();
        } 
        return newPassword;
    }

    /**
     * Deletes a user from the database by username.
     */
    @Override
    public boolean deleteUser(String targetUser) throws SQLException {
        String query = String.format("DELETE FROM %s WHERE userName = ?", User.tableName);
        try (Connection connection = generalDBManager.getConnection(); 
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, targetUser);
            pstmt.executeUpdate();
            return true;
        }
    }
    
    /**
     * Prints all rows from the users table to the console.
     */
    @Override
    public void printAllRows() throws SQLException {
        String query = String.format("SELECT id, userName, password, role FROM %s", User.tableName);
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

    /**
     * Get user by name
     * @param userName
     */
    @Override
    public User getUserByName(String userName) throws SQLException {
        String query = String.format("SELECT * FROM %s WHERE userName = ?", User.tableName);
        try (Connection connection = generalDBManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String dbUserName = rs.getString("userName");
                    String dbPassword = rs.getString("password");
                    String roleString = rs.getString("role");
                    User.Role role = ISharedUser.roleFromString(roleString);

                    return new User(id, dbUserName, dbPassword, role);
                }
            }
        }
        return null;
    }

    /**
     * Get user by id
     * @param id
     */
    @Override
    public User getUserById(int id) throws SQLException {
        String query = String.format("SELECT * FROM %s WHERE id = ?", User.tableName);
        try (Connection connection = generalDBManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String dbUserName = rs.getString("userName");
                    String dbPassword = rs.getString("password");
                    String roleString = rs.getString("role");
                    User.Role role = ISharedUser.roleFromString(roleString);

                    return new User(userId, dbUserName, dbPassword, role);
                }
            }
        }
        return null;
    }

    @Override
    public void toggleIsFavoredReviewer(int currentUserId, int idOfFavoredReviewer) throws SQLException {
        String checkQuery = String.format(
            "SELECT * FROM %s WHERE userId = ? AND favoredReviewerId = ?", 
            FavoredReviewer.tableName
        );
        String insertQuery = String.format(
            "INSERT INTO %s (userId, favoredReviewerId) VALUES (?, ?)", 
            FavoredReviewer.tableName
        );
        String deleteQuery = String.format(
            "DELETE FROM %s WHERE userId = ? AND favoredReviewerId = ?", 
            FavoredReviewer.tableName
        );

        try (Connection connection = generalDBManager.getConnection()) {
            // Check if the relationship already exists
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, currentUserId);
                checkStmt.setInt(2, idOfFavoredReviewer);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        // Relationship exists, so we remove the row
                        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                            deleteStmt.setInt(1, currentUserId);
                            deleteStmt.setInt(2, idOfFavoredReviewer);
                            deleteStmt.executeUpdate();
                        }
                    } else {
                        // Relationship doesn’t exist, so we add the row
                        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                            insertStmt.setInt(1, currentUserId);
                            insertStmt.setInt(2, idOfFavoredReviewer);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
        }
    }

	@Override
	public boolean checkIfFavoredReviewerExists(int currentUserId, int idOfFavoredReviewer) throws SQLException {
		String checkQuery = String.format(
			    "SELECT 1 FROM %s WHERE userId = ? AND favoredReviewerId = ?", 
			    FavoredReviewer.tableName
			);

			try (Connection connection = generalDBManager.getConnection();
			     PreparedStatement pstmt = connection.prepareStatement(checkQuery)) {
			    pstmt.setInt(1, currentUserId);
			    pstmt.setInt(2, idOfFavoredReviewer);
			    try (ResultSet rs = pstmt.executeQuery()) {
			        if (rs.next()) {
			            // Row exists
			            return true;
			        }
			    }
			} catch (SQLException e) {
			    e.printStackTrace();
			}
			return false;

	}
	
	@Override
	public List<Integer> getFavoredReviewerIdsForUser(int userId) throws SQLException {
	    String query = "SELECT favoredReviewerId FROM " + FavoredReviewer.tableName + " WHERE userId = ?";
	    List<Integer> reviewerIds = new ArrayList<>();

	    try (Connection conn = generalDBManager.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setInt(1, userId);

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                reviewerIds.add(rs.getInt("favoredReviewerId"));
	            }
	        }
	    }

	    return reviewerIds;
	}
        
}