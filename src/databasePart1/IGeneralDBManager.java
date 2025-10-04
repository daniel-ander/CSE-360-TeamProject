package databasePart1;

import java.sql.Connection;
import java.sql.SQLException;

public interface IGeneralDBManager {
	public void initializeDBIfNeeded() throws SQLException;
	public void createTables() throws SQLException;
	
	public boolean isDatabaseEmpty() throws SQLException;
	
	public Connection getConnection() throws SQLException;
}