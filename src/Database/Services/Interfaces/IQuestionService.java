package Database.Services.Interfaces;

import Models.Question;

import java.util.List;

import Exceptions.DBExceptions.DataAccessException;

public interface IQuestionService {
	
	public List<Question> getAllQuestions();
	
	public Question getQuestionById(int targetId) throws DataAccessException;
	
	public boolean addQuestion(Question q) throws DataAccessException;
	public boolean updateQuestion(int id, String newText) throws DataAccessException;
	public boolean deleteQuestion(int id) throws DataAccessException;
}