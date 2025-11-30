package Models;

import java.sql.Timestamp;

import Models.DBModels.UserDTO;

public interface IPost {
	enum Type {
		Question, Answer, Review
	}
	
	public UserDTO getAuthor();
	public Integer getId();
	public String getBodyText();
	public boolean getIsDeleted();
	public String getTitleText();
	
	public Timestamp getCreatedTimestamp();
	public Timestamp getLastUpdatedTimestamp();
	
	public Type getPostType();
}
