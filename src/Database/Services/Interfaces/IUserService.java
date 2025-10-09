package Database.Services.Interfaces;

import Models.User;

import java.util.List;

import Exceptions.DBExceptions.DataAccessException;
import Exceptions.DBExceptions.DuplicateRecordIDException;


public interface IUserService {

	public void register(User user) throws DataAccessException, DuplicateRecordIDException;
	public void printUsersTable() throws DataAccessException;
	public void updateUserRole(String userName, User.Role newRole) throws DataAccessException;
	
	public boolean performLogin(User user) throws DataAccessException;
	public boolean doesUserExistByName(String userName) throws DataAccessException;
	public boolean deleteUser(String currentUserName, String targetUserName) throws DataAccessException;
	
	public User.Role getUserRole(String userName) throws DataAccessException;
	
	public List<String> getAllUsernames() throws DataAccessException;
	public User getUserByNameAndPassword(String userName, String password) throws DataAccessException;
	public int getUserIdByName(String userName) throws DataAccessException;
	public String updatePasswordForUser(String userName) throws DataAccessException;

}