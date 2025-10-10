package Database.Services.Interfaces;

import java.util.List;

import Database.Services.Interfaces.IPermissionService;
import Exceptions.DBExceptions.DataAccessException;


public interface IPermissionService {
    
    // We get the list of permissions a user has in relation to a post. IE: ["CREATE", "UPVOTE", "EDIT"]
    public List<String> getEffectivePermissions(int userId, int postId, boolean isQuestion) throws DataAccessException;

    // Adding or removing custom permissions between a user and post(question/answer)
    public void togglePermissionException(int userId, int postId, String permission, boolean isQuestion) throws DataAccessException;
    
    // We seed the table with relevant permissions
    public void seedRolePermissions() throws DataAccessException;
}