package Models;

import java.sql.Timestamp;

public class Question implements IPost {
	public static String tableName = "questions";
	
	private Integer id;
	private String text;
	private String title;
	private int authorId;
	
	private Timestamp createdTimestamp;
	private Timestamp lastUpdatedTimestamp;

	private int upvoteCount;
	private boolean isAnswered;
	private boolean isResolved;
	private boolean isDeleted;
	
/* Added class fields: author, isAnswered, isResolved, isDeleted
 * added author to constructor
 * added getters: getAuthor, isAnswered, isResolved
 * added setters: setAnswered, setResolved
 * */
	
	// Non-DB constructor
	public Question(int authorId, String title, String text) {
		this(null, authorId, title, text, null, null, false, false, false, 0);
	}
		
	// Constructor for when we have all the data (e.g., when retrieving from DB)
	public Question(
			Integer id, 
			int authorId, 
			String title, 
			String text, 
			Timestamp createdTimestamp,
			Timestamp lastUpdatedTimestamp, 
			boolean answered, 
			boolean resolved, 
			boolean deleted, 
			int upvoteCount
	) {
		if (text == null || text.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		this.id = id;
		this.title = title;
		this.text = text;
		this.createdTimestamp = createdTimestamp;
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
		this.upvoteCount = upvoteCount;
		this.authorId = authorId;
		this.isAnswered = answered;
		this.isResolved = resolved;
		this.isDeleted = deleted;
	}
	
	// ===================
    // 		GETTERS
 	// ===================
	
	public Integer getId() {return id;}

	public Integer getAuthorId() {return authorId;}
	public boolean getAnsweredStatus() {return isAnswered;}
	public boolean getResolvedStatus() {return isResolved;}
	public boolean getIsDeleted() {return isDeleted;}
	public int getUpvoteCount() {return upvoteCount;}
	
	@Override
	public String getBodyText() {
		return text;
	}

	@Override
	public String getTitleText() {
		return title;
	}


	@Override
	public Type getPostType() {
		return Type.Question;
	}

	@Override
	public Timestamp getCreatedTimestamp() {
		return createdTimestamp;
	}
	
	@Override
	public Timestamp getLastUpdatedTimestamp() {
		return lastUpdatedTimestamp;
	}
	
	
	// ===================
    // 		SETTERS
 	// ===================
	public void setText(String newText) {
		if (newText == null || newText.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		text = newText;
	}
	
	public void setId(int newId) { this.id = newId; }
	public void setResolved(boolean resolvedStatus) {isResolved = resolvedStatus;}
	public void setAnswered(boolean answeredStatus) {isAnswered = answeredStatus;}
	public void setDeleted(boolean deletedStatus) {isDeleted = deletedStatus;}
	public void setTitle(String newTitle) { this.title = newTitle; }
	public void setUpvoteCount(int newCount) { this.upvoteCount = newCount; }
	
	// ===================
    // 		  UTILS 
 	// ===================
	
	// this lets the question print out correctly
    @Override
    public String toString() {
        return "Q" + id + ": " + text;
    }

}