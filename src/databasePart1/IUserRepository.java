package databasePart1;

import java.sql.SQLException;
import java.util.List;

import Models.User;

// Interface added for the purposes of easier testing

public interface IUserRepository {
	public boolean deleteUser(String currentUser, String targetUser);
	public boolean doesUserExist(String userName);
	public boolean login(User user) throws SQLException;
	
	public String getUserRole(String userName);
	public String issueResetPassword(String userName);
	
	public List<String> getAllUsernames();
	
	public void register(User user) throws SQLException;
	public void updateUserRoles(String userName, List<String> newRoles);
	public void printUsersTable();
	public void updatePassword(String userName, String newPassword) throws SQLException;
}