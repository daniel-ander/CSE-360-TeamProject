package Database;

import java.sql.SQLException;

import Database.Repositories.AnswerRepository;
import Database.Repositories.InvitationRepository;
import Database.Repositories.MessageRepository;
import Database.Repositories.QuestionRepository;
import Database.Repositories.ReviewerRequestRepository;
import Database.Repositories.UserRepository;
import Database.Repositories.PermissionRepository;
import Database.Repositories.Interfaces.IInvitationRepository;
import Database.Repositories.Interfaces.*;
import Database.Services.Interfaces.*;
import Database.Services.*;

// An object that holds every connection service
public class DBConnectionStore {
	private final IGeneralDBManager generalDBManager;
	private final IUserService userService;
	private final IInvitationService invitationService;
	private final IQuestionService questionService;
	private final IAnswerService answerService;
	private final IPermissionService permissionService;
	private final IMessageService messageService;
	
	public DBConnectionStore(
			IGeneralDBManager generalDBManager, 
			IUserService userService, 
			IInvitationService invitationService,
			IQuestionService questionService,
			IMessageService messageService,
			IAnswerService answerService,
			IPermissionService permissionService
	) {
		this.generalDBManager = generalDBManager;
		this.userService = userService;
		this.invitationService = invitationService;
		this.questionService = questionService;
		this.messageService = messageService;
		this.answerService = answerService;
		this.permissionService = permissionService;
	}
	
	// ===================
    // 		 GETTERS 
 	// ===================
	
	public IGeneralDBManager getGeneralDBManager() {
		return generalDBManager;
	}
	
	public IUserService getUserService() {
		return userService;
	}
	
	public IInvitationService getInvitationService() {
		return invitationService;
	}
	
	public IQuestionService getQuestionService() {
		return questionService;
	}
	
	public IAnswerService getAnswerService() {
		return answerService;
	}
	
	public IMessageService getMessageService() {
		return messageService;
	}
	public IPermissionService getPermissionService() {
		return permissionService;
	}
	
	// ===================
    // 		FACTORY 
 	// ===================
	
	// Factory method for creating a repository provider
	public static DBConnectionStore createInstance() throws SQLException {
		IGeneralDBManager dbHelper = new GeneralDBManager();
		
		try {
			// Only initialize DB if the file does not already exist to avoid duplicate seeding
			if (!((GeneralDBManager)dbHelper).doesDatabaseExist("database/FoundationDatabase")) {
				dbHelper.initializeDB();
			}
		} catch (SQLException e) {
			System.err.println("Error initializing database: " + e.getMessage());
			throw e;
		}
		
		IUserRepository userRepository = new UserRepository(dbHelper);
		IReviewerRequestRepository reviewerRequestRepository = new ReviewerRequestRepository(dbHelper);
		IUserService userService = new UserService(userRepository, reviewerRequestRepository);
		
		IInvitationRepository invitationRepository = new InvitationRepository(dbHelper);
		IInvitationService invitationService = new InvitationService(invitationRepository);
		
		IQuestionRepository questionRepository = new QuestionRepository(dbHelper);
		IQuestionService questionService = new QuestionService(questionRepository);
		
		IAnswerRepository answerRepository = new AnswerRepository(dbHelper);
		IAnswerService answerService = new AnswerService(answerRepository);
		
		IPermissionRepository permissionRepository = new PermissionRepository(dbHelper);
		IPermissionService permissionService = new PermissionService(permissionRepository);
		
		IMessageRepository messageRepository = new MessageRepository(dbHelper);
		IMessageService messageService = new MessageService(messageRepository); // Placeholder for message service
		
		
		
		return new DBConnectionStore(dbHelper, userService, invitationService, questionService, messageService, answerService, permissionService);
	}
}