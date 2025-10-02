package databasePart1;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDatabaseHelper {
	public void connectToDatabase() throws SQLException;
	public void createTables() throws SQLException;
	public void markInvitationCodeAsUsed(String code);
	public void printInvitationCodesTable();
	public void closeConnection();
	
	public boolean isDatabaseEmpty() throws SQLException;
	public boolean validateInvitationCode(String code);
	
	public String generateInvitationCode(String roles);
	public String getRoleFromInviteCode(String code);
	
	public Connection getConnection() throws SQLException;
}