package Database.Services.Interfaces;

import Models.User;

import java.sql.SQLException;

import java.util.List;

import Exceptions.UserDBExceptions.*;


public interface IUserService {

	public void register(User user) throws SQLException, DuplicateUserException;
	public void printUsersTable() throws SQLException;
	public void updateUserRole(String userName, User.Role newRole) throws SQLException;
	
	public boolean performLogin(User user) throws SQLException;
	public boolean doesUserExistByName(String userName) throws SQLException;
	public boolean deleteUser(String currentUserName, String targetUserName) throws SQLException;
	
	public User.Role getUserRole(String userName) throws SQLException;
	
	public List<String> getAllUsernames() throws SQLException;
	
	public String resetPasswordForUser(String userName) throws SQLException;

}




























