package Models;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import Utils.RolePermissions.QuestionPermissions;

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
    
    /**
     * Parse a role string and return a single Role.
     * If the string contains multiple comma-separated roles, return the first matching role.
     */
    public static Role roleFromString(String value) {

    	if (value == null) throw new IllegalArgumentException("Invalid role: null");
    	String v = value.trim();
    	if (v.isEmpty()) throw new IllegalArgumentException("Invalid role: " + value);

    	// If multiple roles are present (comma-separated) try each and return first match
    	if (v.contains(",")) {
    		String[] parts = v.split(",");
    		for (String p : parts) {
    			String t = p.trim();
    			for (Role role : Role.values()) {
    				if (role.name().equalsIgnoreCase(t)) return role;
    			}
    		}
    	} else {
    		for (Role role : Role.values()) {
    			if (role.name().equalsIgnoreCase(v)) return role;
    		}
    	}
    	
    	throw new IllegalArgumentException("Invalid role: " + value);
    }

    /**
     * Parse comma-separated roles and return a list of Role enums (may be empty).
     */
    public static List<Role> rolesFromString(String value) {
        List<Role> out = new ArrayList<>();
        if (value == null || value.trim().isEmpty()) return out;
        String[] parts = value.split(",");
        for (String p : parts) {
            String t = p.trim();
            for (Role role : Role.values()) {
                if (role.name().equalsIgnoreCase(t)) {
                    out.add(role);
                    break;
                }
            }
        }
        return out;
    }
    
    public void changeRole(Role newRole) {
    	this.role = newRole;
    }
}