package Controllers;

import Models.Answer;
import Models.Question;

import Sessions.UserSession;

import UI.Elements.QuestionListView;
import UI.Elements.AnswerListView;
import Utils.Debug;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Database.DBConnectionStore;
import Database.Services.Interfaces.IAnswerService;
import Exceptions.DBExceptions.DataAccessException;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AnswerListViewController {
    private final DBConnectionStore dbConnectionStore;
	private final AnswerListView view;
    private final Question question;
    private final IAnswerService answerService;
    private final Stage primaryStage;
    private final UserSession userSession;

    public AnswerListViewController(
            DBConnectionStore dBConnectionStore,
            Stage primaryStage, 
            Question question,
            UserSession userSession,
            AnswerListView view
    ) {
        this.dbConnectionStore = dBConnectionStore;
        this.answerService = dBConnectionStore.getAnswerService();
        
        this.primaryStage = primaryStage;
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
    		if (Debug.isEnabled) System.out.println("Answers refreshed.");
    	} catch (DataAccessException exception) {
    		// TODO: - Deal with answer not being able to be fetched
    		System.err.println("Database error: " + exception.getMessage());
            exception.printStackTrace();
    	}
    }
    
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
				if (Debug.isEnabled) System.out.println("Answers searched with term: " + searchTerm);
			} catch (DataAccessException exception) {
			System.err.println("Database error: " + exception.getMessage());
			exception.printStackTrace();
			}
    	});
    }
    
    private void setAddAnswerButtonAction() {
        // Add answer
        view.getAddAnswerButton().setOnAction(_ -> {
        	if (Debug.isEnabled) System.out.println("Add Answer button clicked.");
        	String text = view.getTextInputArea().getText();
        	if (text == null || text.trim().isEmpty() || text.length() > 500 || text.equals("Input text here...")) {
        		System.out.println("Answer text cannot be empty or placeholder.");
        		return;
        	}
        	Answer a = new Answer(question.getId(), userSession.currentUser().getId(), text);
        	try {
        		answerService.addAnswer(a);
                refreshAnswers();
                if (Debug.isEnabled) System.out.println("Added answer: " + text);
        	} catch (DataAccessException dataAccessException) {
        		// TODO: - Deal with answer not being able to be added
                System.err.println("Database error: " + dataAccessException.getMessage());
                dataAccessException.printStackTrace();
            }
        });
		
	}
    
    private void setShowDeletedAnswersCheckboxAction() {
		view.getShowDeletedAnswersCheckbox().setOnAction(_ -> {
			refreshAnswers();
		});
	}
    
    //TODO: - We're gonna need to figure out how to organize the replies to answers visually
    //      - Whether a nested list view or a tree view or something else. 
    // 		- For now this just adds a reply as a normal answer with a parentAnswerId set.
    
    private void setReplyToAnswerButtonAction() {
    	// Reply to an answer
    	view.getReplyToAnswerButton().setOnAction(_-> {
    		if (Debug.isEnabled) System.out.println("Reply to Answer button clicked.");
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
				if (Debug.isEnabled) System.out.println("Added reply Id#" + reply.getId() + "with parentAnswerId#" + reply.getParentAnswerId() + ": " + text);
				
			} catch (DataAccessException dataAccessException) {
				
			}
    		
    	});
    	
    }
    private void setEditAnswerButtonAction() {
        // Edit answer
        view.getEditAnswerButton().setOnAction(_ -> {
            Answer sel = view.getAnswerList().getSelectionModel().getSelectedItem();
            if (sel != null) {
                sel.setText("Edited: " + sel.getText());
                refreshAnswers();
            }
        });
    	
    }
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
  
    private void setBackButtonAction() {
        // Back to ForumListView (new scene, same as other buttons)
        view.getBackButton().setOnAction(_ -> {
            QuestionListView forumView = new QuestionListView();

            new QuestionListViewController(dbConnectionStore, primaryStage, userSession, forumView);

            Scene forumScene = new Scene(forumView.getLayout(), 800, 600);
            primaryStage.setScene(forumScene);
            primaryStage.setTitle("Forum Page");
        });
    }
  
    private void setResolveButtonAction() {
        // Mark question as resolved
        view.getResolveButton().setOnAction(_ -> {
			  question.setResolved(true);
			  view.getQuestionLabel().setText("[" + question.getId() + "] " + question.getText() + " (Resolved)");
			  question.setText(question.getText() + " (Resolved)");
		  });
		
	  }
    
    private void calculateDepths(List<Answer> answers) {
        Map<Integer, Integer> depthMap = new HashMap<>();
        Map<Integer, Answer> answerById = new HashMap<>();
        for (Answer a : answers) {
        	answerById.put(a.getId(), a);
        	if (Debug.isEnabled) {
        		System.out.println("Answer ID " + a.getId() + " with parent ID " + a.getParentAnswerId());
        	}
        }

        for (Answer answer : answers) {
            int depth = 0;
            int parentId = answer.getParentAnswerId();
            if (Debug.isEnabled) {
				System.out.println("Calculating depth for answer ID " + answer.getId() + " with parent ID " + parentId);
			}

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
            if (Debug.isEnabled) {
                System.out.println("replyDepth for answer ID " + answer.getId() + " set to " + depth);
            }
        }
    }

    
    
    
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
    
    private void setInitialLayout() {
    	// Show selected question at the top
    	view.getQuestionLabel().setText("[" + question.getId() + "] " + question.getText());
    }


}
