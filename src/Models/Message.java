package Models;

import java.sql.Timestamp;

public class Message {
	public static String tableName = "messages";
	
	private Integer id;
	private Integer senderId;
	private Integer receiverId;
	private String content;
	private Timestamp sentTimestamp;

	// Non-DB constructor
	public Message(Integer senderId, Integer receiverId, String content) {
		this(null, senderId, receiverId, content, null);
	}
		
	// Constructor for when we have all the data (e.g., when retrieving from DB)
	public Message(
			Integer id, 
			Integer senderId, 
			Integer receiverId, 
			String content, 
			Timestamp sentTimestamp
	) {
		if (content == null || content.strip().isEmpty()) {
			throw new IllegalArgumentException("Content is empty");
		}
		this.id = id;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.content = content;
		this.sentTimestamp = sentTimestamp;
	}
	
	// ===================
	// Getters
	// ===================
	
	public Integer getId() {
		return id;
	}

	public Integer getSenderId() {
		return senderId;
	}

	public Integer getReceiverId() {
		return receiverId;
	}

	public String getContent() {
		return content;
	}

	public Timestamp getSentTimestamp() {
		return sentTimestamp;
	}
	
	// ===================
	// 		SETTERS
	// ===================
	
	public void setContent(String content) {
		if (content == null || content.strip().isEmpty()) {
			throw new IllegalArgumentException("Content is empty");
		}
		this.content = content;
	}
	
	public void setSentTimestamp(Timestamp sentTimestamp) {
		this.sentTimestamp = sentTimestamp;
	}
	
	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
	}
	
	public void setSenderId(Integer senderId) {
		this.senderId = senderId;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
}
