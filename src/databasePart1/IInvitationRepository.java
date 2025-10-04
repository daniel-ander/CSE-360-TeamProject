package databasePart1;

public interface IInvitationRepository {
	public void markInvitationCodeAsUsed(String code);
	public void printInvitationCodesTable();
	
	public boolean validateInvitationCode(String code);
	
	public String generateInvitationCode(String roles);
	public String getRoleFromInviteCode(String code);
}

