package Controllers.ForumPage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import Config.AppCache;
import Database.Services.Interfaces.IAnswerService;
import Database.Services.Interfaces.IUserService;
import Exceptions.DBExceptions.DataAccessException;
import Models.Answer;
import Models.IPost;
import Models.IPost.Type;
import Models.Question;
import Sessions.UserSession;
import Utils.TriConsumer;

import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class PostDisplayViewModel {
	private IPost post;
	private List<String> permissionList; // fetched from a service in the controller

	private final UserSession userSession;
	private final BooleanProperty isEditingPostProperty = new SimpleBooleanProperty(false);
	
	private final IUserService userService;
	private final IAnswerService answerService;

	private final BooleanProperty isExpandedProperty = new SimpleBooleanProperty(false);
	private final BooleanProperty isCommentingProperty = new SimpleBooleanProperty(false);
	private final BooleanProperty isDeletedProperty = new SimpleBooleanProperty(false);
	private final BooleanProperty doesHaveChildrenProperty = new SimpleBooleanProperty(true);
	private final BooleanProperty isReviewingProperty = new SimpleBooleanProperty(false);
	private final BooleanProperty shouldDisplayFavoredReviewerButtonProperty = new SimpleBooleanProperty(false);
	private final ObjectProperty<FilterType> filterProperty = new SimpleObjectProperty<>(FilterType.TIME);
	private final ObjectProperty<BigDecimal> ratingProperty = new SimpleObjectProperty<>(BigDecimal.ZERO);
	
	private final IntegerProperty upvoteCountProperty = new SimpleIntegerProperty(0);
	private final ObjectProperty<BigDecimal> currentUserRatingProperty = new SimpleObjectProperty<>(null);

	private StringProperty labelTextProperty = new SimpleStringProperty();
	private StringProperty bodyTextProperty = new SimpleStringProperty();

	private int depthLevel;
	
	private final SharedForumState sharedForumState;
	
	private final Consumer<Integer> deleteCallback;
	private final Consumer<IPost> addCallBack;
	private final TriConsumer<Integer, String, String> updateCallBack;
	private final Runnable toggleExpandCallback;
	
	private final int ratingStarCount = 5;

	public enum FilterType {
		TIME
	}

	public PostDisplayViewModel(
			IPost post, 
			List<String> permissionList, 
			UserSession userSession,
			IUserService userService,
			IAnswerService answerService,
			SharedForumState sharedForumState,
			Consumer<Integer> deleteCallBack, 
			Consumer<IPost> addCallBack,
			TriConsumer<Integer, String, String> updateCallBack, 
			Runnable toggleExpandCallback
	) {
		this.post = post;
		this.permissionList = permissionList;
		this.userSession = userSession;
		this.userService = userService;
		this.answerService = answerService;
		this.sharedForumState = sharedForumState;
		this.deleteCallback = deleteCallBack;
		this.addCallBack = addCallBack;
		this.updateCallBack = updateCallBack;
		this.toggleExpandCallback = toggleExpandCallback;

		bodyTextProperty.set(post.getBodyText());

		if (isQuestion()) {
			labelTextProperty.set(((Question) post).getTitleText());
		} else {
			setRatingProperty(((Answer)post).getAverageRating());
			setUpvoteCountProperty(((Answer)post).getUpvoteCount());
		}
		
		try {
		    if (post.getPostType() == Type.Review) {
		        int currentUserId = userSession.getCurrentUserId();
		        int postUserId = post.getAuthorId();
		        
		        boolean favoredReviewerStatus = userService.checkIfFavoredReviewerExists(currentUserId, postUserId);
		        setIsFavoredReviewerProperty(favoredReviewerStatus);
		        
		        setShouldDisplayFavoredReviewerButton();
		    }
		} catch (DataAccessException e) {
		    System.out.println(e.getLocalizedMessage());
		}

	}

	// ===================
	// GETTERS
	// ===================

	public IPost getPost() {
		return post;
	}
	
	public int getNumberOfRatingStars() {
		return ratingStarCount;
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
	
	public BooleanProperty getIsFavoredReviewerProperty() {
		return sharedForumState.getIsFavoredReviewerProperty();
	}
	
	public BooleanProperty getShouldDisplayFavoredReviewerButtonProperty() {
		return shouldDisplayFavoredReviewerButtonProperty;
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
	
	public ObjectProperty<BigDecimal> getRatingProperty() {
	    return ratingProperty;
	}
	
	public BigDecimal getStarRating() {
 		BigDecimal starsToFillRating = getCurrentUserRating() != null ? currentUserRatingProperty.get() : ratingProperty.get();

		return starsToFillRating.multiply(new BigDecimal(ratingStarCount));
	}
	
	public IntegerProperty getUpvoteCountProperty() {
	    return upvoteCountProperty;
	}
	
	public BigDecimal getCurrentUserRating() {
		System.out.println("Post ID: " + post.getId());
		System.out.println("Current user rating: " + currentUserRatingProperty.get());
		return currentUserRatingProperty.get();
	}
	
	public boolean isRatedByUser() {
		return getCurrentUserRating() != null;
	}

	// ===================
	// SETTERS
	// ===================

	public void setDoesHaveChildrenProperty(boolean doesHaveChildren) {
		this.doesHaveChildrenProperty.set(doesHaveChildren);
	}
	
	public void setShouldDisplayFavoredReviewerButton() {
	    boolean isReviewType = post.getPostType() == Type.Review;
	    boolean isCurrentUserAuthor = userSession.getCurrentUserId() == post.getAuthorId();

	    boolean shouldDisplay = isReviewType && !isCurrentUserAuthor;
	    shouldDisplayFavoredReviewerButtonProperty.set(shouldDisplay);
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
	
	public void setIsFavoredReviewerProperty(boolean isFavored) {
		sharedForumState.setIsFavoredReviewerProperty(isFavored);
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
	
	// Building rating staight from the database-style rating 0.0-1.0
	public void setRatingProperty(BigDecimal newRating) {
	    ratingProperty.set(newRating);
	}
	
	// Building rating from the UI Style, IE: 1-5 for stars
	public void setRatingProperty(int starSelected) {
		BigDecimal newRating = BigDecimal.valueOf(starSelected).divide(BigDecimal.valueOf(ratingStarCount), 2, RoundingMode.HALF_UP);
		BigDecimal currentRating = currentUserRatingProperty.get();
		
		try {
			if(newRating == currentRating) {
				answerService.removeRating(post.getId(), userSession.getCurrentUserId());
			} else {
				answerService.updateRating(post.getId(), userSession.getCurrentUserId(), newRating);
				setRatingProperty(newRating);
				currentUserRatingProperty.set(newRating);
			}
		} catch(DataAccessException e) {
			System.out.println(e.getLocalizedMessage());
		}
		setRatingProperty(newRating);
	}
	
	public void setUpvoteCountProperty(int upvoteCount) {
		upvoteCountProperty.set(upvoteCount);
	}
	
	public void resetRating() {
		setRatingProperty(((Answer)post).getAverageRating());
	}
	
	public void setUserDefinedRating() {		
		int answerId = post.getId();
		BigDecimal cachedRating = AppCache.getInstance().getRatingForAnswer(answerId);
		
		if(cachedRating == null) {
			BigDecimal retrievedRating = answerService.getRating(answerId, userSession.getCurrentUserId());
			AppCache.getInstance().setRatingForAnswer(answerId, retrievedRating);
			currentUserRatingProperty.set(retrievedRating);
		} else {
			currentUserRatingProperty.set(cachedRating);
		}
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

	public void setRating(int newRating) {
		
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
	
	public void toggleFavoredReviewerStatus(boolean newStatus) {
		try {
			userService.toggleIsFavoredReviewer(userSession.getCurrentUserId(), post.getAuthorId());
		} catch(DataAccessException e) {
			System.out.println(e.getLocalizedMessage());
		}
		
		setIsFavoredReviewerProperty(newStatus);
	}
	
	// Properties that cannot be active at the same time. 
	// IE: Commenting and Reviewing windows cannot both be open at once
	private void resetNonConcurrentActions() {
		nonConcurrentProperties().forEach(property -> {
			property.set(false);
		});
	}
}