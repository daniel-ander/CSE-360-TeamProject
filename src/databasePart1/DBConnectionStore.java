package databasePart1;

import java.sql.SQLException;

// An object that holds every connection
public class DBConnectionStore {
	private final DatabaseHelper dbHelper;
	private final IUserDBManager userDBManager;
	
	public DBConnectionStore(DatabaseHelper dbHelper, IUserDBManager userDBManager) throws SQLException {
		this.dbHelper = new DatabaseHelper();
		dbHelper.connectToDatabase();
		this.userDBManager = new UserDBManager(dbHelper);
	}
	
	// ===================
	// Getters
	// ===================
	
	public IUserDBManager getUserRepository() {
		return userDBManager;
	}
	
	public DatabaseHelper getDatabaseHelper() {
		return dbHelper;
	}
	
	// Factory method for creating a repository provider
	public static DBConnectionStore createInstance() throws SQLException {
		DatabaseHelper dbHelper = new DatabaseHelper();
		dbHelper.connectToDatabase();
		IUserDBManager userRepo = new UserDBManager(dbHelper);
		
		return new DBConnectionStore(dbHelper, userRepo);
	}
}