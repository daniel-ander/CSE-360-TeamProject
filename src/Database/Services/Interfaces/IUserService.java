package Database.Services.Interfaces;

import Models.User;

import java.util.List;

import Exceptions.DBExceptions.DataAccessException;
import Exceptions.DBExceptions.DuplicateRecordIDException;


public interface IUserService {

	public void register(User user) throws DataAccessException, DuplicateRecordIDException;
	public void printUsersTable() throws DataAccessException;
	public void updateUserRole(String userName, User.Role newRole) throws DataAccessException;
	public void toggleIsFavoredReviewer(int currentUserId, int idOfFavoredReviewer) throws DataAccessException;
	
	// add a role to an existing user without removing existing roles (roles stored as comma-separated string)
	public void addRoleToUser(String userName, User.Role roleToAdd) throws DataAccessException;
	
	public boolean performLogin(User user) throws DataAccessException;
	public boolean doesUserExistByName(String userName) throws DataAccessException;
	public boolean deleteUser(String currentUserName, String targetUserName) throws DataAccessException;
	public boolean checkIfFavoredReviewerExists(int currentUserId, int idOfFavoredReviewer) throws DataAccessException;
	
	public User.Role getUserRole(String userName) throws DataAccessException;
	
	public List<String> getAllUsernames() throws DataAccessException;
	public User getUserByNameAndPassword(String userName, String password) throws DataAccessException;
	public int getUserIdByName(String userName) throws DataAccessException;
	// return the username for a given user id
	public String getUserNameById(int id) throws DataAccessException;
	
	// update user's roles given a comma-separated role string (e.g. "student,reviewer")
	public void updateUserRoles(String userName, String roleString) throws DataAccessException;
	// return the raw comma-separated roles string from the users table (e.g. "student,reviewer")
	public String getUserRolesString(String userName) throws DataAccessException;
		
	public String updatePasswordForUser(String userName) throws DataAccessException;
	public List<String> getUsersSortedByRecentInteraction(String userName) throws DataAccessException;

}