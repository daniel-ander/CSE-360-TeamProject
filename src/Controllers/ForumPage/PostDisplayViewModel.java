package Controllers.ForumPage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import Models.Answer;
import Models.IPost;
import Models.IPost.Type;
import Models.Question;
import Sessions.UserSession;
import Utils.TriConsumer;

import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class PostDisplayViewModel {
	private IPost post;
	private List<String> permissionList; // fetched from a service in the controller

	private final UserSession userSession;
	private final BooleanProperty isEditingPostProperty = new SimpleBooleanProperty(false);

	private BooleanProperty isExpandedProperty = new SimpleBooleanProperty(false);
	private BooleanProperty isCommentingProperty = new SimpleBooleanProperty(false);
	private BooleanProperty isDeletedProperty = new SimpleBooleanProperty(false);
	private BooleanProperty doesHaveChildrenProperty = new SimpleBooleanProperty(true);
	private BooleanProperty isReviewingProperty = new SimpleBooleanProperty(false);
	private ObjectProperty<FilterType> filterProperty = new SimpleObjectProperty<>(FilterType.TIME);

	private StringProperty labelTextProperty = new SimpleStringProperty();
	private StringProperty bodyTextProperty = new SimpleStringProperty();

	private int depthLevel;

	private final Consumer<Integer> deleteCallback;
	private final Consumer<IPost> addCallBack;
	private final TriConsumer<Integer, String, String> updateCallBack;
	private final Runnable toggleExpandCallback;

	public enum FilterType {
		TIME
	}

	public PostDisplayViewModel(IPost post, List<String> permissionList, UserSession userSession,
			Consumer<Integer> deleteCallBack, Consumer<IPost> addCallBack,
			TriConsumer<Integer, String, String> updateCallBack, Runnable toggleExpandCallback) {
		this.post = post;
		this.permissionList = permissionList;
		this.userSession = userSession;
		this.deleteCallback = deleteCallBack;
		this.addCallBack = addCallBack;
		this.updateCallBack = updateCallBack;
		this.toggleExpandCallback = toggleExpandCallback;

		bodyTextProperty.set(post.getBodyText());

		if (isQuestion()) {
			labelTextProperty.set(((Question) post).getTitleText());
		}

	}

	// ===================
	// GETTERS
	// ===================

	public IPost getPost() {
		return post;
	}

	public List<String> getPermissionList() {
		return permissionList;
	}

	public int getDepthLevel() {
		return depthLevel;
	}

	public BooleanProperty getIsEditingPostProperty() {
		return isEditingPostProperty;
	}

	public BooleanProperty getIsReviewingProperty() {
		return isReviewingProperty;
	}

	public FilterType getFilterType() {
		return filterProperty.get();
	}

	public BooleanProperty getIsCommentingProperty() {
		return isCommentingProperty;
	}

	public BooleanProperty getDoesHaveChildren() {
		return doesHaveChildrenProperty;
	}

	public BooleanProperty getIsDeletedProperty() {
		return isDeletedProperty;
	}

	public String getMainLabelText() {
		try {
			return ((Question) post).getTitleText();
		} catch (ClassCastException e) {
			return null;
		}
	}

	public StringProperty getLabelTextProperty() {
		return labelTextProperty;
	}

	public StringProperty getBodyTextProperty() {
		return bodyTextProperty;
	}

	public boolean isQuestion() {
		return post.getPostType() == IPost.Type.Question;
	}

	public boolean getIsDeleted() {
		return post.getIsDeleted();
	}

	public boolean isExpandButtonPressed() {
		return isExpandedProperty.get();
	}
	
	// Properties that cannot be active at once
	private List<BooleanProperty> nonConcurrentProperties() {
		return new ArrayList<BooleanProperty>(List.of(isReviewingProperty, isCommentingProperty));
	}

	// ===================
	// SETTERS
	// ===================

	public void setDoesHaveChildrenProperty(boolean doesHaveChildren) {
		this.doesHaveChildrenProperty.set(doesHaveChildren);
	}

	public void setFilterType(FilterType newFilterType) {
		this.filterProperty.set(newFilterType);
	}

	public void setDepthLevel(int level) {
		this.depthLevel = level;
	}

	public void setIsEditingProperty(boolean isEditing) {
		this.isEditingPostProperty.set(isEditing);
	}

	public void setIsReviewingProperty(boolean isReviewing) {
		resetNonConcurrentActions();
		this.isReviewingProperty.set(isReviewing);
	}

	public void setIsCommentingProperty(boolean isCommenting) {
		resetNonConcurrentActions();
		this.isCommentingProperty.set(isCommenting);
	}

	public void setIsDeletedProperty(boolean isDeleted) {
		this.isDeletedProperty.set(isDeleted);
	}

	// Displaying children elements
	public void setIsExpanded(boolean isExpanded) {
		this.isExpandedProperty.setValue(isExpanded);
	}

	public void setLabelTextProperty(String newLabelText) {
		labelTextProperty.set(newLabelText);
	}

	public void setBodyTextProperty(String newBodyText) {
		bodyTextProperty.set(newBodyText);
	}

	public BooleanProperty getExpandedProperty() {
		return isExpandedProperty;
	}

	// ===================
	// CRUD
	// ===================

	public void deletePost() {
		setIsDeletedProperty(!getIsDeletedProperty().get());
		deleteCallback.accept(post.getId());
	}

	public void toggleExpand() {
		setIsExpanded(!isExpandButtonPressed());
		this.toggleExpandCallback.run();
	}

	public void updatePost(String newTitle, String newBody) {
		if (isQuestion()) {
			updateCallBack.accept(post.getId(), newTitle, newBody);
		} else {
			// A bit hacky, but I didn't see a better option here
			updateCallBack.accept(post.getId(), newBody, ((Answer) post).isCorrect() ? "yes" : "no");
		}
	}

	public void addAnswer(String newTitleText, String newBodyText, IPost.Type answerType) {
		IPost.Type currentPostType = post.getPostType();
		
		int questionId;
		Integer parentAnswerId;
		
		if(currentPostType == Type.Question) {
			questionId = post.getId();
			parentAnswerId = null;
		} else {
			questionId = ((Answer) post).getQuestionId();
			parentAnswerId = post.getId();
		}
		
		boolean isReview;
		if(answerType == Type.Answer) {
			isReview = false;
		} else {
			isReview = true;
		}
		
		Answer newAnswer = new Answer(
				questionId, 
				parentAnswerId, 
				userSession.getCurrentUserId(),
				userSession.getCurrentUserName(), 
				newBodyText,
				isReview
		);

		addCallBack.accept(newAnswer);
	}
	
	// ===================
	// HELPERS
	// ===================
	
	// Properties that cannot be active at the same time. 
	// IE: Commenting and Reviewing windows cannot both be open at once
	private void resetNonConcurrentActions() {
		nonConcurrentProperties().forEach(property -> {
			property.set(false);
		});
	}
}