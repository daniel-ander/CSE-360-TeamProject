package Models;

import java.sql.Timestamp;

public class Message {
	public static String tableName = "messages";
	
	private Integer messageId;
	private Integer reviewId;
	private Integer senderId;
	private Integer receiverId;
	private Integer parentMessageId;
	private String messageText;
	private Timestamp sentTimestamp;

	// Non-DB constructor
	public Message(Integer senderId, Integer reviewId, Integer receiverId,
	               Integer parentMessageId, String messageText) {
	    this(null, reviewId, senderId, receiverId, parentMessageId, messageText, null);
	}
	// Constructor for when we have all the data (e.g., when retrieving from DB)
	public Message(Integer messageId, Integer reviewId, Integer senderId,
            Integer receiverId, Integer parentMessageId,
            String messageText, Timestamp sentTimestamp) {
	 this.messageId = messageId;
	 this.reviewId = reviewId;
	 this.senderId = senderId;
	 this.receiverId = receiverId;
	 this.parentMessageId = parentMessageId;
	 this.messageText = messageText;
	 this.sentTimestamp = sentTimestamp;
}
	
	// ===================
	// Getters
	// ===================
	
	public Integer getId() {
		return messageId;
	}
	
	public Integer getReviewId() {
		return reviewId;
	}

	public Integer getSenderId() {
		return senderId;
	}

	public Integer getReceiverId() {
		return receiverId;
	}
	
	public Integer getParentMessageId() {
		return parentMessageId;
	}

	public String getContent() {
		return messageText;
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
		this.messageText = content;
	}
	
	public void setReviewId(Integer reviewId) {
		this.reviewId = reviewId;
	}
	
	public void setParentMessageId(Integer parentMessageId) {
		this.parentMessageId = parentMessageId;
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
		this.messageId = id;
	}
}
