package Database.Repositories.Interfaces;

import java.sql.SQLException;

public interface IInvitationRepository {
	public boolean insertInvitationCode(String roles) throws SQLException;
	public boolean validateInvitationCode(String code) throws SQLException;
	
	public String getRoleFromInviteCode(String code) throws SQLException;

	public void markInvitationCodeAsUsed(String code) throws SQLException;
	public void printInvitationCodesTable() throws SQLException;
}

