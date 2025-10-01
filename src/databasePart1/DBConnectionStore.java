package databasePart1;

import java.sql.SQLException;

// An object that holds every connection
public class DBConnectionStore {
	private final IDatabaseHelper dbHelper;
	private final IUserDBManager userDBManager;
	
	public DBConnectionStore(IDatabaseHelper dbHelper, IUserDBManager userDBManager) throws SQLException {
		this.dbHelper = dbHelper;
		dbHelper.connectToDatabase();
		this.userDBManager = userDBManager;
	}
	
	// ===================
	// Getters
	// ===================
	
	public IUserDBManager getUserDatabaseManager() {
		return userDBManager;
	}
	
	public IDatabaseHelper getDatabaseHelper() {
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