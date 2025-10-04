package Database;

import java.sql.SQLException;

// An object that holds every connection
public class DBConnectionStore {
	private final IGeneralDBManager generalDBManager;
	private final IUserRepository userRepository;
	private final IInvitationRepository invitationRepository;
	
	public DBConnectionStore(
			IGeneralDBManager generalDBManager, 
			IUserRepository userRepository, 
			IInvitationRepository invitationRepository
	) throws SQLException {
		this.generalDBManager = generalDBManager;
		this.userRepository = userRepository;
		this.invitationRepository = invitationRepository;
	}
	
	// ===================
	// Getters
	// ===================
	
	public IUserRepository getUserRepository() {
		return userRepository;
	}
	
	public IGeneralDBManager getGeneralDBManager() {
		return generalDBManager;
	}
	
	public IInvitationRepository getInvitationRepository() {
		return invitationRepository;
	}
	
	// Factory method for creating a repository provider
	public static DBConnectionStore createInstance() throws SQLException {
		IGeneralDBManager dbHelper = new GeneralDBManager();
		IUserRepository userManager = new UserRepository(dbHelper);
		IInvitationRepository invitationManager = new InvitationRepository(dbHelper);
		
		return new DBConnectionStore(dbHelper, userManager, invitationManager);
	}
}