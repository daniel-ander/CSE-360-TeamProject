package Controllers;

import java.util.List;
import java.util.function.Consumer;

import Models.Answer;
import Models.IPost;
import Models.Question;
import Sessions.UserSession;
import Utils.TriConsumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class PostDisplayViewModel {
    private IPost post;
    private List<String> permissionList; // fetched from a service in the controller
  
    private final UserSession userSession;
    private boolean isEditingQuestion = false;
    private BooleanProperty isCommenting = new SimpleBooleanProperty(false);
    private boolean isHidden = false;
    
    
    private final Consumer<Integer> deleteCallback;
    private final Consumer<IPost> addCallBack;
    private final TriConsumer<Integer, String, String> updateCallBack;
    private final Runnable refreshEntries;
    

    public PostDisplayViewModel(
    		IPost post, 
    		List<String> permissionList,  
    		UserSession userSession,
    		Consumer<Integer> deleteCallBack,
    		Consumer<IPost> addCallBack,
    		TriConsumer<Integer, String, String> updateCallBack,
    		Runnable refreshEntries
    	) {
        this.post = post;
        this.permissionList = permissionList;
        this.userSession = userSession;
        this.deleteCallback = deleteCallBack;
        this.addCallBack = addCallBack;
        this.updateCallBack = updateCallBack;
        this.refreshEntries = refreshEntries;
    }
    
    // ===================
    // 		 GETTERS 
 	// ===================

    public IPost getPost() { return post; }
    public List<String> getPermissionList() { return permissionList; }

    public boolean getIsEditingQuestion() {
    	return isEditingQuestion;
    }
    
    public BooleanProperty getIsCommenting() {
    	return isCommenting;
    }
    
    public String getMainLabelText() {
    	try {
    		return ((Question)post).getTitleText();
    	} catch (ClassCastException e) {
    		return null;
    	}
    }
    
    public boolean isQuestion() {
    	return post.getPostType() == IPost.Type.Question;
    }
    
    public boolean getIsDeleted() {
    	return post.getIsDeleted();
    }
    
    public boolean getIsHidden() {
    	return isHidden;
    }
    
    // ===================
    // 		 SETTERS 
 	// ===================
    
    public void setIsEditing(boolean isEditing) {
    	this.isEditingQuestion = isEditing;
    }
    
    public void setIsCommenting(boolean isCommenting) {
    	this.isCommenting = new SimpleBooleanProperty(isCommenting);
    }
    
    public void setIsHidden(boolean isHidden) {
    	this.isHidden = isHidden;
    }
    
    // ===================
    // 		 CRUD 
 	// ===================
    
    public void deletePost() {
    	deleteCallback.accept(post.getId());
    	
    	refreshEntries.run();
    }
    
    public void updatePost(String newTitle, String newBody) {
    	if(isQuestion()) {
    		updateCallBack.accept(post.getId(), newTitle, newBody);
    	} else {
    		// A bit hacky, but I didn't see a better option here
    		updateCallBack.accept(post.getId(), newBody, ((Answer)post).isCorrect() ? "yes" : "no");
    	}
    	
    	refreshEntries.run();
    }
    
    public void addAnswer(String newTitleText, String newBodyText) {
    	Answer newAnswer;
    	if(isQuestion()) {
    		newAnswer = new Answer(post.getId(), userSession.getCurrentUserId(), userSession.getCurrentUserName(), newBodyText);
    	} else {
    		newAnswer = new Answer(((Answer)post).getQuestionId(), post.getId(), userSession.getCurrentUserId(), userSession.getCurrentUserName(), newBodyText);
    	}
    	
    	addCallBack.accept(newAnswer);
    	
    	refreshEntries.run();
    }
}