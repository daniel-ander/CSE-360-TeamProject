package databasePart1;

import java.sql.Connection;
import java.sql.SQLException;

// NOTE: - The purpose of this interface and the implementing classes is 
// to do general and meta DB tasks
public interface IGeneralDBManager {
	public void initializeDBIfNeeded() throws SQLException;
	public void createTables() throws SQLException;
	
	public boolean isDatabaseEmpty() throws SQLException;
	
	public Connection getConnection() throws SQLException;
}