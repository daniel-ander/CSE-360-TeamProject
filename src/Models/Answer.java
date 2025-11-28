package Models;

import java.math.BigDecimal;
import java.sql.Timestamp;

import Models.DBModels.UserDTO;

public class Answer implements IPost {
	public static String tableName = "answers";
	
	private Integer id;
	private String bodyText;
	private boolean isCorrect;
	private Integer questionId;
	private UserDTO author;
	private Integer parentAnswerId;
	private Timestamp createdTimestamp;
	private Timestamp lastUpdatedTimestamp;
	private BigDecimal averageRating;
	private boolean isDeleted;
	private Integer upvoteCount;
	private int replyDepth;
	private boolean isReview; // indicates review-type post
	

	// Constructor for creating a new answer (not yet in DB)
	public Answer(
			int questionId, 
			int authorId, 
			String bodyText
	) {
		this(questionId, null, null, bodyText);
	}
	
	// Constructor for creating a new nested answer (not yet in DB)
	public Answer(
			int questionId, 
			Integer parentAnswerId, 
			UserDTO user, 
			String bodyText
	) {
		this(questionId, parentAnswerId, user, bodyText, false);
	}

	// New constructor that accepts a review flag
	public Answer(int questionId, 
			Integer parentAnswerId, 
			UserDTO user, 
			String bodyText, 
			boolean isReview
	) {
		this(null, questionId, parentAnswerId, user, bodyText, null, null, BigDecimal.valueOf(0), 0, false, false, isReview);
	}
	
	// Constructor for pulling from DB with all fields (new full signature)
	public Answer(
			Integer id, 
			Integer questionId, 
			Integer parentAnswerId, 
			UserDTO user, 
			String bodyText,
			Timestamp createdTimestamp,
			Timestamp lastUpdatedTimestamp, 
			BigDecimal averageRating,
			int upvoteCount,
			boolean isCorrect, 
			boolean isDeleted,
			boolean isReview
	) {
		if (bodyText == null || bodyText.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
//		System.out.println("The average for: " + id + " is: " + averageRating);

		this.id = id;
		this.questionId = questionId; 
		this.parentAnswerId = parentAnswerId; 
		this.author = user;
		this.bodyText = bodyText;
		this.createdTimestamp = createdTimestamp;
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
		this.averageRating = averageRating;
		this.upvoteCount = upvoteCount;
		this.isCorrect = isCorrect;
		this.isDeleted = isDeleted;
		this.isReview = isReview;

	}
	
	// ===================
    // 		 GETTERS 
 	// ===================
	@Override
	public Integer getId() {return id;}
	public UserDTO getAuthor() {return author;}
	public Integer getQuestionId() {return questionId;}
	public Integer getParentAnswerId() {return parentAnswerId;}
	@Override
	public Timestamp getCreatedTimestamp() {return createdTimestamp;}
	@Override
	public Timestamp getLastUpdatedTimestamp() {return lastUpdatedTimestamp;}
	public boolean getIsDeleted() {return isDeleted;}
	public boolean getIsReview() {return isReview;}
	public BigDecimal getAverageRating() {return averageRating;}
	public Integer getUpvoteCount() {return upvoteCount;}
	public boolean isCorrect() {return isCorrect;}
	public int getReplyDepth() {return replyDepth;}
	@Override
	public String getBodyText() { return bodyText;}
	@Override
	public String getTitleText() {return null;}
	@Override
	public Type getPostType() {return isReview ? Type.Review : Type.Answer;}
	
	// ===================
    // 		 SETTERS 
 	// ===================
	
	public void setIsReview(boolean isReview) { this.isReview = isReview; }
	public void setQuestionId(int givenId) {questionId = givenId;}
	public void setIsCorrect(boolean correct) { isCorrect = correct;}
	public void setIsDeleted(boolean deleted) { isDeleted = deleted;}
	public void setUpvoteCount(int newCount) { upvoteCount = newCount;}
	public void setParentAnswerId(int givenId) {parentAnswerId = givenId;}
	public void setCreatedTimestamp(Timestamp newTimestamp) { createdTimestamp = newTimestamp; }
	public void setAverageRating(BigDecimal averageRating) {this.averageRating = averageRating;}
	public void setLastUpdatedTimestamp(Timestamp newTimestamp) { lastUpdatedTimestamp = newTimestamp; }
	public void setReplyDepth(int depth) { replyDepth = depth; }
	public void setAuthor(UserDTO newUser) { this.author = newUser; }
	public void setId(int newId) { id = newId; }
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