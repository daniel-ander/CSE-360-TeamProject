package Sessions;

import Models.User;

public class UserSession {
	private User user;
    private String userName;
    private User.Role role;
    boolean isLoggedIn;
    
    public UserSession(User user) {
    	this.user = user;
    	this.userName = user.getUserName();
    	this.role = user.getRole();
    }
    
    // ===================
 	// Getters
 	// ===================
    
    public User currentUser() {
    	return user;
    }
    
    public String getUsername() {
    	return userName;
    }
    
    public User.Role getRole() {
    	return role;
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