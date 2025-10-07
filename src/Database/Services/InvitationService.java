package Database.Services;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import Database.Repositories.Interfaces.IInvitationRepository;
import Database.Services.Interfaces.IInvitationService;

import Exceptions.DBExceptions.*;

public class InvitationService implements IInvitationService {
	
	private final IInvitationRepository invitationRepository;
	
	public InvitationService(IInvitationRepository invitationRepository) throws SQLException {
		this.invitationRepository = invitationRepository;
	}
	
	// ===================
    // 		 CREATE 
 	// ===================
	
	@Override
	public String generateInvitationCode(String roles) throws DataAccessException {
		try {
			// NOTE: - Not sure why this is set up this way
			String code = roles + "_" + UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
																					  
			invitationRepository.insertInvitationCode(code);

			return code;
		} catch (SQLException e) {
			throw new DataAccessException("Failed to generate an invitation code in the database due to: " + e);
		}
	}
	
	// ===================
    // 		 READ 
 	// ===================
	
	@Override
	public String getRoleFromInviteCode(String code) throws DataAccessException {
		// TODO :- Write the SQL and implement
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
	public void printInvitationCodesTable() throws DataAccessException {
		try {
			invitationRepository.printInvitationCodesTable();
		} catch (SQLException e) {
			throw new DataAccessException("Could not print invitation codes in the database due to: " + e);
		}
	}
	
	@Override
	public boolean validateInvitationCode(String code) throws DataAccessException {
		try {
			return invitationRepository.validateInvitationCode(code);
		} catch (SQLException e) {
			throw new DataAccessException("Could not validate invitation code in the database due to: " + e);
		}
	}
	
	// ===================
    // 		 UPDATE 
 	// ===================
	
	@Override
	public void markInvitationCodeAsUsed(String code) throws DataAccessException {
		try {
			invitationRepository.markInvitationCodeAsUsed(code);
		} catch (SQLException e) {
			throw new DataAccessException("Could not mark used invitation code in the database code due to: " + e);
		}
	}
	
	// ===================
    // 		 DESTROY 
 	// ===================
	
}