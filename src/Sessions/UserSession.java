package Sessions;

import Models.User;
import Models.DBModels.UserDTO;

public class UserSession {
	private final User user;
    private boolean isLoggedIn;
    
    public UserSession(User user) {
    	this.user = user;
    }
    
    // ===================
 	// Getters
 	// ===================
    
    public User currentUser() {
    	return user;
    }
    
    public String getCurrentUserName() {
    	return user.getUserName();
    }
    
    public User.Role getCurrentUserRole() {
    	return user.getRole();
    }
    
    public int getCurrentUserId() {
		return user.getId();
	}
    
    public boolean getLoggedStatus() {
    	return isLoggedIn;
    }
    
    // ===================
  	// Setters
  	// ===================
    
    public void setLoggedStatus(boolean isLoggedIn) {
    	this.isLoggedIn = isLoggedIn;
    }
    
    public UserDTO getDTOModel() {
    	return new UserDTO(user.getId(), user.getUserName(), user.getRole());
    }
    
}