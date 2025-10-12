package Models;

public class Question {
	public static String tableName = "questions";
	
	private int id; // -1 if question isn't in the database yet
	private String text;
	private String title;
	private int authorId;
	private int upvoteCount;
	private boolean isAnswered;
	private boolean isResolved;
	private boolean isDeleted;
	
/* Added class fields: author, isAnswered, isResolved, isDeleted
 * added author to constructor
 * added getters: getAuthor, isAnswered, isResolved
 * added setters: setAnswered, setResolved
 * */
	// Constructor for when we have all the data (e.g., when retrieving from DB)
	public Question(int id, int authorId, String title, String text, boolean answered, boolean resolved, boolean deleted, int upvoteCount) {
		if (text == null || text.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		this.id = id;
		this.title = title;
		this.text = text;
		this.upvoteCount = upvoteCount;
		this.authorId = authorId;
		this.isAnswered = answered;
		this.isResolved = resolved;
		this.isDeleted = deleted;
	}
	
	
	public Question(int authorId, String title, String text) {
		if (text == null || text.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		this.id = -1;
		this.title = title;
		this.text = text;
		
		this.authorId = authorId;
		this.isAnswered = false;
		this.isResolved = false;
		this.isDeleted = false;

	}
	
	// ===================
    // 		GETTERS
 	// ===================
	
	public int getId() {return id;}
	public String getTitle() {return title;}
	public int getAuthorId() {return authorId;}
	public String getText() {return text;}
	public boolean getAnsweredStatus() {return isAnswered;}
	public boolean getResolvedStatus() {return isResolved;}
	public boolean getDeletedStatus() {return isDeleted;}
	public int getUpvoteCount() {return upvoteCount;}
	
	
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
