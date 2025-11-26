package Database.Repositories;

import Database.Repositories.Interfaces.IAdminRequestRepository;
import Database.IGeneralDBManager;
import Models.AdminRequest;

import java.util.ArrayList;
import java.util.List;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Simple repository for admin requests.
 * Constructor accepts IGeneralDBManager.
 */
public class AdminRequestRepository implements IAdminRequestRepository {

    private final IGeneralDBManager dbManager; // reserved for future DB operations

    public AdminRequestRepository(IGeneralDBManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public synchronized List<AdminRequest> getOpenRequests() {
        String sql = "SELECT id, description, is_closed, original_request_id FROM admin_requests WHERE is_closed = FALSE";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<AdminRequest> list = new ArrayList<>();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public synchronized List<AdminRequest> getClosedRequests() {
        String sql = "SELECT id, description, is_closed, original_request_id FROM admin_requests WHERE is_closed = TRUE";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

               List<AdminRequest> list = new ArrayList<>();
               while (rs.next()) {
                   list.add(map(rs));
               }
               return list;

           } catch (SQLException e) {
               e.printStackTrace();
               return List.of();
           }
    }

    @Override
    public synchronized void createRequest(String description) {
        String sql = "INSERT INTO admin_requests (description, is_closed) VALUES (?, FALSE)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, description);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void closeRequest(int id) {
        String sql = "UPDATE admin_requests SET is_closed = TRUE WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void reopen(int id, String newDescription) {
        String sql = "INSERT INTO admin_requests (description, is_closed) VALUES (?, FALSE)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newDescription);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public AdminRequest find(int id) {
	    String sql = "SELECT id, description, is_closed, original_request_id FROM admin_requests WHERE id = ?";
	
	    try (Connection conn = dbManager.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	
	        stmt.setInt(1, id);
	
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	            	return map(rs);
	            }
	        }
	
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	
	    return null;
    }
    
    private AdminRequest map(ResultSet rs) throws SQLException {
        Integer originalId = null;
        Object obj = rs.getObject("original_request_id");
        if (obj != null) originalId = rs.getInt("original_request_id");

        return new AdminRequest(
                rs.getInt("id"),
                rs.getString("description"),
                rs.getBoolean("is_closed"),
                originalId
        );
    }
}
