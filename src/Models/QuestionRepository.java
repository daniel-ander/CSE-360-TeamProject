package Models;

import java.util.ArrayList;
import java.util.List;

public class QuestionRepository {
	private List<Question> questions;
	
	// CREATE SECTION
	public QuestionRepository() {
		this.questions = new ArrayList<>();
	}
	
	public void addQuestion(Question q) {
		boolean IdExists = (getQuestionById(q.getId()) != null);
		
		if (IdExists) {
			throw new IllegalArgumentException("Question with this id already exists!");
		}
		questions.add(q);
	}
	
	// READ SECTION
	public List<Question> getAll(){
		return new ArrayList<>(questions);
	}
	
	public Question getQuestionById(int searchId) {
		for (Question q : questions) {
			if (q.getId() == searchId) {
				return q;
			}
		}
		return null;
	}
	
	public AnswerRepository getAnswers(int questionId, AnswerRepository answerRepository) {
		return answerRepository.getAnswersByQuestionId(questionId);
	}
	
	public QuestionRepository getAnsweredQuestions(AnswerRepository as) {
		QuestionRepository result = new QuestionRepository();
		for (Question q : questions) {
			if (!(this.getAnswers(q.getId(), as).getAll().isEmpty())) {
				result.addQuestion(q);
			}
		}
		return result;
	}
	
	public QuestionRepository getUnansweredQuestions(AnswerRepository as) {
		QuestionRepository result = new QuestionRepository();
		for (Question q : questions) {
			if (this.getAnswers(q.getId(), as).getAll().isEmpty()) {
				result.addQuestion(q);
			}
		}
		return result;
	}
	
    public QuestionRepository getQuestionsBySearch(String searchString) {
    	QuestionRepository result = new QuestionRepository();
		for (Question q : questions) {
			if (q.getText().toLowerCase().contains(searchString.toLowerCase())) {
				result.addQuestion(q);
			}
		}
		return result;
    }
	
	// UPDATE SECTION
	public boolean updateQuestion(int id, String newText) {
		if (newText == null || newText.trim().isEmpty()) {
		    throw new IllegalArgumentException("Question text cannot be empty.");
		}
		
		Question q = getQuestionById(id);
		if (q == null) {
			return false;
		}
		q.setText(newText);
		return true;
	}
	
	// DELETE SECTION
	public boolean deleteQuestion(int id, AnswerRepository answerRepository) {
		Question q = this.getQuestionById(id);
		
		if (q == null) {
			return false;
		}
		
		answerRepository.deleteByQuestionId(id);
		questions.remove(q);
		
		return true;
	}
}