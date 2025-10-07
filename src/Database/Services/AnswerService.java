package Database.Services;

import Models.Answer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Database.Repositories.Interfaces.IAnswerRepository;

import Database.Services.Interfaces.IAnswerService;

public class AnswerService implements IAnswerService {
	
	private List<Answer> answers;

	private final IAnswerRepository answerRepository;
	
	public AnswerService(IAnswerRepository answerRepository) {
		this.answerRepository = answerRepository;
	}

	// ===================
    // 		 CREATE 
 	// ===================
	
	@Override
	public Answer addAnswer(int questionId, int creatorId, String text) throws SQLException {
		int answerId = answerRepository.insertAnswer(questionId, creatorId, text);
		
		return new Answer(answerId, questionId, creatorId, text, false);
	}
	
	// ===================
    // 		 READ 
 	// ===================
	
	@Override
	public List<Answer> getAllAnswers() throws SQLException {
		return new ArrayList<>(answers);
	}
	
	@Override
	public List<Answer> getAllAnswersForQuestion(int questionId) throws SQLException {
		return answerRepository.getAllAnswersForQuestion(questionId);
	}
	
	@Override
	public List<Answer> getCorrectAnswers() throws SQLException {
		return answerRepository.getCorrectAnswers();
	}

	@Override
	public List<Answer> getAnswersBySearch(String searchString) throws SQLException {
		return answerRepository.getAnswersBySearch(searchString);
	}

	@Override
	public List<Answer> getAnswersByQuestionId(int questionId) throws SQLException {
		return answerRepository.getAnswersByQuestionId(questionId);
	}
	
	@Override
	public Answer getAnswerById(int answerId) throws SQLException {
		return answerRepository.getAnswerById(answerId);
	}
	
	// ===================
    // 		 UPDATE 
 	// ===================
	
	@Override
	public boolean updateAnswer(int id, String newText, boolean newCorrect) throws SQLException {
		if (newText == null || newText.trim().isEmpty()) {
			throw new IllegalArgumentException("Answer text cannot be empty.");
		}

		Answer a = getAnswerById(id);
		if (a == null) {
			return false;
		}

		answerRepository.updateAnswer(id, newText, newCorrect);
		
		a.setText(newText);
		a.setIsCorrect(newCorrect);
		
		return true;
	}
	
	// ===================
    // 		 DESTROY 
 	// ===================
	
	@Override
	public boolean deleteAnswer(int id) throws SQLException {
		return answerRepository.deleteAnswerById(id);
	}

	@Override
	public boolean deleteByQuestionId(int questionId) throws SQLException {
		return answerRepository.deleteByQuestionId(questionId);
	}

}