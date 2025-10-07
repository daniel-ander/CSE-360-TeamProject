package Database.Services;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import Database.Repositories.Interfaces.IInvitationRepository;
import Database.Services.Interfaces.IInvitationService;

public class InvitationService implements IInvitationService {
	
	private final IInvitationRepository invitationRepository;
	
	public InvitationService(IInvitationRepository invitationRepository) throws SQLException {
		this.invitationRepository = invitationRepository;
	}
	
	// ===================
    // 		 CREATE 
 	// ===================
	
	@Override
	public String generateInvitationCode(String roles) throws SQLException {
	    String code = roles + "_" + UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    
	    invitationRepository.insertInvitationCode(code);
	    
	    return code;
	}
	
	// ===================
    // 		 READ 
 	// ===================
	
	@Override
	public String getRoleFromInviteCode(String code) throws SQLException {
		// TODO :- Not sure what this is for
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
	public void printInvitationCodesTable() throws SQLException {
	    invitationRepository.printInvitationCodesTable();
	}
	
	@Override
	public boolean validateInvitationCode(String code) throws SQLException {
	    return invitationRepository.validateInvitationCode(code);
	}
	
	// ===================
    // 		 UPDATE 
 	// ===================
	
	@Override
	public void markInvitationCodeAsUsed(String code) throws SQLException {
	    invitationRepository.markInvitationCodeAsUsed(code);
	}
	
	// ===================
    // 		 DESTROY 
 	// ===================
	
}