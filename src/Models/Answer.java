package Models;

public class Answer {
	private int id; // -1 if answer isn't in the database yet
	private String text;
	private boolean isCorrect;
	private int questionId;
	private int userId;
	
	public Answer(int questionId, int userId, String text, boolean isCorrect) {
		this(-1, questionId, userId, text, isCorrect);
	}
	
	public Answer(int id, int questionId, int userId, String text, boolean isCorrect) {
		if (text == null || text.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		this.id = id;
		this.text = text;
		this.isCorrect = isCorrect;
		this.questionId = questionId; // -1 represents that it is not assigned
		this.userId = userId;
	}
	
	// ===================
    // 		 GETTERS 
 	// ===================
	public int getId() {return id;}
	public int getUserId() {return userId;}
	public int getQuestionId() {return questionId;}
	
	public String getText() {return text;}

	public boolean isCorrect() {return isCorrect;}
	
	// ===================
    // 		 SETTERS 
 	// ===================
	
	public void setQuestionId(int givenId) {questionId = givenId;}
	public void setIsCorrect(boolean correct) { isCorrect = correct;}
	
	public void setText(String newText) {
		if (newText == null || newText.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		text = newText;
	}
	
	// ===================
    // 		  UTILS 
 	// ===================
	// this lets the answer print out correctly
    @Override
    public String toString() {
        return "A" + id + ": " + text + (isCorrect ? " (Correct)" : "");
    }
}
