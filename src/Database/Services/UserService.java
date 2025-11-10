package Database.Services;

import Models.ReviewerRequest;
import Models.User;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import Exceptions.DBExceptions.*;

import Database.Repositories.Interfaces.IUserRepository;
import Database.Repositories.Interfaces.IReviewerRequestRepository;
import Database.Services.Interfaces.IUserService;


public class UserService implements IUserService {

	private final IUserRepository userRepository;
    private final IReviewerRequestRepository reviewerRequestRepository;
    
    public UserService(IUserRepository userRepository, IReviewerRequestRepository reviewerRequestRepository) {
        this.userRepository = userRepository;
        this.reviewerRequestRepository = reviewerRequestRepository;
    }
	
    public UserService(IUserRepository userRepository) {
        this(userRepository, null);
    }

	// ===================
    // 		 CREATE 
 	// ===================
	
	@Override
	
	public void register(User user) throws DataAccessException, DuplicateRecordIDException {
		if(doesUserExistByName(user.getUserName())) {
			throw new DuplicateRecordIDException("A user by this user name already exists.");
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
	@Override
	public User getUserByNameAndPassword(String userName, String password) throws DataAccessException {
		try {
			return userRepository.getUserByNameAndPassword(userName, password);
			
		} catch (SQLException e) {
			throw new DataAccessException("Could not log in due to: " + e);
		}
	}
	@Override
	public int getUserIdByName(String userName) throws DataAccessException {
		try {
			return userRepository.getUserIdByName(userName);
			
		} catch (SQLException e) {
			throw new DataAccessException("Could not get user ID by user name: " + userName + " due to: " + e);
		}
	}
	
	public List<String> getUsersSortedByRecentInteraction(String userName) throws DataAccessException {
		try {
			return userRepository.getUsersSortedByRecentInteraction(userName);
			
		} catch (SQLException e) {
			throw new DataAccessException("Could not get users sorted by recent interaction due to: " + e);
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
	
	// Update user's roles using a comma-separated string (e.g., "student,reviewer")
    @Override
    public void updateUserRoles(String userName, String roleString) throws DataAccessException {
        try {
            if (roleString == null) roleString = "";
            userRepository.updateUserRole(userName, roleString);
        } catch (SQLException e) {
            throw new DataAccessException("Could not update user roles due to: " + e);
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
	
	// add a role to an existing user without removing existing roles
    public void addRoleToUser(String userName, User.Role roleToAdd) throws DataAccessException {
        try {
            String currentRoles = userRepository.getUserRole(userName);
            if (currentRoles == null) {
                throw new DataAccessException("User does not exist: " + userName);
            }

            // normalize and check if role already present (roles stored as comma-separated list)
            String[] parts = currentRoles.split(",");
            for (String p : parts) {
                if (p.trim().equalsIgnoreCase(roleToAdd.name())) {
                    // already has the role
                    return;
                }
            }

            String newRoles;
            if (currentRoles.trim().isEmpty()) {
                newRoles = roleToAdd.name();
            } else {
                newRoles = currentRoles + "," + roleToAdd.name();
            }

            userRepository.updateUserRole(userName, newRoles);
        } catch (SQLException e) {
            throw new DataAccessException("Could not add role to user due to: " + e);
        }
    }
    
 // reviewer request related methods
    public void submitReviewerRequest(int studentId, int instructorId) throws DataAccessException {
        if (reviewerRequestRepository == null) {
            throw new DataAccessException("Reviewer request repository not available.");
        }
        try { // create and submit the reviewer request
            ReviewerRequest request = new ReviewerRequest(studentId, instructorId);
            reviewerRequestRepository.submitRequest(request);
            System.out.println("Student " + studentId + " has submitted a request to become a reviewer.");
        } catch (SQLException e) {
            throw new DataAccessException("Could not submit reviewer request due to: " + e);
        }
    }
    
    // for instructor to view all pending reviewer requests
    public List<ReviewerRequest> viewPendingReviewerRequests(int instructorId) throws DataAccessException {
        if (reviewerRequestRepository == null) {
            throw new DataAccessException("Reviewer request repository not available.");
        }
        try {
            return reviewerRequestRepository.getPendingRequestsByInstructor(instructorId);
        } catch (SQLException e) {
            throw new DataAccessException("Could not fetch pending requests for instructor " + instructorId + " due to: " + e);
        }
    }
    
    // instructor processes a reviewer request to approve/deny
    public void processReviewerRequest(int requestId, String decision) throws DataAccessException {
        if (reviewerRequestRepository == null) {
            throw new DataAccessException("Reviewer request repository not available.");
        }
        try {
            // updates request status
            reviewerRequestRepository.updateRequestStatus(requestId, decision);
            
            if ("APPROVED".equalsIgnoreCase(decision)) {
                // give the reviewer role to the student if approved
                ReviewerRequest request = reviewerRequestRepository.getRequestById(requestId);
                int studentId = request.getStudentId();
                // add role without replacing existing roles
                User user = userRepository.getUserById(studentId);
                if (user != null) {
                    addRoleToUser(user.getUserName(), User.Role.reviewer);
                } else {
                    throw new DataAccessException("User not found with ID: " + studentId);
                }
            }
            System.out.println("Request " + requestId + " processed with decision: " + decision);
        } catch (SQLException e) {
            throw new DataAccessException("Could not process reviewer request due to: " + e);
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
    public String getUserNameById(int id) throws DataAccessException {
        try {
            Models.User user = userRepository.getUserById(id);
            if (user == null) return null;
            return user.getUserName();
        } catch (SQLException e) {
            throw new DataAccessException("Could not get username by id due to: " + e);
        }
    }

    @Override
    public String getUserRolesString(String userName) throws DataAccessException {
        try {
            return userRepository.getUserRole(userName);
        } catch (SQLException e) {
            throw new DataAccessException("Could not get user roles string due to: " + e);
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
	
	public void toggleIsFavoredReviewer(int currentUserId, int idOfFavoredReviewer) throws DataAccessException {
		try {
			userRepository.toggleIsFavoredReviewer(currentUserId, idOfFavoredReviewer);
		} catch(SQLException e) {
			throw new DataAccessException("Could not add/remove reviewer from favorites due to: " + e);
		}
	}

	@Override
	public boolean checkIfFavoredReviewerExists(int currentUserId, int idOfFavoredReviewer) throws DataAccessException {
		try {
			return userRepository.checkIfFavoredReviewerExists(currentUserId, idOfFavoredReviewer);
		} catch(SQLException e) {
			throw new DataAccessException("Could not fetch favored reviewer status due to: " + e);
		}
	}
	
	@Override
	public List<Integer> getFavoredReviewerIdsForUser(int userId) throws DataAccessException {
		try {
			return userRepository.getFavoredReviewerIdsForUser(userId);
		} catch(SQLException e) {
			throw new DataAccessException("Could not fetch favored reviewers due to: " + e);
		}
	}
	

}