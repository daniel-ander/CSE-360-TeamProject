package Database;

import java.sql.SQLException;

import Database.Repositories.AnswerRepository;
import Database.Repositories.InvitationRepository;
import Database.Repositories.QuestionRepository;
import Database.Repositories.UserRepository;
import Database.Repositories.Interfaces.IInvitationRepository;
import Database.Repositories.Interfaces.*;
import Database.Services.Interfaces.*;
import Database.Services.*;

// An object that holds every connection
public class DBConnectionStore {
	private final IGeneralDBManager generalDBManager;
	private final IUserService userService;
	private final IInvitationService invitationService;
	private final IQuestionService questionService;
	private final IAnswerService answerService;
	
	public DBConnectionStore(
			IGeneralDBManager generalDBManager, 
			IUserService userService, 
			IInvitationService invitationService,
			IQuestionService questionService,
			IAnswerService answerService
	) {
		this.generalDBManager = generalDBManager;
		this.userService = userService;
		this.invitationService = invitationService;
		this.questionService = questionService;
		this.answerService = answerService;
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
	
	// ===================
    // 		FACTORY 
 	// ===================
	
	// Factory method for creating a repository provider
	public static DBConnectionStore createInstance() throws SQLException {
		IGeneralDBManager dbHelper = new GeneralDBManager();
		
		IUserRepository userRepository = new UserRepository(dbHelper);
		IUserService userService = new UserService(userRepository);
		
		IInvitationRepository invitationRepository = new InvitationRepository(dbHelper);
		IInvitationService invitationService = new InvitationService(invitationRepository);
		
		IQuestionRepository questionRepository = new QuestionRepository(dbHelper);
		IQuestionService questionService = new QuestionService(questionRepository);
		
		IAnswerRepository answerRepository = new AnswerRepository(dbHelper);
		IAnswerService answerService = new AnswerService(answerRepository);
		
		
		return new DBConnectionStore(dbHelper, userService, invitationService, questionService, answerService);
	}
}


