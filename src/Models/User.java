package Models;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User {
    private String userName;
    private String password;
    private int id;
    private Role role;
    
    public static enum Role {
    	admin,
    	student,
    	instructor,
    	staff,
    	reviewer
    }

    // Constructor to initialize a new User object with userName, password, and role.
    public User(String userName, String password, Role role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }
    
    // ===================
    // 		  GETTERS 
 	// ===================
    
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public int getId() { return id; }
    public Role getRole() { return role; }
    
    // ===================
    // 		  SETTERS 
 	// ===================
    
    // Sets the role of the user.
    public void setRole(Role role) {
    	this.role = role;
    }
    
    public void setId(int newId) { this.id = newId; }
    
    public static Role roleFromString(String value) {    	

    	for(Role role : Role.values()) {
    		if(role.name().equalsIgnoreCase(value))
    			return role;
    	}
    	
    	// NOTE/BUG: - This may cause an error if more than one role is selected.
    	// We need to decide on how we'll handle the roles, but a check box is
    	// not appropriate when only a single role can be had.
    	
    	throw new IllegalArgumentException("Invalid role: " + value);
    }
    
    public void changeRole(Role newRole) {
    	this.role = newRole;
    }
}
