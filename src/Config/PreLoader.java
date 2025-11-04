package Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Database.DBConnectionStore;
import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IPermissionService;
import Database.Services.Interfaces.IQuestionService;
import DisplayModels.PostWithPermissions;

import Exceptions.DBExceptions.DataAccessException;
import Models.Answer;
import Models.IPost;
import Models.Question;
import Sessions.UserSession;
import Utils.AnswerPreloadPriority;

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

	public PreloadedDataStore preloadAll() throws DataAccessException {
		List<Question> questions = questionService.getAllQuestions();
		Map<Integer, List<Answer>> answers = new HashMap<>();
		Map<Integer, List<String>> permissions = new HashMap<>();

		for (Question q : questions) {
			answers.put(q.getId(), answerService.getAnswersByQuestionID(q.getId()));
			permissions.put(q.getId(), permissionService.getEffectivePermissions(userSession.getCurrentUserId(),
					q.getId(), IPost.Type.Question));
		}

		return new PreloadedDataStore(questions, answers, permissions);
	}

	public void preload() {
		ExecutorService executor = Executors.newFixedThreadPool(4);

		executor.submit(() -> {
			List<Question> initialDisplayQuestions = questionService.getLimitedAmountOfQuestions(20);

			fetchQuestions(initialDisplayQuestions, executor);
			fetchAnswers(initialDisplayQuestions, executor);

		});
	}

	private void fetchQuestions(List<Question> initialDisplayQuestions, ExecutorService executor) {
		executor.submit(() -> {

			List<PostWithPermissions> questionsWithPermissions = new ArrayList<>();

			// Getting the first batch of pre-loaded questions
			for (Question question : initialDisplayQuestions) {
				List<String> userQuestionPermissions = permissionService
						.getEffectivePermissions(userSession.getCurrentUserId(), question.getId(), IPost.Type.Question);

				questionsWithPermissions.add(new PostWithPermissions(question, userQuestionPermissions));
			}

			AppCache.getInstance().setInitialQuestionsData(questionsWithPermissions);
		});
	}

	private void fetchAnswers(List<Question> questions, ExecutorService executor) {
		executor.submit(() -> {
			List<Question> remainingQuestions = new ArrayList<>(questions);

			// We make sure that the questions for which the answers are loaded
			// are loaded based on which questions the user sees
			for (Question question : questions) {
				if (AnswerPreloadPriority.isPriority(question.getId())) {
					fetchAnswersForQuestion(question, executor);
					remainingQuestions.remove(question);
				}
			}

			for (Question question : remainingQuestions) {
				fetchAnswersForQuestion(question, executor);
			}
		});
	}

	private void fetchAnswersForQuestion(Question question, ExecutorService executor) {
		executor.submit(() -> {
			List<Answer> answers = answerService.getAnswersByQuestionID(question.getId());
			List<PostWithPermissions> answersWithPermissions = new ArrayList<>();
			for (Answer a : answers) {
				List<String> permissions = permissionService.getEffectivePermissions(userSession.getCurrentUserId(),
						a.getId(), IPost.Type.Answer);
				answersWithPermissions.add(new PostWithPermissions(a, permissions));
			}

			AppCache.getInstance().setAnswersDataProperty(answersWithPermissions);
		});
	}
}