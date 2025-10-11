package Database.Repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import Database.IGeneralDBManager;
import Database.Repositories.Interfaces.IPermissionRepository;


public class PermissionRepository implements IPermissionRepository {
	private final IGeneralDBManager generalDBManager;
	
	public PermissionRepository(IGeneralDBManager generalDBManager) {
		this.generalDBManager = generalDBManager;
	}
	
	// Get permissions for a post(question/answer) in relation to a user
	public Map<Integer, List<String>> getEffectivePermissions(int userId, List<Integer> postIds, boolean isQuestion) throws SQLException {
		// TODO: - This will be the actual query to get permissions
		
//	    if (postIds == null || postIds.isEmpty()) return Collections.emptyMap();
//	    String type = isQuestion ? "question" : "answer";
//	    
//	    String placeholders = postIds.stream().map(id -> "?").collect(Collectors.joining(", "));
//
//	    String query = String.format("""
//	        SELECT DISTINCT final_permissions.post_id, final_permissions.permission
//	        FROM (
//	            -- User-specific overrides (explicitly allowed)
//	            SELECT upp.post_id, upp.permission
//	            FROM user_%s_permissions upp
//	            WHERE upp.user_id = ?
//	              AND upp.post_id IN (%s)
//	              AND upp.is_allowed = TRUE
//
//	            UNION
//
//	            -- Author override: grant full access if user is the author
//	            SELECT fq.id AS post_id, ap.permission
//	            FROM %ss fq
//	            JOIN (
//	                SELECT 'VIEW' AS permission
//	                UNION ALL SELECT 'COMMENT'
//	                UNION ALL SELECT 'EDIT'
//	                UNION ALL SELECT 'DELETE'
//	            ) ap ON 1=1
//	            WHERE fq.userId = ?
//	              AND fq.id IN (%s)
//
//	            UNION
//
//	            -- Role-based defaults if NOT overridden or author
//	            SELECT fq.id AS post_id, rpp.permission
//	            FROM users u
//	            JOIN %s_permissions rpp ON u.role = rpp.role
//	            JOIN %ss fq ON fq.id IN (%s)
//	            WHERE u.id = ?
//	              AND NOT EXISTS (
//	                  SELECT 1
//	                  FROM user_%s_permissions upp
//	                  WHERE upp.user_id = u.id
//	                    AND upp.post_id = fq.id
//	                    AND upp.permission = rpp.permission
//	                    AND upp.is_allowed = TRUE
//	              )
//	              AND fq.userId <> u.id
//	        ) final_permissions
//	        ORDER BY post_id;
//	        """, type, placeholders, placeholders, type, placeholders, type);
//
//	    Map<Integer, List<String>> permissionMap = new HashMap<>();
//
//	    try (Connection connection = generalDBManager.getConnection();
//	         PreparedStatement pstmt = connection.prepareStatement(query)) {
//
//	        int paramIndex = 1;
//
//	        // post_list values
//	        for (Integer postId : postIds) {
//	            pstmt.setInt(paramIndex++, postId);
//	        }
//
//	        // user-specific
//	        pstmt.setInt(paramIndex++, userId);
//	        for (Integer postId : postIds) {
//	            pstmt.setInt(paramIndex++, postId);
//	        }
//
//	        // author
//	        pstmt.setInt(paramIndex++, userId);
//	        for (Integer postId : postIds) {
//	            pstmt.setInt(paramIndex++, postId);
//	        }
//
//	        // role-based
//	        pstmt.setInt(paramIndex++, userId);
//	        for (Integer postId : postIds) {
//	            pstmt.setInt(paramIndex++, postId);
//	        }
//
//	        ResultSet rs = pstmt.executeQuery();
//
//	        while (rs.next()) {
//	            int postId = rs.getInt("post_id");
//	            String permission = rs.getString("permission");
//	            permissionMap.computeIfAbsent(postId, k -> new ArrayList<>()).add(permission);
//	        }
//	    }
//
//	    return permissionMap;
	    return new HashMap<>();
	}

	// Get permissions for a post(question/answer) in relation to a user
    public List<String> getEffectivePermissions(int userId, int postId, boolean isQuestion) throws SQLException {
    	// TODO:- This is an inefficient way of doing this. The query above, once figured out, is the right approach
		String type = isQuestion ? "question" : "answer";
		String query = String.format("""
			SELECT DISTINCT permission
				FROM (
				    -- User-specific overrides (explicitly allowed)
				    SELECT upp.permission
				    FROM user_%s_permissions upp
				    WHERE upp.user_id = ?
				      AND upp.post_id = ?
				      AND upp.is_allowed = TRUE

				    UNION

				    -- Author override: grant full access if user is the author
				    SELECT permission FROM (
						SELECT 'VIEW' AS permission
						UNION ALL SELECT 'COMMENT'
						UNION ALL SELECT 'EDIT'
						UNION ALL SELECT 'DELETE'
					) AS author_perms
					WHERE EXISTS (
						SELECT 1	
						FROM %ss fq
						WHERE fq.id = ?
							AND fq.userId = ?
						)

				    UNION

				    -- Role-based defaults if NOT overridden or author
				    SELECT rpp.permission
				    FROM users u
				    JOIN %s_permissions rpp ON u.role = rpp.role
				    WHERE u.id = ?
				      AND NOT EXISTS (
				          SELECT 1
				          FROM user_%s_permissions upp
				          WHERE upp.user_id = u.id
				            AND upp.post_id = ?
				            AND upp.permission = rpp.permission
				            AND upp.is_allowed = FALSE
				      )
				      AND NOT EXISTS (
				          SELECT 1
				          FROM %ss fq
				          WHERE fq.id = ?
				            AND fq.userId = u.id
				      )
				) AS final_permissions;
			""", type, type, type, type, type);
		
		try (Connection connection = generalDBManager.getConnection();
	    		PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, userId); 
			pstmt.setInt(2, postId);
			pstmt.setInt(3, postId); 
			pstmt.setInt(4, userId);
			pstmt.setInt(5, userId); 
			pstmt.setInt(6, postId);
			pstmt.setInt(7, postId);

			ResultSet rs = pstmt.executeQuery();
			List<String> permissions = new ArrayList<>();

			while (rs.next()) {
				permissions.add(rs.getString("permission"));
			}

			return permissions;
		}
    }

    
    // Adding or removing custom permissions between a user and post(question/answer)
    public void togglePermissionException(int userId, int postId, String permission, boolean isQuestion) throws SQLException {
        String type = isQuestion ? "question" : "answer";

        String query = String.format("SELECT 1 FROM user_%s_permissions WHERE user_id = ? AND post_id = ? AND permission = ?", type);

        try (Connection connection = generalDBManager.getConnection();
	    		PreparedStatement pstmt = connection.prepareStatement(query)) {
        	pstmt.setInt(1, userId);
        	pstmt.setInt(2, postId);
        	pstmt.setString(3, permission);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String deleteSql = String.format("DELETE FROM user_%s_permissions WHERE user_id = ? AND post_id = ? AND permission = ?", type);
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, userId);
                    deleteStmt.setInt(2, postId);
                    deleteStmt.setString(3, permission);
                    deleteStmt.executeUpdate();
                    System.out.println("Removed permission exception for " + permission);
                }
            } else {
                String insertSql = String.format("INSERT INTO user_%s_permissions (user_id, post_id, permission, is_allowed) VALUES (?, ?, ?, ?)", type);
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, postId);
                    insertStmt.setString(3, permission);
                    insertStmt.setBoolean(4, false); // insert false here to block
                    insertStmt.executeUpdate();
                    System.out.println("Added blocking exception for " + permission);
                }
            }
        }
    }
    
	public boolean isPermissionTableEmpty(Connection conn) throws SQLException {
		String query = "SELECT COUNT(*) FROM question_permissions";
		try (Connection connection = generalDBManager.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery(query);
			if (rs.next()) {
				return rs.getInt(1) == 0;
			}
		}
		return true;
	}
    

    
    // Set default permissions for each type of user in relation to a regular post(question/answer)
    public void seedRolePermissions() throws SQLException {
		Map<String, List<String>> questionRolePermissions = new HashMap<>();

		questionRolePermissions.put("admin", Arrays.asList("VIEW", "COMMENT", "DELETE", "APPROVE", "UPVOTE"));
		questionRolePermissions.put("instructor", Arrays.asList("VIEW", "COMMENT", "EDIT", "UPVOTE"));
		questionRolePermissions.put("student", Arrays.asList("VIEW", "COMMENT", "UPVOTE"));
		questionRolePermissions.put("staff", Arrays.asList("VIEW", "EDIT", "UPVOTE"));
		questionRolePermissions.put("reviewer", Arrays.asList("VIEW", "COMMENT", "APPROVE", "UPVOTE"));

		String query = "MERGE INTO question_permissions (role, permission) VALUES (?, ?)";

		try (Connection connection = generalDBManager.getConnection();
	    		PreparedStatement pstmt = connection.prepareStatement(query)) {
			for (Map.Entry<String, List<String>> entry : questionRolePermissions.entrySet()) {
				String role = entry.getKey();
				for (String permission : entry.getValue()) {
					pstmt.setString(1, role);
					pstmt.setString(2, permission);
					pstmt.addBatch(); 
				}
			}
			pstmt.executeBatch(); 
			System.out.println("Role-permission mappings seeded successfully.");
		}
		
		Map<String, List<String>> answerRolePermissions = new HashMap<>();

		answerRolePermissions.put("admin", Arrays.asList("VIEW", "COMMENT", "DELETE", "UPVOTE"));
		answerRolePermissions.put("instructor", Arrays.asList("VIEW", "COMMENT", "EDIT", "UPVOTE"));
		answerRolePermissions.put("student", Arrays.asList("VIEW", "COMMENT", "UPVOTE"));
		answerRolePermissions.put("staff", Arrays.asList("VIEW", "EDIT", "UPVOTE"));
		answerRolePermissions.put("reviewer", Arrays.asList("VIEW", "COMMENT", "UPVOTE"));

		query = "MERGE INTO answer_permissions (role, permission) VALUES (?, ?)";

		try (Connection connection = generalDBManager.getConnection();
	    		PreparedStatement pstmt = connection.prepareStatement(query)) {
			for (Map.Entry<String, List<String>> entry : answerRolePermissions.entrySet()) {
				String role = entry.getKey();
				for (String permission : entry.getValue()) {
					pstmt.setString(1, role);
					pstmt.setString(2, permission);
					pstmt.addBatch(); 
				}
			}
			pstmt.executeBatch(); 
			System.out.println("Role-permission mappings seeded successfully.");
		}
	}
}