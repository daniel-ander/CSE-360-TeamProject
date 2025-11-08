package Controllers.Messages;

import Models.Message;
import Database.Services.MessageService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Handles the data and logic for the right side of the Messages page.
 * 
 * Each MessagesViewModel represents a single message thread originating from a review (an Answer marked as isReview).
 * It interacts with the MessageService to retrieve and send messages tied to that review.
 * 
 * Data:
 *  - Populates from the Message table.
 *  - Each reply in the thread is a Message record with review_id referencing the review Answer.
 * 
 * Structure:
 *  - Holds an ObservableList<MessageViewModel> for the thread messages.
 *  - Updates when a new message is sent or when the selected review changes.
 * 
 * Display:
 *  - The visual layout of each message is handled by MessageCell.
 */


public class MessagesViewModel {
	

}
