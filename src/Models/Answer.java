package Models;

import java.sql.Timestamp;

public class Answer implements IPost {
	public static String tableName = "answers";
	
	private Integer id;
	private String bodyText;
	private boolean isCorrect;
	private boolean isReview;
	private Integer questionId;
	private Integer authorId;
	private String authorName;
	private Integer parentAnswerId;
	private Timestamp createdTimestamp;
	private Timestamp lastUpdatedTimestamp;
	private boolean isDeleted;
	private Integer upvoteCount;
	private int replyDepth;
	

	// Constructor for creating a new answer (not yet in DB)
	public Answer(int questionId, int authorId, String authorName, String bodyText) {
		this(questionId, null, authorId, authorName, bodyText);
	}
	
	// Constructor for creating a new nested answer (not yet in DB)
	public Answer(int questionId, Integer parentAnswerId, int authorId, String authorName, String bodyText) {
		this(null, questionId, parentAnswerId, authorId, authorName, bodyText, null, null, null, false, false, false, 0);
	}
	
	// Constructor for pulling from DB with all fields
	public Answer(
			Integer id, 
			Integer questionId, 
			Integer parentAnswerId, 
			Integer authorId, 
			String authorName, 
			String bodyText,
			Timestamp createdTimestamp,
			Timestamp lastUpdatedTimestamp, 
			Integer upvoteCount, 
			boolean isCorrect, 
			boolean isReview,
			boolean isDeleted, 
			int replyDepth
	) {
		if (bodyText == null || bodyText.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		
		this.id = id;
		this.questionId = questionId; 
		this.parentAnswerId = parentAnswerId; 
		this.authorId = authorId;
		this.authorName = authorName;
		this.bodyText = bodyText;
		this.createdTimestamp = createdTimestamp;
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
		this.upvoteCount = upvoteCount;
		this.replyDepth = replyDepth;
		
		this.isReview = isReview;
		this.isCorrect = isCorrect;
		this.isDeleted = isDeleted;

	}
	
	// ===================
    // 		 GETTERS 
 	// ===================
	@Override
	public Integer getId() {return id;}
	public Integer getAuthorId() {return authorId;}
	public Integer getQuestionId() {return questionId;}
	public Integer getParentAnswerId() {return parentAnswerId;}
	@Override
	public Timestamp getCreatedTimestamp() {return createdTimestamp;}
	@Override
	public Timestamp getLastUpdatedTimestamp() {return lastUpdatedTimestamp;}
	public boolean getIsDeleted() {return isDeleted;}
	public Integer getUpvoteCount() {return upvoteCount;}
	public String getAuthorName() {return authorName;}
	public boolean isCorrect() {return isCorrect;}
	public boolean isReview() {return isReview;}
	public int getReplyDepth() {return replyDepth;}
	@Override
	public String getBodyText() { return bodyText;}
	@Override
	public String getTitleText() {return null;}
	@Override
	public Type getPostType() {return Type.Answer;}
	
	// ===================
    // 		 SETTERS 
 	// ===================
	
	public void setQuestionId(int givenId) {questionId = givenId;}
	public void setIsCorrect(boolean correct) { isCorrect = correct;}
	public void setIsReview(boolean isReview) { this.isReview = isReview;}
	public void setIsDeleted(boolean deleted) { isDeleted = deleted;}
	public void setUpvoteCount(int newCount) { upvoteCount = newCount;}
	public void setParentAnswerId(int givenId) {parentAnswerId = givenId;}
	public void setCreatedTimestamp(Timestamp newTimestamp) { createdTimestamp = newTimestamp; }
	public void setLastUpdatedTimestamp(Timestamp newTimestamp) { lastUpdatedTimestamp = newTimestamp; }
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
    	 return "Answer{id=" + id +
                 ", bodyText='" + bodyText + '\'' +
                 ", isCorrect=" + isCorrect +
                 ", parentAnswerId=" + parentAnswerId +
                 '}';
    }
}
