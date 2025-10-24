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
    private BooleanProperty isExpanded = new SimpleBooleanProperty(false);
    private BooleanProperty isCommenting = new SimpleBooleanProperty(false);
    private BooleanProperty doesHaveChildren = new SimpleBooleanProperty(true);
    private boolean isHidden = false;
    private int depthLevel;
    
    
    private final Consumer<Integer> deleteCallback;
    private final Consumer<IPost> addCallBack;
    private final TriConsumer<Integer, String, String> updateCallBack;
    private final Runnable toggleExpand;
    

    public PostDisplayViewModel(
    		IPost post, 
    		List<String> permissionList,  
    		UserSession userSession,
    		Consumer<Integer> deleteCallBack,
    		Consumer<IPost> addCallBack,
    		TriConsumer<Integer, String, String> updateCallBack,
    		Runnable toggleExpand
    	) {
        this.post = post;
        this.permissionList = permissionList;
        this.userSession = userSession;
        this.deleteCallback = deleteCallBack;
        this.addCallBack = addCallBack;
        this.updateCallBack = updateCallBack;
        this.toggleExpand = toggleExpand;
    }
    
    // ===================
    // 		 GETTERS 
 	// ===================

    public IPost getPost() { return post; }
    public List<String> getPermissionList() { return permissionList; }
    
    public int getDepthLevel() {
    	return depthLevel;
    }

    public boolean getIsEditingQuestion() {
    	return isEditingQuestion;
    }
    
    public BooleanProperty getIsCommenting() {
    	return isCommenting;
    }
    
    public BooleanProperty getDoesHaveChildren() {
    	return doesHaveChildren;
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
    
    public boolean isExpanded() {
    	return isExpanded.get();
    }
    
    // ===================
    // 		 SETTERS 
 	// ===================
    
    public void setDoesHaveChildren(boolean doesHaveChildren) {
    	this.doesHaveChildren.set(doesHaveChildren);
    }
    
    public void setDepthLevel(int level) {
    	this.depthLevel = level;
    }
    
    public void setIsEditing(boolean isEditing) {
    	this.isEditingQuestion = isEditing;
    }
    
    public void setIsCommenting(boolean isCommenting) {
    	this.isCommenting = new SimpleBooleanProperty(isCommenting);
    }
    
    public void setIsHidden(boolean isHidden) {
    	this.isHidden = isHidden;
    }
    
    // Displaying children elements
    public void setIsExpanded(boolean isExpanded) {
    	this.isExpanded.setValue(isExpanded);
    }
    
    public BooleanProperty getExpandedProperty() {
        return isExpanded;
    }
    
    // ===================
    // 		 CRUD 
 	// ===================
    
    public void deletePost() {
    	deleteCallback.accept(post.getId());
    }
    
    public void toggleExpand() {
    	setIsExpanded(!isExpanded());
    	this.toggleExpand.run();
    }
    
    public void updatePost(String newTitle, String newBody) {
    	if(isQuestion()) {
    		updateCallBack.accept(post.getId(), newTitle, newBody);
    	} else {
    		// A bit hacky, but I didn't see a better option here
    		updateCallBack.accept(post.getId(), newBody, ((Answer)post).isCorrect() ? "yes" : "no");
    	}
    }
    
    public void addAnswer(String newTitleText, String newBodyText) {
    	Answer newAnswer;
    	if(isQuestion()) {
    		newAnswer = new Answer(post.getId(), userSession.getCurrentUserId(), userSession.getCurrentUserName(), newBodyText);
    	} else {
    		newAnswer = new Answer(((Answer)post).getQuestionId(), post.getId(), userSession.getCurrentUserId(), userSession.getCurrentUserName(), newBodyText);
    	}
    	
    	addCallBack.accept(newAnswer);
    }
}