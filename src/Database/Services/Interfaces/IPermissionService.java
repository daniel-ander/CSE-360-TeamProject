package Database.Services.Interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import Database.Services.Interfaces.IPermissionService;
import Exceptions.DBExceptions.DataAccessException;


public interface IPermissionService {
    
    // We get the list of permissions a user has in relation to a post. IE: ["CREATE", "UPVOTE", "EDIT"]
	public Map<Integer, List<String>> getEffectivePermissions(int userId, List<Integer> postIds, boolean isQuestion) throws DataAccessException;
    public List<String> getEffectivePermissions(int userId, int postId, boolean isQuestion) throws DataAccessException;

    // Adding or removing custom permissions between a user and post(question/answer)
    public void togglePermissionException(int userId, int postId, String permission, boolean isQuestion) throws DataAccessException;
    
    // We seed the table with relevant permissions
    public void seedRolePermissions() throws DataAccessException;
}