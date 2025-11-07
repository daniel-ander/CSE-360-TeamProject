package Models;

import java.sql.Timestamp;

public interface IPost {
	enum Type {
		Question, Answer, Review
	}
	
	public Integer getId();
	public String getBodyText();
	public boolean getIsDeleted();
	public String getTitleText();
	
	public Timestamp getCreatedTimestamp();
	public Timestamp getLastUpdatedTimestamp();
	
	public Type getPostType();
}
