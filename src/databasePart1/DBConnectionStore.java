package databasePart1;

import java.sql.SQLException;

// An object that holds every connection
public class DBConnectionStore {
	private final IGeneralDBManager generalDBManager;
	private final IUserDBManager userDBManager;
	private final IInvitationDBManager invitationDBManager;
	
	public DBConnectionStore(
			IGeneralDBManager generalDBManager, 
			IUserDBManager userDBManager, 
			IInvitationDBManager invitationDBManager
	) throws SQLException {
		this.generalDBManager = generalDBManager;
		this.userDBManager = userDBManager;
		this.invitationDBManager = invitationDBManager;
	}
	
	// ===================
	// Getters
	// ===================
	
	public IUserDBManager getUserDatabaseManager() {
		return userDBManager;
	}
	
	public IGeneralDBManager getGeneralDBManager() {
		return generalDBManager;
	}
	
	public IInvitationDBManager getInvitationDBManager() {
		return invitationDBManager;
	}
	
	// Factory method for creating a repository provider
	public static DBConnectionStore createInstance() throws SQLException {
		IGeneralDBManager dbHelper = new GeneralDBManager();
		IUserDBManager userManager = new UserDBManager(dbHelper);
		IInvitationDBManager invitationManager = new InvitationDBManager(dbHelper);
		
		return new DBConnectionStore(dbHelper, userManager, invitationManager);
	}
}