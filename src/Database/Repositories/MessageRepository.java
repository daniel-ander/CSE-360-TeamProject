package Database.Repositories;

import Database.Repositories.Interfaces.IMessageRepository;
import java.sql.SQLException;
import java.util.List;

import Database.IGeneralDBManager;
import Models.Message;

public class MessageRepository implements IMessageRepository {
	private final IGeneralDBManager generalDBManager;
	
	public MessageRepository(IGeneralDBManager generalDBManager) {
		this.generalDBManager = generalDBManager;
	}
	
	// ===================
	// 		 CREATE 
 	// ===================
	
	@Override
	public Message insertMessage(Message message) throws SQLException {
		String insertSQL = String.format("", null);
		// Implement the insertion logic here using generalDBManager
		// TODO Auto-generated method stub 
		return null;
	}
	
	// ===================
	// 		 READ
	// ===================
	
	@Override
	public List<Message> getMessagesForUsername(String username) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	// ===================
	// 		 UPDATE
	// ===================
	
	@Override
	public boolean updateMessage(int messageId, String newContent) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	
	// ===================
	// 		 DELETE
	// ===================
	
	@Override
	public boolean deleteMessage(int messageId) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	

}
