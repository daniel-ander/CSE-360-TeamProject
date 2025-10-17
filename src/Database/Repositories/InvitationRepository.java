package Database.Repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Database.IGeneralDBManager;
import Database.Repositories.Interfaces.IInvitationRepository;
import Models.Invitation;
import Utils.Debug;

public class InvitationRepository implements IInvitationRepository {
	
	private final IGeneralDBManager generalDBManager;
	
	public InvitationRepository(IGeneralDBManager generalDBManager) {
		this.generalDBManager = generalDBManager;
	}
	
	// ===================
    // 		 CREATE 
 	// ===================
	
	@Override
	// Generates a new invitation code and inserts it into the database.
	public boolean insertInvitationCode(String code) throws SQLException {
		boolean generateInvitationCodeDebug = true;

	    String query = String.format("INSERT INTO %s (code) VALUES (?)", Invitation.tableName);
	    
	    try (Connection connection = generalDBManager.getConnection();
	    		PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } 
	    if (generateInvitationCodeDebug) System.out.println("Generated invitation code: " + code);
	    
	    return true;
	}
	
	// ===================
    // 		 READ 
 	// ===================
	
	@Override
	public String getRoleFromInviteCode(String code) throws SQLException {
		// TODO: - Something definitely missing here
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
	public boolean validateInvitationCode(String code) throws SQLException {
	    String query = String.format("SELECT * FROM %s WHERE code = ? AND isUsed = FALSE", Invitation.tableName);
	    
	    try (Connection connection = generalDBManager.getConnection();
	    		PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        if (Debug.isEnabled && "0000".equals(code)) {
		    	return true;
		    }
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    }
	    return false;
	}
	
	// ===================
    // 		 UPDATE 
 	// ===================
	
	@Override
	// Marks the invitation code as used in the database.
	public void markInvitationCodeAsUsed(String code) throws SQLException {
	    String query = String.format("UPDATE %s SET isUsed = TRUE WHERE code = ?", Invitation.tableName);
	    try (Connection connection = generalDBManager.getConnection();
	    		PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    }
	}
	
	// ===================
    // 		 DESTROY 
 	// ===================
	
	// ===================
    // 		 OTHER 
 	// ===================

	@Override
	public void printInvitationCodesTable() throws SQLException {
	    String query = String.format("SELECT code, isUsed FROM %s", Invitation.tableName);
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

	    }
	}
}