package Database.Services.Interfaces;

import Models.Question;

import java.util.List;

import Exceptions.DBExceptions.DataAccessException;

public interface IQuestionService {
	
	public List<Question> getAllQuestions();
	public List<Question> getLimitedAmountOfQuestions(int amount);
	
	public Question getQuestionById(int targetId) throws DataAccessException;
	
	public boolean addQuestion(Question q) throws DataAccessException;
	public boolean updateQuestion(int id, String title, String newText) throws DataAccessException;
	public boolean deleteQuestion(int id) throws DataAccessException;
	
	// Get questions authored by a given user
	public List<Question> getQuestionsFromUser(int userId) throws DataAccessException;
}
