package Controllers;

import Models.Answer;
import Models.Question;

import Sessions.UserSession;


import UI.Elements.AnswerListView;
import Utils.Debug;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Database.DBConnectionStore;
import Database.Services.Interfaces.IAnswerService;
import Exceptions.DBExceptions.DataAccessException;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The AnswerListViewController class manages the display and interaction
 * with answers for a specific question. It handles adding, editing, deleting,
 * replying, searching, and refreshing answers, as well as marking questions resolved.
 */
public class AnswerListViewController {

	private final AnswerListView view;
    private final Question question;
    private final IAnswerService answerService;

    private final UserSession userSession;

    /**
     * Creates a new AnswerListViewController.
     * Initializes the layout, sets up actions, and loads answers for the given question.
     */
    public AnswerListViewController(
            DBConnectionStore dBConnectionStore,
            Stage primaryStage, 
            Question question,
            UserSession userSession,
            AnswerListView view
    ) {
        this.answerService = dBConnectionStore.getAnswerService();
        
		this.view = view;
        this.question = question;

        this.userSession = userSession;
		setInitialLayout();
		    
        setUpAction();
        refreshAnswers();
    }
    
    // ===================
    // 		 SETTERS 
 	  // ===================
    
    /**
     * Refreshes the list of answers for the current question.
     * Filters out deleted answers if the checkbox is not selected.
     */
    private void refreshAnswers() {
    	try {
    		List<Answer> allAnswers = answerService.getAllAnswersForQuestion(question.getId());
    		
    		if (!view.getShowDeletedAnswersCheckbox().isSelected()) {
    		    allAnswers.removeIf(Answer::getIsDeleted);
    		}

    		
    		for (Answer a : allAnswers) {
    		    System.out.printf("Fetched answer ID %d parent=%d%n", a.getId(), a.getParentAnswerId());
    		}
    		calculateDepths(allAnswers);
    		view.getAnswerList().setItems(
    				FXCollections.observableArrayList(allAnswers));
    		Debug.write("Answers refreshed.");
    	} catch (DataAccessException exception) {
    		// TODO: - Deal with answer not being able to be fetched
    		System.err.println("Database error: " + exception.getMessage());
            exception.printStackTrace();
    	}
    }
    
    /**
     * Configures the search button action.
     * Allows searching answers by text and updates the list view.
     */
    private void setSearchAnswersButtonAction() {
    	view.getSearchAnswersButton().setOnAction(_ -> {
    		String searchTerm = view.getSearchAnswersTextField().getText().toLowerCase();
    		if (searchTerm == null || searchTerm.trim().isEmpty()) {
				refreshAnswers();
				return;
			}
			try {
				List<Answer> searchResults = answerService.getAnswersBySearch(searchTerm);
				
				if (!view.getShowDeletedAnswersCheckbox().isSelected()) {
				    searchResults.removeIf(Answer::getIsDeleted);
				}
				
				calculateDepths(searchResults);
				view.getAnswerList().setItems(
						FXCollections.observableArrayList(searchResults));
				Debug.write("Answers searched with term: " + searchTerm);
			} catch (DataAccessException exception) {
				System.err.println("Database error: " + exception.getMessage());
				exception.printStackTrace();
			}
    	});
    }
    
    /**
     * Configures the add answer button action.
     * Validates input and adds a new answer to the database.
     */
    private void setAddAnswerButtonAction() {
        // Add answer
        view.getAddAnswerButton().setOnAction(_ -> {
        	Debug.write("Add Answer button clicked.");
        	String text = view.getTextInputArea().getText();
        	if (text == null || text.trim().isEmpty() || text.length() > 500 || text.equals("Input text here...")) {
        		System.out.println("Answer text cannot be empty or placeholder.");
        		return;
        	}
        	Answer a = new Answer(question.getId(), userSession.currentUser().getId(), text);
        	try {
        		answerService.addAnswer(a);
                refreshAnswers();
                Debug.write("Added answer: " + text);
        	} catch (DataAccessException dataAccessException) {
        		// TODO: - Deal with answer not being able to be added
                System.err.println("Database error: " + dataAccessException.getMessage());
                dataAccessException.printStackTrace();
            }
        });
		
	}
    
    /**
     * Configures the checkbox action for showing deleted answers.
     * Refreshes the list when toggled.
     */
    private void setShowDeletedAnswersCheckboxAction() {
		view.getShowDeletedAnswersCheckbox().setOnAction(_ -> {
			
			refreshAnswers();
		});
	}
    
    /**TODO: - We're gonna need to figure out how to organize the replies to answers visually
    *      - Whether a nested list view or a tree view or something else. 
    * 		- For now this just adds a reply as a normal answer with a parentAnswerId set.
    */
    private void setReplyToAnswerButtonAction() {
    	// Reply to an answer
    	view.getReplyToAnswerButton().setOnAction(_-> {
    		Debug.write("Reply to Answer button clicked.");
    		String text = view.getTextInputArea().getText();
    		if (text == null || text.trim().isEmpty() || text.length() > 500 || text.equals("Input text here...")) {
				System.out.println("Answer text cannot be empty or placeholder.");
				return;
			}
    		Answer selectedAnswer = view.getAnswerList().getSelectionModel().getSelectedItem();
			if (selectedAnswer == null) {
				System.out.println("Select an answer to reply to.");
				return;
			}
			Answer reply = new Answer(question.getId(), userSession.currentUser().getId(), text);
			reply.setParentAnswerId(selectedAnswer.getId());
			try {
				answerService.addAnswer(reply);
				refreshAnswers();
				Debug.write("Added reply Id#" + reply.getId() + "with parentAnswerId#" + reply.getParentAnswerId() + ": " + text);
				
			} catch (DataAccessException dataAccessException) {
				
			}
    		
    	});
    	
    }
    
    /**
     * Configures the edit button action.
     * Updates the selected answer text.
     */
    private void setEditAnswerButtonAction() {
        // Edit answer
        view.getEditAnswerButton().setOnAction(_ -> {
            Answer sel = view.getAnswerList().getSelectionModel().getSelectedItem();
            if (sel != null) {
                sel.setText("Edited: " + sel.getBodyText());
                refreshAnswers();
            }
        });
    	
    }
    
    /**
     * Configures the delete button action.
     * Deletes the selected answer from the database.
     */
    private void setDeleteAnswerButtonAction() {
        // Delete answer
        view.getDeleteAnswerButton().setOnAction(_ -> {
            Answer sel = view.getAnswerList().getSelectionModel().getSelectedItem();
            if (sel != null) {
            	try {
            		answerService.deleteAnswer(sel.getId());
            		refreshAnswers();
            	} catch (DataAccessException exception) {
            		// TODO: - Deal with answer not being able to be deleted
                    System.err.println("Database error: " + exception.getMessage());
                    exception.printStackTrace();
                }
                
            }
        });
    }
  
    /**
     * Configures the back button action.
     * Intended to navigate back to the forum view.
     */
    private void setBackButtonAction() {
        // Back to ForumListView (new scene, same as other buttons)
        view.getBackButton().setOnAction(_ -> {
//            QuestionListView forumView = new QuestionListView();
//
//            new QuestionListViewController(
//            		dbConnectionStore, 
//            		primaryStage, 
//            		userSession, 
//            		new PostListViewModel(dbConnectionStore.getQuestionService(), answerService, userSession), 
//            		forumView
//            );
//
//            Scene forumScene = new Scene(forumView.getLayout(), 800, 600);
//            primaryStage.setScene(forumScene);
//            primaryStage.setTitle("Forum Page");
        });
    }
  
    /**
     * Configures the resolve button action.
     * Marks the current question as resolved and updates the label.
     */
    private void setResolveButtonAction() {
        // Mark question as resolved
        view.getResolveButton().setOnAction(_ -> {
			  question.setResolved(true);
			  view.getQuestionLabel().setText("[" + question.getId() + "] " + question.getBodyText() + " (Resolved)");
			  question.setText(question.getBodyText() + " (Resolved)");
		  });
		
	  }
    
    /**
     * Calculates the depth of replies for each answer.
     * Helps organize nested replies visually.
     */
    private void calculateDepths(List<Answer> answers) {
        Map<Integer, Integer> depthMap = new HashMap<>();
        Map<Integer, Answer> answerById = new HashMap<>();
        for (Answer a : answers) {
        	answerById.put(a.getId(), a);
        	Debug.write("Answer ID " + a.getId() + " with parent ID " + a.getParentAnswerId());
        }

        for (Answer answer : answers) {
            int depth = 0;
            int parentId = answer.getParentAnswerId();
            Debug.write("Calculating depth for answer ID " + answer.getId() + " with parent ID " + parentId);

            while (parentId > 0 && depth < 10) { // Prevent infinite loops
                depth++;
                Integer knownDepth = depthMap.get(parentId);
                if (knownDepth != null) {
                    depth += knownDepth;
                    break;
                }
                Answer parentAnswer = answerById.get(parentId);
                if (parentAnswer == null) break;
                parentId = parentAnswer.getParentAnswerId();
            }
            depthMap.put(answer.getId(), depth);
            answer.setReplyDepth(depth);
            System.out.println("replyDepth for answer ID " + answer.getId() + " set to " + depth);
        }
    }

    
    
    /**
     * Sets up all button and checkbox actions for the view.
     */
    private void setUpAction() {
    	setAddAnswerButtonAction();
    	setEditAnswerButtonAction();
    	setDeleteAnswerButtonAction();
    	setBackButtonAction();
    	setResolveButtonAction();
    	setReplyToAnswerButtonAction();
    	setShowDeletedAnswersCheckboxAction();
    	setSearchAnswersButtonAction();
    }
    
    /**
     * Sets up initial layout for the page
     */
    private void setInitialLayout() {
    	// Show selected question at the top
    	view.getQuestionLabel().setText("[" + question.getId() + "] " + question.getBodyText());
    }


}
