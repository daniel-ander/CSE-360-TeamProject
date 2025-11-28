package Models;

import java.util.Arrays;
import java.util.List;

import Models.Interfaces.ISharedUser;

import java.util.ArrayList;

import Utils.RolePermissions.QuestionPermissions;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User implements ISharedUser {
	public static String tableName = "users";
	
    private String userName;
    private String password;
    private Integer id; //
    private Role role;
 
    public User(String userName, String password, Role role) {
    	this(null, userName, password, role);
    }
    
    public User(Integer id, String userName, String password, Role role) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.role = role;
    }
    
    // ===================
    // 		  GETTERS 
    // ===================
    
    @Override
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    @Override
    public int getId() { return id; }
    @Override
    public Role getRole() { return role; }
    
    // ===================
    // 		  SETTERS 
    // ===================
    
    // Sets the role of the user.
    @Override
    public void setRole(Role role) {
    	this.role = role;
    }
    
    @Override
    public void setId(int newId) { this.id = newId; }

}