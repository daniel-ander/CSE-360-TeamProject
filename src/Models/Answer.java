package Models;

import java.sql.Timestamp;

public class Answer {
	private int id; // -1 if answer isn't in the database yet
	private String text;
	private boolean isCorrect;
	private int questionId;
	private int userId;
	private int parentAnswerId; // -1 if it is not a reply to another answer
	private Timestamp timestamp;
	private boolean isDeleted;
	private int upvoteCount;
	

	// Constructor for creating a new answer (not yet in DB)
	public Answer(int id, int questionId, int userId, String text, boolean isCorrect) {
		if (text == null || text.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		this.id = id;
		this.text = text;
		this.isCorrect = isCorrect;
		this.questionId = questionId; // -1 represents that it is not assigned
		this.userId = userId;
		this.parentAnswerId = -1; // -1 represents that it is not a reply to another answer
	}
	
	// Constructor for pulling from DB with all fields
	public Answer(int id, int questionId, int parentAnswerId, int userId, String body,Timestamp timestamp, int upvoteCount, boolean isCorrect, boolean isDeleted) {
		if (body == null || body.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		
		this.id = id;
		this.questionId = questionId; // -1 represents that it is not assigned
		this.parentAnswerId = parentAnswerId; // -1 represents that it is not a reply to another answer
		this.userId = userId;
		this.text = body;
		this.timestamp = timestamp;
		this.upvoteCount = upvoteCount;
		
		this.isCorrect = isCorrect;
		this.isDeleted = isDeleted;

	}

	// ===================
    // 		 GETTERS 
 	// ===================
	public int getId() {return id;}
	public int getUserId() {return userId;}
	public int getQuestionId() {return questionId;}
	public int getParentAnswerId() {return parentAnswerId;}
	public Timestamp getTimestamp() {return timestamp;}
	public boolean getIsDeleted() {return isDeleted;}
	public int getUpvoteCount() {return upvoteCount;}
	public String getText() {return text;}
	public boolean isCorrect() {return isCorrect;}
	
	// ===================
    // 		 SETTERS 
 	// ===================
	
	public void setQuestionId(int givenId) {questionId = givenId;}
	public void setIsCorrect(boolean correct) { isCorrect = correct;}
	public void setIsDeleted(boolean deleted) { isDeleted = deleted;}
	public void setUpvoteCount(int newCount) { upvoteCount = newCount;}
	public void setParentAnswerId(int givenId) {parentAnswerId = givenId;}
	public void setTimestamp(Timestamp newTimestamp) { timestamp = newTimestamp; }
	public void setUserId(int newUserId) { userId = newUserId; }
	
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
