package Models.Interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface ISharedUser {
	
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
	
	public String getUserName();
    public int getId();
    public Role getRole();
    
    // ===================
    // 		  SETTERS 
    // ===================
    
    // Sets the role of the user.
    public void setRole(Role role);
    
    public void setId(int newId);
    
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
}