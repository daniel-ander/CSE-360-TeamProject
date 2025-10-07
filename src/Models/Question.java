package Models;

public class Question {

	private int id;
	private String text;
	private int authorId;
	private boolean answered;
	private boolean resolved;
	private boolean deleted;
	
/* Added class fields: author, isAnswered, isResolved, isDeleted
 * added author to constructor
 * added getters: getAuthor, isAnswered, isResolved
 * added setters: setAnswered, setResolved
 * */
	
	public Question(int id, int authorId, String text) {
		if (text == null || text.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		this.id = id;		
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
	public int getAuthorId() {return authorId;}
	public String getText() {return text;}
	public boolean getAnswered() {return answered;}
	public boolean getResolved() {return resolved;}
	public boolean getDeleted() {return deleted;}
	
	
	// ===================
    // 		SETTERS
 	// ===================
	public void setText(String newText) {
		if (newText == null || newText.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		text = newText;
	}
	
	public void setResolved(boolean resolvedStatus) {resolved = resolvedStatus;}
	public void setAnswered(boolean answeredStatus) {answered = answeredStatus;}
	public void setDeleted(boolean deletedStatus) {deleted = deletedStatus;}
	
	// ===================
    // 		  UTILS 
 	// ===================
	
	// this lets the question print out correctly
    @Override
    public String toString() {
        return "Q" + id + ": " + text;
    }
}
