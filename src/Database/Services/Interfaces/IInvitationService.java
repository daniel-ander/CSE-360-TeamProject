package Database.Services.Interfaces;

import java.sql.SQLException;

public interface IInvitationService {

	public String generateInvitationCode(String roles) throws SQLException;
	public String getRoleFromInviteCode(String code) throws SQLException;
	
	public boolean validateInvitationCode(String code) throws SQLException;
	
	public void markInvitationCodeAsUsed(String code) throws SQLException;
	public void printInvitationCodesTable() throws SQLException;
}