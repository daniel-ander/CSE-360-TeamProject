package Database;

import java.sql.Connection;
import java.sql.SQLException;

public interface IGeneralDBManager {	
	public void initializeDB() throws SQLException;
	
	public boolean isDatabaseEmpty() throws SQLException;
	
	public Connection getConnection() throws SQLException;
}