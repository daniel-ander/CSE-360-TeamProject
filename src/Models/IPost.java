package Models;

public interface IPost {
	enum Type {
		Question, Answer
	}
	
	public Integer getId();
	public String getBodyText();
	public String getTitleText();
	
	public Type getPostType();
}
