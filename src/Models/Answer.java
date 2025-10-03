package Models;

public class Answer {
	private int id;
	private String text;
	private boolean isCorrect;
	private int questionId;
	private String creator;
	
	// CREATE SECTION
	public Answer(int id, int questionId, String text, boolean isCorrect, String creatorName) {
		if (text == null || text.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		this.id = id;
		this.text = text;
		this.isCorrect = isCorrect;
		this.questionId = questionId; // -1 represents that it is not assigned
		this.creator = creatorName;
	}
	
	// READ SECTION
	public int getId() {return id;}
	public int getQuestionId() {return questionId;}
	public String getText() {return text;}
	public String getCreator() {return creator;}
	public boolean isCorrect() {return isCorrect;}
	
	// UPDATE SECTION
	public void setQuestionId(int givenId) {questionId = givenId;}
	public void setIsCorrect(boolean correct) { isCorrect = correct;}
	
	public void setText(String newText) {
		if (newText == null || newText.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		text = newText;
	}
	
	// this lets the answer print out correctly
    @Override
    public String toString() {
        return "A" + id + ": " + text + (isCorrect ? " (Correct)" : "");
    }
}
