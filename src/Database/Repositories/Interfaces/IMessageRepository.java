package Database.Repositories.Interfaces;

import java.sql.SQLException;
import java.util.List;

import Models.Message;

public interface IMessageRepository {
	
	
	public List<Message> getMessagesForUsername(String username) throws SQLException;
	
	public boolean updateMessage(int messageId, String newContent) throws SQLException;
	
	public Message insertMessage(Message message) throws SQLException;
	
	
	public boolean deleteMessage(int messageId) throws SQLException;
	

}
