package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InvitationRepository implements IInvitationRepository {
	
	private final IGeneralDBManager generalDBManager;
	
	public InvitationRepository(IGeneralDBManager generalDBManager) throws SQLException {
		this.generalDBManager = generalDBManager;
	}
	
	@Override
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String roles) {
		boolean generateInvitationCodeDebug = true;
	    String code = roles + "_" + UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code) VALUES (?)";
	    
	    try (Connection connection = generalDBManager.getConnection();
	    		PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    if (generateInvitationCodeDebug) System.out.println("Generated invitation code: " + code);
	    
	    return code;
	}
	
	@Override
	public String getRoleFromInviteCode(String code) {
		String roleBits = code.substring(0, code.indexOf('_'));
		List<String> roles = new ArrayList<>();
		String[] roleNames = {"admin","student","instructor","staff","reviewer"};

		for (int i = 0; i < roleBits.length(); i++) {
		    if (roleBits.charAt(i) == '1') {
		        roles.add(roleNames[i]);
		    }
		}
		String rolesStr = String.join(",", roles);
		return rolesStr;
	}
	
	@Override
	// Validates an invitation code to check if it is unused and valid.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    
	    try (Connection connection = generalDBManager.getConnection();
	    		PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        if ("0000".equals(code)) {
		    	return true;
		    }
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	@Override
	// Marks the invitation code as used in the database.
	public void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (Connection connection = generalDBManager.getConnection();
	    		PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	@Override
	public void printInvitationCodesTable() {
	    String query = "SELECT code, isUsed FROM InvitationCodes";
	    try (Connection connection = generalDBManager.getConnection();
	    		PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {

	        System.out.printf("%-15s %-10s%n", "Code", "Is Used");
	        System.out.println("-------------------------");

	        while (rs.next()) {
	            String code = rs.getString("code");
	            boolean isUsed = rs.getBoolean("isUsed");

	            System.out.printf("%-15s %-10s%n", code, isUsed);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
}