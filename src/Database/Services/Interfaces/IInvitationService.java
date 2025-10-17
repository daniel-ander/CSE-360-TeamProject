package Database.Services.Interfaces;

import Exceptions.DBExceptions.DataAccessException;

public interface IInvitationService {

	public String generateInvitationCode(String roles) throws DataAccessException;
	public String getRoleFromInviteCode(String code) throws DataAccessException;
	
	public boolean validateInvitationCode(String code) throws DataAccessException;
	
	public void markInvitationCodeAsUsed(String code) throws DataAccessException;
	public void printInvitationCodesTable() throws DataAccessException;
}