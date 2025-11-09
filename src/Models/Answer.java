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
	private Timestamp createdTimestamp;
	private Timestamp lastUpdatedTimestamp;
	private boolean isDeleted;
	private Integer upvoteCount;
	private int replyDepth;
	private boolean isReview; // new flag to indicate review-type post
	

	// Constructor for creating a new answer (not yet in DB)
	public Answer(int questionId, int authorId, String authorName, String bodyText) {
		this(questionId, null, authorId, authorName, bodyText);
	}
	
	// Constructor for creating a new nested answer (not yet in DB)
	public Answer(int questionId, Integer parentAnswerId, int authorId, String authorName, String bodyText) {
		this(questionId, parentAnswerId, authorId, authorName, bodyText, false);
	}

	// New constructor that accepts a review flag
	public Answer(int questionId, Integer parentAnswerId, int authorId, String authorName, String bodyText, boolean isReview) {
		this(null, questionId, parentAnswerId, authorId, authorName, bodyText, null, null, null, false, false, isReview, 0);
	}
	
	// Constructor for pulling from DB with all fields (new full signature)
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
			boolean isDeleted,
			boolean isReview,
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
		
		this.isCorrect = isCorrect;
		this.isDeleted = isDeleted;
		this.isReview = isReview;

	}

	// Backwards-compatible constructor matching existing repository calls (no isReview param)
	public Answer(Integer id, Integer questionId, Integer parentAnswerId, Integer authorId, String authorName,
			String bodyText, Timestamp createdTimestamp, Timestamp lastUpdatedTimestamp, Integer upvoteCount,
			boolean isCorrect, boolean isDeleted, int replyDepth) {
		this(id, questionId, parentAnswerId, authorId, authorName, bodyText, createdTimestamp, lastUpdatedTimestamp, upvoteCount, isCorrect, isDeleted, false, replyDepth);
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
	public int getReplyDepth() {return replyDepth;}
	@Override
	public String getBodyText() { return bodyText;}
	@Override
	public String getTitleText() {return null;}
	@Override
	public Type getPostType() {return Type.Answer;}
	
	// new getter to expose review flag
	public boolean isReview() { return isReview; }
	
	// ===================
    // 		 SETTERS 
 	// ===================
	
	public void setQuestionId(int givenId) {questionId = givenId;}
	public void setIsCorrect(boolean correct) { isCorrect = correct;}
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