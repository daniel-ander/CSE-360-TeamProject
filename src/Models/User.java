package Models;

import java.util.Arrays;
import java.util.List;

import Config.DefaultRolePermissions.QuestionPermissions;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User {
	public static String tableName = "users";
	
    private String userName;
    private String password;
    private int id; //
    private Role role;
    
    public static enum Role {
    	admin,
    	student,
    	instructor,
    	staff,
    	reviewer;
    	
    	public static List<String> getAllRoles() {
			return Arrays.stream(Role.values())
					.map(Enum::name)
					.toList();
		}
    }
 
    public User(String userName, String password, Role role) {
    	this.userName = userName;
    	this.password = password;
    	this.role = role;
    	this.id = -1;
    }
    
    public User(int id, String userName, String password, Role role) {
        this.id = id;
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

        if (value == null) {
            throw new IllegalArgumentException("Invalid role: null");
        }

        // Support comma-separated role strings (e.g. "student,reviewer"). Use the first valid role found.
        String[] tokens = value.split(",");
        for (String token : tokens) {
            String trimmed = token.trim();
            for (Role role : Role.values()) {
                if (role.name().equalsIgnoreCase(trimmed))
                    return role;
            }
        }

        throw new IllegalArgumentException("Invalid role: " + value);
    }
    
    public void changeRole(Role newRole) {
    	this.role = newRole;
    }
}
