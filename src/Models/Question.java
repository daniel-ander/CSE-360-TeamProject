package Models;

public class Question {

	private int id; // -1 if question isn't in the database yet
	private String text;
	private String title;
	private int authorId;
	private int upvoteCount;
	private boolean answered;
	private boolean resolved;
	private boolean deleted;
	
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
		this.answered = answered;
		this.resolved = resolved;
		this.deleted = deleted;
	}
	
	
	public Question(int authorId, String title, String text) {
		if (text == null || text.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		this.id = -1;
		this.title = title;
		this.text = text;
		
		this.authorId = authorId;
		this.answered = false;
		this.resolved = false;
		this.deleted = false;

	}
	
	// ===================
    // 		GETTERS
 	// ===================
	
	public int getId() {return id;}
	public String getTitle() {return title;}
	public int getAuthorId() {return authorId;}
	public String getText() {return text;}
	public boolean getAnswered() {return answered;}
	public boolean getResolved() {return resolved;}
	public boolean getDeleted() {return deleted;}
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
	public void setResolved(boolean resolvedStatus) {resolved = resolvedStatus;}
	public void setAnswered(boolean answeredStatus) {answered = answeredStatus;}
	public void setDeleted(boolean deletedStatus) {deleted = deletedStatus;}
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
