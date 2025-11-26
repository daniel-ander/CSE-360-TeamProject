package Database.Repositories;

import Database.Repositories.Interfaces.IAdminRequestRepository;
import Database.IGeneralDBManager;
import Models.AdminRequest;

import java.util.ArrayList;
import java.util.List;

import java.sql.SQLException;
import java.sql.Types;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Simple repository for admin requests.
 * Constructor accepts IGeneralDBManager.
 */
public class AdminRequestRepository implements IAdminRequestRepository {

    private final IGeneralDBManager dbManager;

    public AdminRequestRepository(IGeneralDBManager dbManager) {
        this.dbManager = dbManager;
    }
    
    @Override
    public synchronized List<AdminRequest> getAllRequests() {
    	String sql = "SELECT id, description, is_closed, original_request_id FROM admin_requests";
    	List<AdminRequest> list = new ArrayList<>();
    	
    	try (Connection conn = dbManager.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery()) {
    		
    		while (rs.next()) {
    			list.add(map(rs));
    		}
    		
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return list;
    }
    
    @Override
    public AdminRequest getById(int id) {
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

    @Override
    public synchronized void insert(AdminRequest request) {
        String sql = "INSERT INTO admin_requests (description, is_closed, original_request_id) VALUES (?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, request.getDescription());
            stmt.setBoolean(2, request.isClosed());
            if (request.getOriginalRequestId() != null) {
            	stmt.setInt(3, request.getOriginalRequestId());
            } else {
            	stmt.setNull(3, Types.INTEGER);
            }
            
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void update(AdminRequest request) {
        String sql = "UPDATE admin_requests SET description = ?, is_closed = ?, original_request_id = ? WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, request.getDescription());
            stmt.setBoolean(2, request.isClosed());
            if (request.getOriginalRequestId() != null) {
            	stmt.setInt(3, request.getOriginalRequestId());
            } else {
            	stmt.setNull(3, Types.INTEGER);
            }
            stmt.setInt(4, request.getId());
            
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private AdminRequest map(ResultSet rs) throws SQLException {
        return new AdminRequest(
                rs.getInt("id"),
                rs.getString("description"),
                rs.getBoolean("is_closed"),
                rs.getObject("original_request_id") != null ? rs.getInt("original_request_id") : null
        );
    }
}
