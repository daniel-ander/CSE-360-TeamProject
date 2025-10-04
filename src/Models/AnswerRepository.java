package Models;

import java.util.ArrayList;
import java.util.List;

public class AnswerRepository {
	private List<Answer> answers;
	
	// CREATE SECTION
	public AnswerRepository() {
		this.answers = new ArrayList<>();
	}
	
	public void addAnswer(Answer a) {
		boolean IdExists = (getAnswerById(a.getId()) != null);
		
		if (IdExists) {
			throw new IllegalArgumentException("Answer with this id already exists!");
		}
		answers.add(a);
	}
	
	// READ SECTION
	public List<Answer> getAll(){
		return new ArrayList<>(answers);
	}
	
	public Answer getAnswerById(int answerId) {
		for (Answer a : answers) {
			if (a.getId() == answerId) {
				return a;
			}
		}
		return null;
	}
	
    public AnswerRepository getCorrectAnswers() {
    	AnswerRepository resultAnswers = new AnswerRepository();
		for (Answer a : answers) {
			if (a.isCorrect()) {
				resultAnswers.addAnswer(a);
			}
		}
		return resultAnswers;
    }
    
    public AnswerRepository getAnswersBySearch(String searchString) {
    	AnswerRepository resultAnswers = new AnswerRepository();
		for (Answer a : answers) {
			if (a.getText().toLowerCase().contains(searchString.toLowerCase())) {
				resultAnswers.addAnswer(a);
			}
		}
		return resultAnswers;
    }
    
    public AnswerRepository getAnswersByQuestionId(int questionId) {
    	AnswerRepository resultAnswers = new AnswerRepository();
		for (Answer a : answers) {
			if (a.getQuestionId() == questionId) {
				resultAnswers.addAnswer(a);
			}
		}
		return resultAnswers;
    }
	
	// UPDATE SECTION
	public boolean updateAnswer(int id, String newText, boolean newCorrect) {
		if (newText == null || newText.trim().isEmpty()) {
		    throw new IllegalArgumentException("Answer text cannot be empty.");
		}
		
		Answer a = getAnswerById(id);
		if (a == null) {
			return false;
		}
		
		a.setText(newText);
		a.setIsCorrect(newCorrect);
		return true;
	}
	
	// DELETE SECTION
	public boolean deleteAnswer(int id) {
		return answers.removeIf(a -> a.getId() == id);
	}
	
	public boolean deleteByQuestionId(int questionId) {
		return answers.removeIf(a -> a.getQuestionId() == questionId);
	}
}