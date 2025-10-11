package Database.Repositories.Interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IPermissionRepository {
	public List<String> getEffectivePermissions(int userId, int postId, boolean isQuestion) throws SQLException;
	public Map<Integer, List<String>> getEffectivePermissions(int userId, List<Integer> postIds, boolean isQuestion) throws SQLException;

    public void togglePermissionException(int userId, int postId, String permission, boolean isQuestion) throws SQLException;
    public void seedRolePermissions() throws SQLException;
}