package Database.Services.Interfaces;

import java.util.List;
import Exceptions.DBExceptions.DataAccessException;
import Models.Message;

public interface IMessageService {
	public List<Message> getMessagesForUsername(String username) throws DataAccessException;
	
	public boolean updateMessage(int messageId, String newContent) throws DataAccessException;
	
	public Message insertMessage(Message message) throws DataAccessException;
	
	public boolean deleteMessage(int messageId) throws DataAccessException;
	

}
