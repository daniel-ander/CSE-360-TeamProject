package Sessions;

import Models.User;

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
    
    public boolean getLoggedStatus() {
    	return isLoggedIn;
    }
    
    // ===================
  	// Setters
  	// ===================
    
    public void setLoggedStatus(boolean isLoggedIn) {
    	this.isLoggedIn = isLoggedIn;
    }
    
}