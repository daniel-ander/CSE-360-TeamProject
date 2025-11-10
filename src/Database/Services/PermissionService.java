package Database.Services;

import java.sql.SQLException;
import java.util.List;

import Database.Repositories.Interfaces.IPermissionRepository;
import Database.Services.Interfaces.IPermissionService;
import Exceptions.DBExceptions.DataAccessException;
import Models.IPost;


public class PermissionService implements IPermissionService {
    private final IPermissionRepository permissionRepository;

    public PermissionService(IPermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }
    
    /** We get the list of permissions a user has in relation to a post. IE: ["CREATE", "UPVOTE", "EDIT"] */
    public List<String> getEffectivePermissions(int userId, int postId, IPost.Type type) throws DataAccessException {
    	try {
			return permissionRepository.getEffectivePermissions(userId, postId, type == IPost.Type.Question);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to get effective permissions for userId: " + userId + " postId: " + postId + " due to: " + e.getMessage());
		}
    }

    /** Adding or removing custom permissions between a user and post(question/answer) */
    public void togglePermissionException(int userId, int postId, String permission, boolean isQuestion) throws DataAccessException {
    	try {
    		permissionRepository.togglePermissionException(userId, postId, permission, isQuestion);
    	} catch (SQLException e) {
    		throw new DataAccessException("Failed to generate an invitation code in the database due to: " + e.getMessage());
    	}
    }
    
    /** We seed the table with relevant permissions */
    public void seedRolePermissions() throws DataAccessException {
    	try {
    		permissionRepository.seedRolePermissions();
    	} catch (SQLException e) {
    		System.out.println("Could not seed role permissions due to: " + e.getMessage());
    	}
    }

}