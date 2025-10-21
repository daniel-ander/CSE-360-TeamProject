package Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Controllers.PostDisplayViewModel;
import Database.DBConnectionStore;
import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IPermissionService;
import Database.Services.Interfaces.IQuestionService;
import DisplayModels.PostWithPermissions;
import DisplayModels.QuestionWithAnswersAndPermissions;
import Exceptions.DBExceptions.DataAccessException;
import Models.Answer;
import Models.IPost;
import Models.Question;
import Sessions.UserSession;

public class PreLoader {

    private final IQuestionService questionService;
    private final IAnswerService answerService;
    private final IPermissionService permissionService;
    private final UserSession userSession;

    public PreLoader(DBConnectionStore dBConnectionStore, UserSession userSession) {
        this.questionService = dBConnectionStore.getQuestionService();
        this.answerService = dBConnectionStore.getAnswerService();
        this.permissionService = dBConnectionStore.getPermissionService();
        this.userSession = userSession;
    }
    
//    public PreloadedDataStore preloadFirst(int amount) {
//        List<Question> questions = questionService.getAllQuestions();
//        Map<Integer, List<Answer>> answers = new HashMap<>();
//        Map<Integer, List<String>> permissions = new HashMap<>();
//
//        for (Question q : questions) {
//            answers.put(q.getId(), answerService.getAnswersByQuestionID(q.getId()));
//            permissions.put(q.getId(), permissionService.getEffectivePermissions(
//            	userSession.getCurrentUserId(), q.getId(), IPost.Type.Question));
//        }
//
//        return new PreloadedDataStore(questions, answers, permissions);
//    }

    public PreloadedDataStore preloadAll() throws DataAccessException {
        List<Question> questions = questionService.getAllQuestions();
        Map<Integer, List<Answer>> answers = new HashMap<>();
        Map<Integer, List<String>> permissions = new HashMap<>();

        for (Question q : questions) {
            answers.put(q.getId(), answerService.getAnswersByQuestionID(q.getId()));
            permissions.put(q.getId(), permissionService.getEffectivePermissions(
            	userSession.getCurrentUserId(), q.getId(), IPost.Type.Question));
        }

        return new PreloadedDataStore(questions, answers, permissions);
    }
    
    public void preload() {
    	ExecutorService executor = Executors.newSingleThreadExecutor();
    	
    	executor.submit(() -> {
    		List<Question> initialDisplayQuestions = questionService.getLimitedAmountOfQuestions(20);
            
            List<PostWithPermissions> questionsWithPermissions = new ArrayList<>();
            
            
            // Getting the first batch of pre-loaded questions
            for (Question question : initialDisplayQuestions) {
                List<String> userQuestionPermissions = permissionService
                        .getEffectivePermissions(userSession.getCurrentUserId(), question.getId(), IPost.Type.Question);
                
                questionsWithPermissions.add(new PostWithPermissions(question, userQuestionPermissions));
            }
            
            AppCache.getInstance().setInitialQuestionsData(questionsWithPermissions);
            
            List<PostWithPermissions> answersWithPermissions = new ArrayList<>();
            
            // Filling out the first batch with answers
            for (Question question : initialDisplayQuestions) {

                answersWithPermissions = new ArrayList<>();

                List<Answer> initialAnswers = answerService.getAnswersByQuestionID(question.getId());
                
                for(Answer answer : initialAnswers) {
                	List<String> userAnswerPermissions = permissionService
                			.getEffectivePermissions(userSession.getCurrentUserId(), answer.getId(), IPost.Type.Answer);
                	answersWithPermissions.add(new PostWithPermissions(answer, userAnswerPermissions));
                	
                }
                
                AppCache.getInstance().setAnswersData(answersWithPermissions);
                                
            }
            
    	});
    }
}