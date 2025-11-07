package Database.Services;

import Database.Repositories.Interfaces.IMessageRepository;
import Database.Services.Interfaces.IMessageService;
import java.util.List;

import Exceptions.DBExceptions.*;
import Models.Message;

public class MessageService implements IMessageService {

		private final IMessageRepository messageRepository;
		
		public MessageService(IMessageRepository messageRepository) {
			this.messageRepository = messageRepository;
		}
		
		// ===================
		// 		 CREATE
		// ===================
		
		@Override
		public Message insertMessage(Message message) throws DataAccessException {
			// TODO Auto-generated method stub
			return null;
		}
		
		// ===================
		// 		 READ
		// ===================
		
		@Override
		public List<Message> getMessagesForUsername(String username) throws DataAccessException {
			// TODO Auto-generated method stub
			return null;
		}
		
		// ===================
		// 		 UPDATE
		// ===================
		
		@Override
		public boolean updateMessage(int messageId, String newContent) throws DataAccessException {
			// TODO Auto-generated method stub
			return false;
		}
		
		// ===================
		// 		 DELETE
		// ===================
		
		@Override
		public boolean deleteMessage(int messageId) throws DataAccessException {
			// TODO Auto-generated method stub
			return false;
		}
		
}

