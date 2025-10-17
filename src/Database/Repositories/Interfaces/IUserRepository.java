package Database.Repositories.Interfaces;

import java.sql.SQLException;
import java.util.List;

import Models.User;

// Interface added for the purposes of easier testing

public interface IUserRepository {
	
	public List<String> getAllUsernames() throws SQLException;
	
	public int insertNewUser(User user) throws SQLException;
	public void updateUserRole(String userName, String roleString) throws SQLException;
	public void printAllRows() throws SQLException;

	public boolean doesUserExist(User user) throws SQLException;
	public boolean doesUserExistByName(String userName) throws SQLException;
	public boolean deleteUser(String targetUser) throws SQLException;

	public String getUserRole(String userName) throws SQLException;
	public User getUserByNameAndPassword(String userName, String password) throws SQLException;
	public User getUserByName(String userName) throws SQLException;
	public String updatePasswordForUser(String userName, String newPassword) throws SQLException;

	public int getUserIdByName(String userName) throws SQLException;
}