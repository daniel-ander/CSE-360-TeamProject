package databasePart1;

import java.sql.SQLException;

// An object that holds every connection
public class RepositoryProvider {
	private final DatabaseHelper dbHelper;
	private final IUserRepository userRepository;
	
	public RepositoryProvider(DatabaseHelper dbHelper, IUserRepository userRepository) throws SQLException {
		this.dbHelper = new DatabaseHelper();
		dbHelper.connectToDatabase();
		this.userRepository = new UserRepository(dbHelper);
	}
	
	// ===================
	// Getters
	// ===================
	
	public IUserRepository getUserRepository() {
		return userRepository;
	}
	
	public DatabaseHelper getDatabaseHelper() {
		return dbHelper;
	}
	
	// Factory method for creating a repository provider
	public static RepositoryProvider createInstance() throws SQLException {
		DatabaseHelper dbHelper = new DatabaseHelper();
		dbHelper.connectToDatabase();
		IUserRepository userRepo = new UserRepository(dbHelper);
		
		return new RepositoryProvider(dbHelper, userRepo);
	}
}