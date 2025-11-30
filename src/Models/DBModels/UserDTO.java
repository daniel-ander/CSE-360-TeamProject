package Models.DBModels;

import Models.Interfaces.ISharedUser;

/**
 * The UserDTO class represents a user entity in the system after being fetched
 * It contains the user's details such as userName and role, but not sensitive information
 */
public class UserDTO implements ISharedUser {
	private int id;
	private String username;
	private Role role;
	
	public UserDTO(int id, String username, Role role) {
		this.id = id;
		this.username = username;
		this.role = role;
	}

	@Override
	public String getUserName() {
		return username;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public Role getRole() {
		return role;
	}

	@Override
	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public void setId(int newId) {
		this.id = newId;
	}
	
}