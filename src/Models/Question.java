package Models;

public class Question {
	private int id;
	private String text;
	
	// CREATE SECTION
	public Question(int id, String text) {
		if (text == null || text.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		this.id = id;
		this.text = text;
	}
	
	// READ SECTION
	public int getId() {return id;}
	public String getText() {return text;}
	
	// UPDATE SECTION
	public void setText(String newText) {
		if (newText == null || newText.strip().isEmpty()) {
			throw new IllegalArgumentException("Text is empty");
		}
		text = newText;
	}
	
	// this lets the question print out correctly
    @Override
    public String toString() {
        return "Q" + id + ": " + text;
    }
}
