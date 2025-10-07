package Database.Services;

import Models.User;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import Exceptions.UserDBExceptions.*;

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
	
	public void register(User user) throws SQLException, DuplicateUserException {
		if(doesUserExistByName(user.getUserName())) {
			throw new DuplicateUserException("A user with that user name already exists.");
		} else {
			int newUserId = userRepository.insertNewUser(user);
			
		}
	}
	
	// ===================
    // 		 READ 
 	// ===================

	// Validates a user's login credentials.
	@Override
	public boolean performLogin(User user) throws SQLException {
		return userRepository.doesUserExist(user);
	}

	// Checks if a user already exists in the database based on their userName.
	@Override
	public boolean doesUserExistByName(String userName) throws SQLException {
		return userRepository.doesUserExistByName(userName);
	}
	
	// ===================
    // 		 UPDATE 
 	// ===================
	
	@Override
	public User.Role getUserRole(String userName) throws SQLException {
		String roleString = userRepository.getUserRole(userName);
		return User.roleFromString(roleString);
	}
	
	@Override
	public void updateUserRole(String userName, User.Role newRole) throws SQLException {
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
		userRepository.updateUserRole(userName, newRole.name());
	}
	
	@Override
	public List<String> getAllUsernames() throws SQLException {
		return userRepository.getAllUsernames();
	}
	
	
	
	@Override
	public String resetPasswordForUser(String userName) throws SQLException {
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
	    
	    String newPass = userRepository.updatePasswordForUser(userName, tempPass);
	    return newPass;
	}
	
	// ===================
    // 		 DELETE 
 	// ===================

	@Override
	public boolean deleteUser(String currentUserName, String targetUserName) throws SQLException {
	    // Prevent an admin from deleting their own account
	    if (currentUserName.equals(targetUserName)) {
	        User.Role currentRole = getUserRole(currentUserName);
	        
	        if (currentRole == User.Role.admin) {
	            System.out.println("Admins cannot delete their own account.");
	            return false;  // Only in this specific case, return false
	        }
	    }
	    
	    // If above check passes, proceed to delete user
	    userRepository.deleteUser(targetUserName);  // May throw SQLException
	    
	    return true;  // If no exception thrown, delete was successful
	}
	
	@Override
	public void printUsersTable() throws SQLException {
		userRepository.printAllRows();
	}
	

}




























