package Database.Services;

import Models.User;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import Exceptions.DBExceptions.*;

import Database.Repositories.Interfaces.IUserRepository;
import Database.Services.Interfaces.IUserService;


public class UserService implements IUserService {

	private final IUserRepository userRepository;

	public UserService(IUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// ===================
    // 		 CREATE 
 	// ===================
	
	@Override
	
	public void register(User user) throws DataAccessException, DuplicateRecordIDException {
		if(doesUserExistByName(user.getUserName())) {
			throw new DuplicateRecordIDException("A user with this id already exists.");
		} 
		
		try {
			userRepository.insertNewUser(user);
		} catch (SQLException e) {
			throw new DataAccessException("Could not register user by username: " + user.getUserName() + " from the database due to: " + e);
		}
	}
	
	// ===================
    // 		 READ 
 	// ===================

	// Validates a user's login credentials.
	@Override
	public boolean performLogin(User user) throws DataAccessException {
		try {
			return userRepository.doesUserExist(user);
			
		} catch (SQLException e) {
			throw new DataAccessException("Could not log in due to: " + e);
		}
	}

	// Checks if a user already exists in the database based on their userName.
	@Override
	public boolean doesUserExistByName(String userName) throws DataAccessException {
		try {
			return userRepository.doesUserExistByName(userName);
			
		} catch (SQLException e) {
			throw new DataAccessException("Could not log in due to: " + e);
		}
	}
	
	// ===================
    // 		 UPDATE 
 	// ===================
	
	@Override
	public User.Role getUserRole(String userName) throws DataAccessException {
		try {
			String roleString = userRepository.getUserRole(userName);
			return User.roleFromString(roleString);
			
		} catch (SQLException e) {
			throw new DataAccessException("Could not get user by user name: " + userName + " due to: " + e);
		}
	}
	
	@Override
	public void updateUserRole(String userName, User.Role newRole) throws DataAccessException {
		// Normalize, de-dup, and join
		
		// NOTE: - Logic relevant only if we're doing multiple roles
		
//	    LinkedHashSet<String> norm = new LinkedHashSet<>();
//	    for (String r : newRoles) {
//	        if (r != null) {
//	            String v = r.trim().toLowerCase(Locale.ROOT);
//	            if (!v.isEmpty()) norm.add(v);
//	        }
//	    }
//	    String roleString = String.join(",", norm); // empty string means “no roles”
		try {
			userRepository.updateUserRole(userName, newRole.name());
			
		} catch (SQLException e) {
			throw new DataAccessException("Could not log in due to: " + e);
		}
	}
	
	@Override
	public List<String> getAllUsernames() throws DataAccessException {
		try {
			return userRepository.getAllUsernames();
			
		} catch (SQLException e) {
			throw new DataAccessException("Could not get user names due to: " + e);
		}
	}
	
	
	
	@Override
	public String updatePasswordForUser(String userName) throws DataAccessException {
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
	    
	    try {
	    	String newPass = userRepository.updatePasswordForUser(userName, tempPass);
		    return newPass;
			
		} catch (SQLException e) {
			throw new DataAccessException("Could not get user names due to: " + e);
		}
	}
	
	// ===================
    // 		 DELETE 
 	// ===================

	@Override
	public boolean deleteUser(String currentUserName, String targetUserName) throws DataAccessException {
	    // Prevent an admin from deleting their own account
	    if (currentUserName.equals(targetUserName)) {
	        User.Role currentRole = getUserRole(currentUserName);
	        
	        if (currentRole == User.Role.admin) {
	            System.out.println("Admins cannot delete their own account.");
	            return false;  // Only in this specific case, return false
	        }
	    }
	    
	    // If above check passes, proceed to delete user
	    try {
	    	return userRepository.deleteUser(targetUserName);
			
		} catch (SQLException e) {
			throw new DataAccessException("Could not delete user due to: " + e);
		}
	}
	
	@Override
	public void printUsersTable() throws DataAccessException {
		try {
			userRepository.printAllRows();
			
		} catch (SQLException e) {
			throw new DataAccessException("Could not print user table due to: " + e);
		}
	}
	

}




























