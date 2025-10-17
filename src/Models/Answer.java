package Models;

import java.sql.Timestamp;

public class Answer implements IPost {
	public static String tableName = "answers";
	
	private Integer id;
	private String bodyText;
	private boolean isCorrect;
	private Integer questionId;
	private Integer authorId;
	private String authorName;
	private Integer parentAnswerId;
	private Timestamp timestamp;
	private boolean isDeleted;
	private Integer upvoteCount;
	private int replyDepth;
	

	// Constructor for creating a new answer (not yet in DB)
	public Answer(int questionId, int authorId, String authorName, String bodyText) {
		this(questionId, null, authorId, authorName, bodyText);
	}
	
	// Constructor for creating a new nested answer (not yet in DB)
	public Answer(int questionId, Integer parentAnswerId, int authorId, String authorName, String bodyText) {
		this(null, questionId, parentAnswerId, authorId, authorName, bodyText, null, null, false, false, 0);
	}
	
	// Constructor for pulling from DB with all fields
	public Answer(
			Integer id, 
			Integer questionId, 
			Integer parentAnswerId, 
			Integer authorId, 
			String authorName, 
			String bodyText,
			Timestamp timestamp, 
			Integer upvoteCount, 
			boolean isCorrect, 
			boolean isDeleted, 
			int replyDepth
	) {
		if (bodyText == null || bodyText.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		
		this.id = id;
		this.questionId = questionId; // -1 represents that it is not assigned
		this.parentAnswerId = parentAnswerId; // -1 represents that it is not a reply to another answer
		this.authorId = authorId;
		this.authorName = authorName;
		this.bodyText = bodyText;
		this.timestamp = timestamp;
		this.upvoteCount = upvoteCount;
		this.replyDepth = replyDepth;
		
		this.isCorrect = isCorrect;
		this.isDeleted = isDeleted;

	}
	
	// ===================
    // 		 GETTERS 
 	// ===================
	public Integer getId() {return id;}
	public Integer getAuthorId() {return authorId;}
	public Integer getQuestionId() {return questionId;}
	public Integer getParentAnswerId() {return parentAnswerId;}
	public Timestamp getTimestamp() {return timestamp;}
	public boolean getIsDeleted() {return isDeleted;}
	public Integer getUpvoteCount() {return upvoteCount;}
	public String getAuthorName() {return authorName;}
	public boolean isCorrect() {return isCorrect;}
	public int getReplyDepth() {return replyDepth;}
	
	// ===================
    // 		 SETTERS 
 	// ===================
	
	public void setQuestionId(int givenId) {questionId = givenId;}
	public void setIsCorrect(boolean correct) { isCorrect = correct;}
	public void setIsDeleted(boolean deleted) { isDeleted = deleted;}
	public void setUpvoteCount(int newCount) { upvoteCount = newCount;}
	public void setParentAnswerId(int givenId) {parentAnswerId = givenId;}
	public void setTimestamp(Timestamp newTimestamp) { timestamp = newTimestamp; }
	public void setReplyDepth(int depth) { replyDepth = depth; }
	public void setUserId(int newUserId) { authorId = newUserId; }
	public void setId(int newId) { id = newId; }
	public void setAuthorName(String name) { authorName = name; }
	public void setText(String newText) {
		if (newText == null || newText.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		bodyText = newText;
	}
	
	// ===================
    // 		  UTILS 
 	// ===================
	// this lets the answer print out correctly
    @Override
    public String toString() {
        return "A" + id + ": " + bodyText + (isCorrect ? " (Correct)" : "");
    }

	@Override
	public String getBodyText() {
		return bodyText;
	}

	@Override
	public String getTitleText() {
		return null;
	}

	@Override
	public Type getPostType() {
		return Type.Answer;
	}
}
